package cn.bugstack.domain.strategy.service.armory;

import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author fuzhouling
 * @date 2024/08/14
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Slf4j
@Service
public class StrategyArmoryDispatch implements IStrategyArmory,IStrategyDispatch{
    @Resource
    private IStrategyRepository repository;
    @Resource
    private IActivityRepository activityRepository;

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);
        assembleLotteryStrategy(String.valueOf(strategyId),strategyAwardEntities);

        for(StrategyAwardEntity strategyAwardEntity : strategyAwardEntities){
            //优先去db中查奖品、策略的库存，然后把库存放到redis中线进行加载
            assembleLotteryStrategy(strategyId, strategyAwardEntity.getAwardId(),strategyAwardEntity.getAwardCount());
        }


        //2、权重策略配置 - 适用于 rule_weight 权重规则配置
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        //这一块也需要对strategyEntity进行判断，因为我如果没有查到数据的话直接就是null了，下面又会报空指针
        String ruleWeight = strategyEntity.getRuleModels();
        if(null == ruleWeight) return true;
        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRule(strategyId,ruleWeight);
        if(null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WIGHT_IS_NULL.getCode(),ResponseCode.STRATEGY_RULE_WIGHT_IS_NULL.getInfo());
        }
        Map<String, List<Integer>> ruleValueMap =  strategyRuleEntity.getRuleValues();
        Set<String> keys = ruleValueMap.keySet();
        for (String key : keys){
            //4000:102,103,104,105 单个value --- 4000 102 103 104 105
            List<Integer> ruleWeightValues = ruleValueMap.get(key);
            //TODO 后续可以学习一下这种处理方法 lambada -- 为什么要这个？ 这里就是克隆一个对象，然后不属于当前策略的移除掉
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(entity -> !ruleWeightValues.contains(entity.getAwardId()));
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key), strategyAwardEntitiesClone);
        }
        return true;
    }

    @Override
    public boolean assembleLotteryStrategyByActivityId(Long activityId) {
        //这块方法会默认先从redis中读取对应活动实体
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        return assembleLotteryStrategy(activityEntity.getStrategyId());
    }

    public void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities) {
       if(strategyAwardEntities == null||strategyAwardEntities.isEmpty())return;
        //获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        //获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //用 1 % 0.0001 获得概率范围，百分位、千分位、万分位
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);
//        BigDecimal rateRange = BigDecimal.valueOf(convert(minAwardRate.doubleValue()));

        //TODO ！！（补充学习） 生成策略奖品概率查找表「这里指需要在list集合中，存放上对应的奖品占位即可，占位越多等于概率越高」--后面再看看
        List<Integer> strategyAwardSearchRateTables = new ArrayList<>(totalAwardRate.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            // 计算出每个概率值需要存放到查找表的数量，循环填充
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        //对存储的奖品进行乱序操作。避免顺序生成的随机数前面是固定的奖品。
        Collections.shuffle(strategyAwardSearchRateTables);

        //生成出Map集合，key值，对应的就是后续的概率值。通过概率来获得对应的奖品ID
        Map<Integer, Integer> shuffleStrategyAwardSearchRateTable = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTables.get(i));
        }

        //存放到 Redis
        repository.storeStrategyAwardSearchRateTable(key,
                shuffleStrategyAwardSearchRateTable.size(), shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rateRange = repository.getRateRange(strategyId);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(String.valueOf(strategyId), new SecureRandom().nextInt(rateRange));

    }

    @Override
    public Integer getRandomAwardId(Long strategyId,String ruleWeightValue){
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rateRange = repository.getRateRange(key);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(rateRange));

    }

    public void assembleLotteryStrategy(Long strategyId, Integer awardId, Integer awardCount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        repository.cacheStrategyAwardCount(cacheKey, awardCount);
        log.info("策略奖品库存初始化成功，策略ID：{}，奖品ID：{}，库存：{}，缓存key：{}", strategyId, awardId, awardCount,cacheKey);

    }
    @Override
    public boolean subtractAwardStock(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY+strategyId+Constants.UNDERLINE+ awardId;
        return repository.subtractAwardStock(cacheKey);
    }
}

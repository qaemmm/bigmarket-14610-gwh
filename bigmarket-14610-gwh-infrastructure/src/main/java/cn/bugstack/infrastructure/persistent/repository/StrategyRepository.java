package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.infrastructure.persistent.dao.IStrategyAwardDao;
import cn.bugstack.infrastructure.persistent.dao.IStrategyDao;
import cn.bugstack.infrastructure.persistent.dao.IStrategyRuleDao;
import cn.bugstack.infrastructure.persistent.po.Strategy;
import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import cn.bugstack.infrastructure.persistent.po.StrategyRule;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import org.checkerframework.checker.units.qual.C;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/08/14
 * @program bigmarket-14610-gwh
 * @description 策略服务仓储实现
 **/
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;
    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY+ strategyId;
        List<StrategyAwardEntity> strategiesAwardEntities =  redisService.getValue(cacheKey);
        if(null!= strategiesAwardEntities&&!strategiesAwardEntities.isEmpty()){
            return strategiesAwardEntities;
        }
        //查询数据库
        List<StrategyAward> strategyAwards = strategyAwardDao.getStrategyListByStrategyId(strategyId);
        strategiesAwardEntities = new ArrayList<>();
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardId(strategyAward.getAwardId())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            strategiesAwardEntities.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey,strategiesAwardEntities);
        return strategiesAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(String key, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+ key, rateRange);
        Map<Integer,Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, Integer rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, rateKey);
    }

    @Override
    public int getRateRange(String key) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public int getRateRange(Long StragegyId) {

        return getRateRange(String.valueOf(StragegyId));
    }

    @Resource
    private IStrategyDao strategyDao;

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        Strategy strategy =  strategyDao.queryStrategyByStrategyId(strategyId);
        if(null!=strategy){
            StrategyEntity strategyEntity = new StrategyEntity();
            strategyEntity.setRuleModels(strategy.getRuleModels());
            strategyEntity.setStrategyDesc(strategyEntity.getStrategyDesc());
            strategyEntity.setStrategyId(strategyEntity.getStrategyId());
            return strategyEntity;
        }
        return null;
    }

    @Resource
    private IStrategyRuleDao strategyRuleDao;
    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleWeight) {
        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(strategyId,ruleWeight);
        if(null!=strategyRule){
            StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
            strategyRuleEntity.setRuleDesc(strategyRule.getRuleDesc());
            strategyRuleEntity.setRuleModel(strategyRule.getRuleModel());
            strategyRuleEntity.setRuleType(strategyRule.getRuleType());
            strategyRuleEntity.setRuleValue(strategyRule.getRuleValue());
            strategyRuleEntity.setStrategyId(strategyRule.getStrategyId());
            return strategyRuleEntity;
        }
        return null;
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(ruleModel);
        strategyRule.setAwardId(awardId);
        return strategyRuleDao.queryStrategyRuleValue(strategyRule);
    }
}

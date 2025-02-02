package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.award.model.valobj.RuleWeightVO;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.model.valobj.*;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.infrastructure.dao.IRaffleActivityAccountDayDao;
import cn.bugstack.infrastructure.dao.IRaffleActivityDao;
import cn.bugstack.infrastructure.dao.IRuleTreeDao;
import cn.bugstack.infrastructure.dao.IRuleTreeNodeDao;
import cn.bugstack.infrastructure.dao.IRuleTreeNodeLineDao;
import cn.bugstack.infrastructure.dao.IStrategyAwardDao;
import cn.bugstack.infrastructure.dao.IStrategyDao;
import cn.bugstack.infrastructure.dao.IStrategyRuleDao;
import cn.bugstack.infrastructure.dao.po.RaffleActivityAccountDay;
import cn.bugstack.infrastructure.dao.po.RuleTree;
import cn.bugstack.infrastructure.dao.po.RuleTreeNode;
import cn.bugstack.infrastructure.dao.po.RuleTreeNodeLine;
import cn.bugstack.infrastructure.dao.po.Strategy;
import cn.bugstack.infrastructure.dao.po.StrategyAward;
import cn.bugstack.infrastructure.dao.po.StrategyRule;
import cn.bugstack.infrastructure.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author fuzhouling
 * @date 2024/08/14
 * @program bigmarket-14610-gwh
 * @description 策略服务仓储实现
 **/
@Slf4j
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;
    @Resource
    private IRedisService redisService;
    @Resource
    private IRaffleActivityDao raffleActivityDao;
    @Resource
    private IRaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY+ strategyId;
        List<StrategyAwardEntity> strategiesAwardEntities =  redisService.getValue(cacheKey);
        if(null!= strategiesAwardEntities&&!strategiesAwardEntities.isEmpty()){
            log.info("从缓存中获取策略奖品: strategiesAwardEntities：{}", strategiesAwardEntities);
            log.info("从缓存中获取策略奖品: strategiesAwardEntities的类型：{}", strategiesAwardEntities.getClass());
            return strategiesAwardEntities;
        }
        //查询数据库
        List<StrategyAward> strategyAwards = strategyAwardDao.getStrategyListByStrategyId(strategyId);
        strategiesAwardEntities = new ArrayList<>();
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .awardCount(strategyAward.getAwardCount())
                    .awardTitle(strategyAward.getAwardTitle())
                    .awardSubtitle(strategyAward.getAwardSubtitle())
                    .sort(strategyAward.getSort())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardId(strategyAward.getAwardId())
                    .awardRate(strategyAward.getAwardRate())
                    .ruleModels(strategyAward.getRuleModels())
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
        String cacheKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key;
        //在通过缓存获取抽奖范围值时，如果忘记初始化策略到缓存中会抛异常。所以新增加了判断代码，增强健壮性。
        if (!redisService.isExists(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key)) {
            throw new AppException(ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getCode(), cacheKey + Constants.COLON + ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getInfo());
        }
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public int getRateRange(Long StrategyId) {
        return getRateRange(String.valueOf(StrategyId));
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
        StrategyRule strategyRuleReq = new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleWeight);
        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(strategyRuleReq);
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



    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModel(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        String ruleModel = strategyAwardDao.queryStrategyAwardRuleModel(strategyAward);
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = StrategyAwardRuleModelVO.builder()
                .ruleModels(ruleModel)
                .build();
        return strategyAwardRuleModelVO;
    }

    @Resource
    private IRuleTreeDao ruleTreeDao;

    @Resource
    private IRuleTreeNodeDao ruleTreeNodeDao;

    @Resource
    private IRuleTreeNodeLineDao ruleTreeNodeLineDao;

    // TODO ！！ 这一块代码是构建一个决策树，有时间可以拿出来手写一遍
    @Override
    public RuleTreeVO queryRuleTreeVoByTreeId(String treeId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = redisService.getValue(cacheKey);
        if (null != ruleTreeVOCache) {
            return ruleTreeVOCache;
        }

        // 从数据库获取
        RuleTree ruleTree = ruleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId(treeId);

        // 1. tree node line 转换Map结构
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();

            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = ruleTreeNodeLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }

        // 2. tree node 转换为Map结构
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(ruleTreeNode.getTreeId())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .treeNodeLineVOList(ruleTreeNodeLineMap.get(ruleTreeNode.getRuleKey()))
                    .build();
            treeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }

        // 3. 构建 Rule Tree
        RuleTreeVO ruleTreeVODB = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .treeRootRuleNode(ruleTree.getTreeNodeRuleKey())
                .treeNodeMap(treeNodeMap)
                .build();

        redisService.setValue(cacheKey, ruleTreeVODB);
        return ruleTreeVODB;
    }

    @Override
    public void cacheStrategyAwardCount(String cacheKey, Integer awardCount) {
        //这里是做缓存预热地，先从缓存中获取对应地值，如果有值就不做处理，如果没有就设置值
        if (null != redisService.getValue(cacheKey)) {
            return;
        }
        redisService.setAtomicLong(cacheKey, awardCount);
        log.info("策略奖品库存初始化成功 缓存key：{},库存：{}，", cacheKey, awardCount);
    }

    @Override
    public boolean subtractAwardStock(String cacheKey) {
        // 检查库存值是否为整数
        long stockCount;

        // 扣减库存
        try {
            stockCount = redisService.decr(cacheKey);
        } catch (Exception e) {
            log.error("执行DECR命令时出错，cacheKey: {}, error: {}", cacheKey, e.getMessage());
            return false;
        }
        if (stockCount < 0) {
            redisService.setValue(cacheKey, 0);
            return false;
        }
        // 1. 按照cacheKey decr 后的值，如 99、98、97 和 key 组成为库存锁的key进行使用。
        // 2. 加锁为了兜底，如果后续有恢复库存，手动处理等，也不会超卖。因为所有的可用库存key，都被加锁了
        String lockKey = cacheKey + Constants.UNDERLINE + stockCount;
        Boolean lock = redisService.setNx(lockKey);
        if (!lock) {
            log.info("策略奖品库存加锁失败{}", lockKey);
        }
        return lock;
    }

    //延迟队列数据存放缓存处理。
    @Override
    public void awardStockConsumeSendQueue(StrategyAwardStockVO strategyAwardStockVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY ;
        RBlockingQueue<StrategyAwardStockVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<StrategyAwardStockVO> delayQueue = redisService.getDelayedQueue(blockingQueue);
        //todo ！！（补充学习） 这一块的延时队列起到什么样的效果？
        delayQueue.offer(strategyAwardStockVO,3, TimeUnit.SECONDS);
    }

    @Override
    @Deprecated
    public StrategyAwardStockVO takeQueue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY ;
        RBlockingQueue<StrategyAwardStockVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
       //这里使用poll就可以了，不需要用take阻塞等待，因为这里是延迟队列，已经有了延迟时间。
        return blockingQueue.poll();
    }

    @Override
    public StrategyAwardStockVO takeQueue(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY+Constants.UNDERLINE+strategyId+Constants.UNDERLINE+awardId ;
        RBlockingQueue<StrategyAwardStockVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        //这里使用poll就可以了，不需要用take阻塞等待，因为这里是延迟队列，已经有了延迟时间。
        return blockingQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        strategyAwardDao.updateStrategyAwardStock(strategyAward);
    }

    @Override
    public StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY+ strategyId+Constants.UNDERLINE+awardId;
        StrategyAwardEntity strategyAwardEntity =  redisService.getValue(cacheKey);
        if(null!= strategyAwardEntity){
            return strategyAwardEntity;
        }
        // 查询数据
        StrategyAward strategyAwardReq = new StrategyAward();
        strategyAwardReq.setStrategyId(strategyId);
        strategyAwardReq.setAwardId(awardId);

        StrategyAward strategyAwardRes = strategyAwardDao.queryStrategyAwardEntity(strategyAwardReq);
        // 转换数据
         strategyAwardEntity = StrategyAwardEntity.builder()
                .strategyId(strategyAwardRes.getStrategyId())
                .awardId(strategyAwardRes.getAwardId())
                .awardTitle(strategyAwardRes.getAwardTitle())
                .awardSubtitle(strategyAwardRes.getAwardSubtitle())
                .awardCount(strategyAwardRes.getAwardCount())
                .awardCountSurplus(strategyAwardRes.getAwardCountSurplus())
                .awardRate(strategyAwardRes.getAwardRate())
                .sort(strategyAwardRes.getSort())
                .build();

        redisService.setValue(cacheKey,strategyAwardEntity);

        return strategyAwardEntity;
    }

    @Override
    public Integer queryTodayUserRaffleCount(String userId, Long strategyId) {
        Long activityId = raffleActivityDao.queryActivityIdByStrategyId(strategyId);
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setUserId(userId);
        raffleActivityAccountDay.setActivityId(activityId);
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        RaffleActivityAccountDay raffleActivityAccountDayRes = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(raffleActivityAccountDay);
        if(null==raffleActivityAccountDayRes)return 0;
        //通过今天的总的-剩余的次数，
        return raffleActivityAccountDayRes.getDayCount()-raffleActivityAccountDayRes.getDayCountSurplus();
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
       Long strategyId =  raffleActivityDao.queryStrategyIdByActivityId(activityId);
        return strategyId;
    }

    @Override
    public Map<String, Integer> getRuleValueByTreeIds(String[] treeIds) {
        if(null==treeIds||treeIds.length==0){
            return Collections.emptyMap();
        }
        Map<String,Integer> ruleValueMap = new HashMap<>();
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleTreeNodeListByTreeIds(treeIds);
        for(RuleTreeNode ruleTreeNode:ruleTreeNodes){
            ruleValueMap.put(ruleTreeNode.getTreeId(),Integer.parseInt(ruleTreeNode.getRuleValue()));
        }
        return ruleValueMap;
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeight(Long strategyId) {
        String cacheKey =Constants.RedisKey.STRATEGY_RULE_WEIGHT_KEY + strategyId;
        List<RuleWeightVO> ruleWeightVOS = redisService.getValue(cacheKey);
        if(null!=ruleWeightVOS)return ruleWeightVOS;
        ruleWeightVOS = new ArrayList<>();
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(DefaultChainFactory.LogicModel.RULE_WIGHT.getCode());
        String ruleValue = strategyRuleDao.queryStrategyRuleValue(strategyRule);
        StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
        strategyRuleEntity.setStrategyId(strategyId);
        strategyRuleEntity.setRuleModel(DefaultChainFactory.LogicModel.RULE_WIGHT.getCode());
        strategyRuleEntity.setRuleValue(ruleValue);
        Map<String, List<Integer>> ruleWeightValues = strategyRuleEntity.getRuleValues();
        // 3. 遍历规则组装奖品配置
        Set<String> ruleWeightKeys = ruleWeightValues.keySet();
        for (String ruleWeightKey : ruleWeightKeys) {
            List<Integer> awardIds = ruleWeightValues.get(ruleWeightKey);
            List<RuleWeightVO.Award> awardList = new ArrayList<>();
            // 也可以修改为一次从数据库查询
            for (Integer awardId : awardIds) {
                StrategyAward strategyAwardReq = new StrategyAward();
                strategyAwardReq.setStrategyId(strategyId);
                strategyAwardReq.setAwardId(awardId);
                StrategyAward strategyAward = strategyAwardDao.queryStrategyAward(strategyAwardReq);
                awardList.add(RuleWeightVO.Award.builder()
                        .awardId(strategyAward.getAwardId())
                        .awardTitle(strategyAward.getAwardTitle())
                        .build());
            }

            ruleWeightVOS.add(RuleWeightVO.builder()
                    .ruleValue(ruleValue)
                    .weight(Integer.valueOf(ruleWeightKey.split(Constants.COLON)[0]))
                    .awardIds(awardIds)
                    .awardList(awardList)
                    .build());
        }
        redisService.setValue(cacheKey,ruleWeightVOS);
        return ruleWeightVOS;
    }

    @Override
    public List<StrategyAwardStockKeyVO> queryOpenActivityStrategyAwardList() {
        String cacheKey = Constants.RedisKey.OPEN_ACTIVITY_STRATEGY_AWARD_LIST ;
        List<StrategyAwardStockKeyVO> strategyAwardStockKeyVOS= redisService.getValue(cacheKey);
        if (null != strategyAwardStockKeyVOS)return strategyAwardStockKeyVOS;

        List<StrategyAward> strategyAwards = strategyAwardDao.queryOpenActivityStrategyAwardList();
        if (null == strategyAwards || strategyAwards.isEmpty()) {
            return Collections.emptyList();
        }
        strategyAwardStockKeyVOS = new ArrayList<>();
        for (StrategyAward strategyAward: strategyAwards){
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = StrategyAwardStockKeyVO.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .build();
            strategyAwardStockKeyVOS.add(strategyAwardStockKeyVO);
        }
        redisService.setValue(cacheKey,strategyAwardStockKeyVOS);
        return strategyAwardStockKeyVOS;
    }


}

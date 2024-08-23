package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleLimitTypeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.infrastructure.persistent.dao.IRuleTreeDao;
import cn.bugstack.infrastructure.persistent.dao.IRuleTreeNodeDao;
import cn.bugstack.infrastructure.persistent.dao.IRuleTreeNodeLineDao;
import cn.bugstack.infrastructure.persistent.dao.IStrategyAwardDao;
import cn.bugstack.infrastructure.persistent.dao.IStrategyDao;
import cn.bugstack.infrastructure.persistent.dao.IStrategyRuleDao;
import cn.bugstack.infrastructure.persistent.po.RuleTree;
import cn.bugstack.infrastructure.persistent.po.RuleTreeNode;
import cn.bugstack.infrastructure.persistent.po.RuleTreeNodeLine;
import cn.bugstack.infrastructure.persistent.po.Strategy;
import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import cn.bugstack.infrastructure.persistent.po.StrategyRule;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // TODO 这一块代码看不懂啊
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
//        log.info("策略奖品库存扣减后剩余库存{}", cacheKey);
        //扣减库存，需要对库存进行校验
        // 获取当前库存值
        Integer stockValue = redisService.getValue(cacheKey);
        // 检查库存值是否为整数
        long stockCount;
        try {
            stockCount = stockValue;
            log.info("库存值为整数，cacheKey: {}, value: {},stockCount:{}", cacheKey, stockValue,stockCount);
        } catch (NumberFormatException e) {
            log.error("库存值不是整数，cacheKey: {}, value: {}", cacheKey, stockValue);
            return false;
        }
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
        delayQueue.offer(strategyAwardStockVO,3, TimeUnit.SECONDS);
    }

    @Override
    public StrategyAwardStockVO takeQueue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY ;
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


}

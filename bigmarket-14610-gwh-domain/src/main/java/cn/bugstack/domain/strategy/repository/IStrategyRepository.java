package cn.bugstack.domain.strategy.repository;

import cn.bugstack.domain.award.model.valobj.RuleWeightVO;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockVO;

import java.util.List;
import java.util.Map;

/**
 * @author guoweihong
 * @description 策略服务仓储接口
 * @create 2023-12-23 09:33
 */

public interface IStrategyRepository {

    //遍历所有的策略中奖集合实体，通过策略id
     List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    //通过策略id将中奖范围和乱序后的结果表存储好
    void storeStrategyAwardSearchRateTable(String key, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    //通过生成的随机值，获取概率值奖品查找表的结果
    Integer getStrategyAwardAssemble(String key,Integer rateKey);

    //分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
    int getRateRange(String key);

    int getRateRange(Long strategyId);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleWeight);

    String queryStrategyRuleValue(Long strategyId,String ruleModel);


    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModel(Long strategyId, Integer awardId);

    RuleTreeVO queryRuleTreeVoByTreeId(String treeId);

    /**
     * 提前将库存丢入redis中进行预热
     * */
     void cacheStrategyAwardCount(String cacheKey, Integer awardCount);

    boolean subtractAwardStock(String cacheKey);

    void awardStockConsumeSendQueue(StrategyAwardStockVO strategyAwardStockVO);

    StrategyAwardStockVO takeQueue();

    StrategyAwardStockVO takeQueue(Long strategyId, Integer awardId);

    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId);

    Integer queryTodayUserRaffleCount(String userId, Long strategyId);

    Long queryStrategyIdByActivityId(Long activityId);

    Map<String, Integer> getRuleValueByTreeIds(String[] treeIds);

    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);

    List<StrategyAwardStockKeyVO> queryOpenActivityStrategyAwardList();

    Integer queryActivityAccountTotalUseCount(String userId, Long strategyId);
}

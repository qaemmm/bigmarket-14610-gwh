package cn.bugstack.domain.strategy.service.armory;

public interface IStrategyDispatch {
    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param strategyId 策略ID
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);

    /**
     *
     * 获得特定的ruleWeightValue权重的抽奖结果
     * @param strategyId
     * @param ruleWeightValue :{通常可以如下 4000:102,103,104,105}
     * @return
     */

    Integer getRandomAwardId(Long strategyId,String ruleWeightValue);


    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param strategyId 策略id
     * @param awardId 奖品id
     * @return 抽奖结果
     */
    boolean subtractAwardStock(Long strategyId, Integer awardId);
}

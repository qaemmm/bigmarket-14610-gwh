package cn.bugstack.domain.strategy.service.armory;



public interface IStrategyArmory {

    /**
     * 构建抽奖策略
     *
     * @param strategyId 策略ID
     * @return 是否
     */
    boolean assembleLotteryStrategy(Long strategyId);

    /**
     * 装配抽奖策略配置「触发的时机可以为活动审核通过后进行调用」
     *
     * @param activityId 活动ID
     * @return 装配结果
     */
    boolean assembleLotteryStrategyByActivityId(Long activityId);


}

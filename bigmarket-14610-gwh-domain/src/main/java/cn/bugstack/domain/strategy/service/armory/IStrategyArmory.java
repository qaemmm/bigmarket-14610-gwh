package cn.bugstack.domain.strategy.service.armory;



public interface IStrategyArmory {

    /**
     * 构建抽奖策略
     *
     * @param strategyId 策略ID
     * @return 是否
     */
    boolean assembleLotteryStrategy(Long strategyId);



}

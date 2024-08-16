package cn.bugstack.domain.strategy.service.armory;



public interface IStrategyArmory {

    /**
     * 构建彩票策略
     *
     * @param strategyId 策略ID
     * @return 是否
     */
    boolean assembleLotteryStrategy(Long strategyId);



}

package cn.bugstack.domain.strategy.service.rule.chain;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 责任链接口
 **/
public interface ILogicChain extends ILogicChainArmory{
    /**
     * 责任链接口
     * @param userId 用户ID
     * @param strategyId 策略ID
     * @return 抽奖结果的奖品id
     */
    Integer logic(String userId,Long strategyId);
}

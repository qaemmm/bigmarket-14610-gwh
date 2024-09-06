package cn.bugstack.domain.strategy.service.rule.chain;

import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 责任链接口
 **/
// --这里继承的好处是什么？  -- 单一的职责，不会出现多个职责的混乱，同时也可以通过继承的方式，将多个职责组合在一起
public interface ILogicChain extends ILogicChainArmory{
    /**
     * 责任链接口
     * @param userId 用户ID
     * @param strategyId 策略ID
     * @return 抽奖结果的奖品id
     */
    DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId);

}

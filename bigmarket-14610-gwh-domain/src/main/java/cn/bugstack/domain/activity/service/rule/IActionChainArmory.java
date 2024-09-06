package cn.bugstack.domain.activity.service.rule;

import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
public interface IActionChainArmory {
    IActionChain next();

    IActionChain appendNext(IActionChain next);
}

package cn.bugstack.domain.activity.service.quota.rule;

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

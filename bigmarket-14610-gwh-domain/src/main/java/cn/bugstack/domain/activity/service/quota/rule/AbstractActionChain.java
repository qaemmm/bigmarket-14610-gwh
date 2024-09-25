package cn.bugstack.domain.activity.service.quota.rule;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description 下单规则责任链抽象类
 **/
public abstract class AbstractActionChain implements IActionChain {
    private IActionChain next;

    @Override
    public IActionChain appendNext(IActionChain next) {
        this.next = next;
        return next;
    }

    @Override
    public IActionChain next() {
        return next;
    }

}

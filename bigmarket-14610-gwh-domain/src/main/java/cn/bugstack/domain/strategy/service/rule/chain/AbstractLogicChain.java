package cn.bugstack.domain.strategy.service.rule.chain;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
public abstract class AbstractLogicChain implements ILogicChain{

    private ILogicChain next;

    @Override
    public ILogicChain appendNext(ILogicChain next){
        this.next = next;
        return next;
    }

    @Override
    public ILogicChain next(){
        return next;
    }


}

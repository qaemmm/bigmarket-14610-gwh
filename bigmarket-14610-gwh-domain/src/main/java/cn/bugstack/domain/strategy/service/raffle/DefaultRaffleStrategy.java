package cn.bugstack.domain.strategy.service.raffle;

import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.AbstractRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/08/16
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Component
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {


    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory chainFactory, DefaultTreeFactory treeFactory) {
       super(repository,strategyDispatch,chainFactory,treeFactory);
    }


    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain iLogicChain = chainFactory.openLogicChain(strategyId);
        DefaultChainFactory.StrategyAwardVO strategyAwardVO =  iLogicChain.logic(userId, strategyId);
        return strategyAwardVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        //这一块要干嘛? queryStrategyAwardRuleModel
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModel(strategyId, awardId);
        if(null == strategyAwardRuleModelVO){
            return DefaultTreeFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .build();
        }
        String treeId = strategyAwardRuleModelVO.getRuleModels();
        //去哪里获取treeId == queryStrategyAwardRuleModel
        RuleTreeVO ruleTreeVO = repository.queryRuleTreeVoByTreeId(treeId);
        if(null==ruleTreeVO){
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 "
                    + strategyAwardRuleModelVO.getRuleModels());
        }

        IDecisionTreeEngine iDecisionTreeEngine = treeFactory.openLogicTree(ruleTreeVO);
        return iDecisionTreeEngine.process(userId, strategyId, awardId);

    }
}

package cn.bugstack.domain.strategy.service.rule.tree.factory.engine.impl;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeNodeVO;
import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 决策树引擎
 **/
@Slf4j
//@Service 这一块确实不用加，因为在DefaultTreeFactory中已经加了 new DecisionTreeEngine通过构造函数注入
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }


    //todo 代码忘了，不知道这么process，是干嘛的？
    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardVO = null;
        // 获取基础信息
        String nextNode = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();
        // 获取起始节点「根节点记录了第一个要执行的规则」
        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);

        while(null!=nextNode){
            // 获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());
            // 决策节点计算
            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId, ruleTreeNode.getRuleValue());
            RuleLogicCheckTypeVO ruleLogicCheckType = logicEntity.getRuleLogicCheckType();
            strategyAwardVO = logicEntity.getStrategyAwardVO();
            log.info("决策树引擎【{}】treeId:{} node:{} code:{}",
                    ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNode, ruleLogicCheckType.getCode());
            // 获取下个节点
            nextNode = nextNode(ruleLogicCheckType.getCode(), ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode = treeNodeMap.get(nextNode);
        }
        return strategyAwardVO;

    }

    private String nextNode(String code, List<RuleTreeNodeLineVO> treeNodeLineVOList) {
        //1、没有线即没有下一个节点，返回null
        if (null == treeNodeLineVOList || treeNodeLineVOList.isEmpty()) return null;
        //2、遍历线，找到符合条件的线
        for (RuleTreeNodeLineVO nodeLine : treeNodeLineVOList) {
            if (decisionLogic(code, nodeLine)) {
                return nodeLine.getRuleNodeTo();//返回线的终点，即下一个节点
            }
        }
        //执行到这里，说明有线连接到下一个节点，但是每个节点都无法到达，属于特殊情况，直接抛异常
          //  throw new RuntimeException("决策树引擎，nextNode计算失败，未找到可执行的节点！");
        return null;
    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine){
        switch (nodeLine.getRuleLimitType()){
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
                //下面暂时不需要使用
            case GT:
            case LT:
            case GE:
            case LE:
            default:return false;
        }
    }
}

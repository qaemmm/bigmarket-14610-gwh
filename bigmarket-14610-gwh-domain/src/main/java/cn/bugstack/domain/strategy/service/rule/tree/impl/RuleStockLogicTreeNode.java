package cn.bugstack.domain.strategy.service.rule.tree.impl;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 库存节点
 **/
@Slf4j
@Component("rule_stock")
public class RuleStockLogicTreeNode implements ILogicTreeNode{

    @Resource
    private IStrategyRepository strategyRepository;

    @Resource
    private IStrategyDispatch strategyDispatch;
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId,String ruleValue) {
        log.info("库存节点{} {} {}", userId, strategyId, awardId);
        //扣减库存
        boolean status = strategyDispatch.subtractAwardStock(strategyId, awardId);
        //库存扣减成功后发送消息
        if(status){
            //写入延时队列，延迟消费更新数据库记录，在trigger的job；UpdateAwardStockJob下消息队列，更新数据库记录。
            strategyRepository.awardStockConsumeSendQueue(StrategyAwardStockVO.builder()
                    .awardId(awardId)
                    .strategyId(strategyId)
                    .build());
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                    .build();
        }
        // 如果库存不足，则直接返回放行
        log.info("规则过滤-库存扣减-告警，库存不足 userId:{} strategyId:{} awardId:{} "
                ,userId,strategyId,awardId);

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }
}

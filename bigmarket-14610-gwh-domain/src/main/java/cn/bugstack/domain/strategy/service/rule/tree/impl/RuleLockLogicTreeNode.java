package cn.bugstack.domain.strategy.service.rule.tree.impl;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 次数锁
 **/
@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue) {
        //次数锁
        log.info("规则过滤-次数锁 userId:{} strategyId:{} awardId:{} ruleValue:{}", userId, strategyId, awardId, ruleValue);

        long raffleCount = 0L;
        try {
            raffleCount = Long.parseLong(ruleValue);
        } catch (Exception e) {
            throw new RuntimeException("规则过滤-次数锁异常 ruleValue: " + ruleValue + " 配置不正确");
        }
        Integer userRaffleCount = strategyRepository.queryTodayUserRaffleCount(userId, strategyId);
        if (userRaffleCount >= raffleCount) {
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                    .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                            .awardId(awardId)
                            .awardRuleValue(ruleValue)
                            .build())
                    .build();
        }

        //用户抽奖次数小于规则限定值，规则要接管，做兜底处理
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
//                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
//                        .awardId(awardId)
//                        .awardRuleValue(ruleValue)
//                        .build())
                .build();


    }
}

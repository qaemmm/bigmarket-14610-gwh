package cn.bugstack.domain.strategy.service.rule;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @author fustack
 * @param <T>
 *           这个是作为一个抽奖规则的过滤的接口：有权重、黑名单
 *
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {
    /**
     *  传入一个必要过滤的参数实体，返回一个规则实体
     */

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);
}

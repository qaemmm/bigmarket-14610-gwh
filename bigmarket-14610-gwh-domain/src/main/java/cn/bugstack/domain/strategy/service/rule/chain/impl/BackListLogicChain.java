package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.chain.AbstractLogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Slf4j
@Component("rule_blacklist")
public class BackListLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;
    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-黑名单开始 userId: {} strategyId: {} ruleModel: {}",
                userId, strategyId, ruleModel());
        String ruleValue = repository.queryStrategyRuleValue(strategyId, null, ruleModel());
        //todo 需要做校验吗？

        String[] part = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(part[0]);
        String[] BlackUserIds = part[1].split(Constants.SPLIT);
        for (String BlackUserId : BlackUserIds){
            if(StringUtils.equals(userId, BlackUserId)){
                log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
                return DefaultChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .awardRuleValue(ruleModel())
                        .build();
            }
        }
        //过滤其他责任链
        log.info("抽奖责任链-黑名单放行userId:{} strategyId:{} ruleModel:{} ",userId,strategyId,ruleModel());
        return next().logic(userId,strategyId);
    }

    protected String ruleModel(){
        return DefaultChainFactory.LogicModel.RULE_BLACKLIST.getCode();
    }
}

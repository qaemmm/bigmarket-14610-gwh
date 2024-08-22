package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.AbstractLogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;

    @Resource
    private IStrategyDispatch strategyDispatch;

    public Long userScore = 0L;



    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重规则开始 userId: {} strategyId: {} ruleModel: {}",
                userId, strategyId, ruleModel());
        String ruleValue = repository.queryStrategyRuleValue(strategyId, null, ruleModel());
        Map<Long,String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if(null == analyticalValueGroup||analyticalValueGroup.isEmpty()){
            return null;
        }

        // 2. 转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);
        Long nextValue = null;
        // 3. 找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、
        // 【5000 积分，能找到 5000:102,103,104,105,106,107】
        for (Long key : analyticalSortedKeys){
            if(userScore>=key){
                nextValue = key;
            }
        }
        //获取到范文权重值 4000:102,103,104,105之后调用抽奖的方法strategyDispatch.getRandomAwardId，返回一个奖品id
        if(null!=nextValue){
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, analyticalValueGroup.get(nextValue));
            log.info("抽奖责任链-权重接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .awardRuleValue(ruleModel())
                    .build();
        }

        // 5.否则，过滤其他责任链
        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }


    public String ruleModel(){
        return DefaultChainFactory.LogicModel.RULE_WIGHT.getCode();
    }

    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        Map<Long, String> analyticalValueGroup = new HashMap<>();
        if(null != ruleValue){
            String[] ruleValueGroup = ruleValue.split(Constants.SPACE);
           for(String ruleValueGroupItem : ruleValueGroup){
               if(null==ruleValueGroupItem||ruleValueGroupItem.isEmpty()){
                   continue;
               }
               String[] part = ruleValueGroupItem.split(Constants.COLON);
               if(part.length!=2){
                   throw new IllegalArgumentException("rule_weight rule_rule invalid input format"+ ruleValueGroupItem);
               }
               analyticalValueGroup.put(Long.parseLong(part[0]), ruleValueGroupItem);
           }
        }
        return analyticalValueGroup;
    }

}

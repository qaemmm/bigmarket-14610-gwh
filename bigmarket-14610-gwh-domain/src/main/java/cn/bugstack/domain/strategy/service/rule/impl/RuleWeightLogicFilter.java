package cn.bugstack.domain.strategy.service.rule.impl;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.annotation.LogicStrategy;
import cn.bugstack.domain.strategy.service.rule.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/08/16
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Component
@Slf4j
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository repository;

    public Long userScore = 4500L;

    /**
     * 权重规则过滤：
     * 1、权重规则格式：4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     * 2、解析数据格式：判断那个范围符用户的特定抽奖范围
     * @param ruleMatterEntity 规则物料实体对象
     * @return
     */
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤权重范围：userId:{} strategyId:{} ruleMode:{}",
                ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleValue = repository.queryStrategyRuleValue( strategyId, ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        // 1. 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
        Map<Long,String> analyticalValueGroup = getAnalyzedValue(ruleValue);
        //判断的时候总是取最小的路径（技巧吧）
        if(null == analyticalValueGroup||analyticalValueGroup.isEmpty()){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode()).
                    info(DefaultLogicFactory.LogicModel.RULE_WIGHT.getInfo()).build();
        }

        // 2. 转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        // 3. 找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、【5000 积分，能找到 5000:102,103,104,105,106,107】
       Long nextValue = analyticalSortedKeys.stream()
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);

        if(null!=nextValue){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(strategyId)
                            .ruleWeightValueKey(analyticalValueGroup.get(nextValue))
                                    .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .code(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .info(DefaultLogicFactory.LogicModel.RULE_WIGHT.getInfo())
                   .build();
        }


        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                .info(DefaultLogicFactory.LogicModel.RULE_WIGHT.getInfo()).build();
    }

    //4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
    private Map<Long, String> getAnalyzedValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long,String> ruleValueMap = new HashMap<>();
        for(String ruleValueKey:ruleValueGroups){
            if(ruleValueKey==null|| ruleValueKey.isEmpty()){
                return ruleValueMap;
            }
            String[] parts = ruleValueKey.split(Constants.COLON);
            if(parts.length != 2){
                throw new IllegalStateException("rule_weight rule_rule invalid input format"+ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]),ruleValueKey);
        }
        return ruleValueMap;
    }
}

package cn.bugstack.domain.strategy.service.rule.chain.factory;

import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 抽奖策略责任链工厂
 **/
@CommonsLog
@Service
public class DefaultChainFactory {
    private final Map<String, ILogicChain> logicChainMap;
    protected IStrategyRepository repository;
    public DefaultChainFactory(Map<String, ILogicChain> logicChainMap, IStrategyRepository repository){
        this.logicChainMap = logicChainMap;
        this.repository = repository;
    }

    /**
     * 通过传入strategyId策略id，构建责任链
     */
    public ILogicChain openLogicChain(Long strategyId){
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategyEntity.ruleModels();
        // 如果未配置策略规则，则只装填一个默认责任链
        if(null==ruleModels|| ruleModels.length==0) {
            return logicChainMap.get("default");
        }
        // 按照配置顺序装填用户配置的责任链；rule_blacklist、rule_weight
        // 「注意此数据从Redis缓存中获取，如果更新库表，记得在测试阶段手动处理缓存」
        ILogicChain iLogicChain = logicChainMap.get(ruleModels[0]);
        ILogicChain currentChain = iLogicChain;
        for(int i=1;i<ruleModels.length;i++){
            ILogicChain nextLogicChain = logicChainMap.get(ruleModels[i]);
            currentChain = currentChain.appendNext(nextLogicChain);
        }
        currentChain.appendNext(logicChainMap.get("default"));
        return iLogicChain;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO{
        /**抽奖奖品id - 内部流转使用*/
        private Integer awardId;
        /**
         * 抽奖类型；黑名单抽奖、权重规则、默认抽奖
         */
        private String logicModel;
        /**
         * 抽奖奖品规则
         */
        private String awardRuleValue;
    }


    @Getter
    @AllArgsConstructor
    public enum LogicModel{
        RULE_WIGHT("rule_weight", "权重规则"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_DEFAULT("default", "默认抽奖")
        ;

        private final String code;
        private final String info;

    }
}

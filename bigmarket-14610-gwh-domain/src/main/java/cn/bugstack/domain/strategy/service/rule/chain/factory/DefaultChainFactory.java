package cn.bugstack.domain.strategy.service.rule.chain.factory;

import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description todo desc...
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
        ILogicChain currntChain = iLogicChain;
        for(int i=1;i<ruleModels.length;i++){
            ILogicChain nextLogicChain = logicChainMap.get(ruleModels[i]);
            currntChain = currntChain.appendNext(nextLogicChain);
        }
        currntChain.appendNext(logicChainMap.get("default"));
        return iLogicChain;

    }
}

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 抽奖策略责任链工厂
 **/
@Slf4j
@Service
public class DefaultChainFactory {
    private final ApplicationContext applicationContext;
    private final ConcurrentHashMap<Long, ILogicChain> logicChainMap;
    protected IStrategyRepository repository;
    // 使用ThreadLocal为每个线程提供独立的责任链
    private final ThreadLocal<ILogicChain> threadLocalLogicChain = new ThreadLocal<>();

    public DefaultChainFactory(ApplicationContext applicationContext,IStrategyRepository repository){
        this.applicationContext = applicationContext;
        this.logicChainMap = new ConcurrentHashMap<>();;
        this.repository = repository;
    }

    /**
     * 通过传入strategyId策略id，构建责任链
     * 如果现在线程1进来查到的配置是String[] ruleModels = rule_blacklist、rule_weight。
     * 线程2进来查询配置String[] ruleModels = rule_blacklist、rule_region
     * 由于currentChain.appendNext是单例的，所以这边双线程同步进行追加的时候可能会出现rule_blacklist->rule_region->rule_weight->default，进而导致决策树构建不安全
     */
    public ILogicChain openLogicChain(Long strategyId){

        // 如果当前线程已有责任链实例，则直接返回
        ILogicChain threadLogicChain = threadLocalLogicChain.get();
        if (threadLogicChain != null) {
            return threadLogicChain;
        }

        // 如果当前线程没有责任链实例，则创建并存储
        ILogicChain cacheLogicChain = logicChainMap.get(strategyId);
        if (cacheLogicChain != null) {
            // 如果缓存中存在，则使用缓存的责任链
            threadLocalLogicChain.set(cacheLogicChain);
            return cacheLogicChain;
        }

        //10006策略会查出的rule_model有俩种rule_blacklist、rule_weight。
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategyEntity.ruleModels();
        // 如果未配置策略规则，则只装填一个默认责任链
        if(null==ruleModels|| ruleModels.length==0) {
            ILogicChain iLogicChain = applicationContext.getBean(LogicModel.RULE_DEFAULT.getCode(), ILogicChain.class);
            threadLocalLogicChain.set(iLogicChain);
            return iLogicChain;
        }
        // 按照配置顺序装填用户配置的责任链；rule_blacklist、rule_weight
        // 「注意此数据从Redis缓存中获取，如果更新库表，记得在测试阶段手动处理缓存」
        ILogicChain iLogicChain = applicationContext.getBean(ruleModels[0], ILogicChain.class);
        ILogicChain currentChain = iLogicChain;
        for(int i=1;i<ruleModels.length;i++){
            ILogicChain nextLogicChain = applicationContext.getBean(ruleModels[i], ILogicChain.class);
            currentChain = currentChain.appendNext(nextLogicChain);
        }
        //再末尾再追加一个default
        currentChain.appendNext(applicationContext.getBean(LogicModel.RULE_DEFAULT.getCode(), ILogicChain.class));
        threadLocalLogicChain.set(currentChain);
        logicChainMap.put(strategyId, currentChain);
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

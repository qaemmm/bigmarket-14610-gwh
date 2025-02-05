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
import java.util.*;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 抽奖策略权重规则
 **/
@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;

    @Resource
    private IStrategyDispatch strategyDispatch;

    // 按需选择需要的计算策略，旧版是 AnalyticalNotEqual 算法，新增加 = 算法。使用时可以实例化 AnalyticalEqual 即可。这个操作也可以从数据库中配置处理。
    private final IAnalytical analytical = new AnalyticalNotEqual();

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重规则开始 userId: {} strategyId: {} ruleModel: {}",
                userId, strategyId, ruleModel());

        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());
        // 1. 解析权重规则值 4000:102,103,104,105 拆解为；4000 -> 4000:102,103,104,105 便于比对判断
        Map<Integer,String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if(null == analyticalValueGroup || analyticalValueGroup.isEmpty()){
            log.warn("抽奖责任链-权重告警【策略配置权重，但ruleValue未配置相应值】 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel());
            return next().logic(userId, strategyId);
        }

        //  2. 用户分值
        Integer userScore = repository.queryActivityAccountTotalUseCount(userId, strategyId);

        // 3. 获取权重对应key
        String analyticalValue = analytical.getAnalyticalValue(analyticalValueGroup, userScore);

        //获取到范文权重值 4000:102,103,104,105之后调用抽奖的方法strategyDispatch.getRandomAwardId，返回一个奖品id
        if(null!=analyticalValue){
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId,analyticalValue);
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

    private Map<Integer, String> getAnalyticalValue(String ruleValue) {
        Map<Integer, String> analyticalValueGroup = new HashMap<>();
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
               analyticalValueGroup.put(Integer.parseInt(part[0]), ruleValueGroupItem);
           }
        }
        return analyticalValueGroup;
    }

    interface IAnalytical {
        String getAnalyticalValue(Map<Integer, String> analyticalValueGroup, Integer userScore);
    }

    // 获得指定权重值 = n
    static class AnalyticalEqual implements IAnalytical {

        @Override
        public String getAnalyticalValue(Map<Integer, String> analyticalValueGroup, Integer userScore) {
            return analyticalValueGroup.get(userScore);
        }
    }

    // 获取范围权重值 > n
    static class AnalyticalNotEqual implements IAnalytical {

        @Override
        public String getAnalyticalValue(Map<Integer, String> analyticalValueGroup, Integer userScore) {
            // 2. 转换Keys值，并默认排序
            List<Integer> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
            Collections.sort(analyticalSortedKeys);

            // 3. 找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、【5000 积分，能找到 5000:102,103,104,105,106,107】
            /* 找到最后一个符合的值[如用户传了一个 5900 应该返回正确结果为 5000]，如果使用 Lambda findFirst 需要注意使用 sorted 反转结果
             *   Long nextValue = null;
             *         for (Long analyticalSortedKeyValue : analyticalSortedKeys){
             *             if (userScore >= analyticalSortedKeyValue){
             *                 nextValue = analyticalSortedKeyValue;
             *             }
             *         }
             * 星球伙伴 @慢慢来 ID 6267 提供
             * Long nextValue = analyticalSortedKeys.stream()
             *      .filter(key -> userScore >= key)
             *      .max(Comparator.naturalOrder())
             *      .orElse(null);
             */
            Integer nextValue = analyticalSortedKeys.stream()
                    .sorted(Comparator.reverseOrder())
                    .filter(analyticalSortedKeyValue -> userScore >= analyticalSortedKeyValue)
                    .findFirst()
                    .orElse(null);

            // 返回权重范围的 key值
            return analyticalValueGroup.get(nextValue);
        }
    }

}

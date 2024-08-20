package cn.bugstack.domain.strategy.model.entity;

import cn.bugstack.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author fuzhouling
 * @date 2024/08/16
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyEntity {
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖策略描述
     */
    private String strategyDesc;
    /**
     * 抽奖规则模型
     */
    private String ruleModels;

    public String[] ruleModels() {
        if(StringUtils.isBlank(ruleModels))return null;
        return ruleModels.split(Constants.SPLIT);
    }

    public String getRuleModels() {
        String[] ruleModels = this.ruleModels();
        if(ruleModels == null)return null;
        for (String ruleModel : ruleModels){
            if("rule_weight".equals(ruleModel))return ruleModel;
        }
        return null;
    }
}

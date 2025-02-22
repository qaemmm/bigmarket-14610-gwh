package cn.bugstack.domain.strategy.model.valobj;

import cn.bugstack.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/08/19
 * @program bigmarket-14610-gwh
 * @description 抽奖策略规则规则值对象：值对象，没有唯一id，仅限于从数据库查找对象
 **/
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class StrategyAwardRuleModelVO {
    private String ruleModels;

}

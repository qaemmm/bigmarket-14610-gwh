package cn.bugstack.domain.strategy.model.entity;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/08/16
 * @program bigmarket-14610-gwh
 * @description 规则动作实体:这一块就做了限制，你需要传入一个RuleActionEntity.RaffleEntity的实体，
 * 而这里RaffleBeforeEntity、RaffleCenterEntity、RaffleAfterEntity抽奖前中后都做了约束，使得你丢进来的对象
 * 只能是这个类型的 ---- 主要是做一个约束的规范
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {


    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();
    private String ruleModel;
    private T data;

    static public class RaffleEntity {

    }

    // 抽奖之前
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleBeforeEntity extends RaffleEntity {
        /**
         * 策略ID
         */
        private Long strategyId;

        /**
         * 权重值Key；用于抽奖时可以选择权重抽奖。
         */
        private String ruleWeightValueKey;

        /**
         * 奖品ID；
         */
        private Integer awardId;
    }

    // 抽奖之中
    static public class RaffleCenterEntity extends RaffleEntity {

    }

    // 抽奖之后
    static public class RaffleAfterEntity extends RaffleEntity {

    }


}

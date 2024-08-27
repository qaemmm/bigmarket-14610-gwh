package cn.bugstack.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/08/26
 * @program bigmarket-14610-gwh
 * @description 策略奖品存储值对象
 **/
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class StrategyAwardStockVO {

    private Long strategyId;
    private Integer awardId;
}

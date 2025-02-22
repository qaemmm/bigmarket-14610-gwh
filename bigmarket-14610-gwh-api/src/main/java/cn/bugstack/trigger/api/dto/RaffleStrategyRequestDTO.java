package cn.bugstack.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fuzhouling
 * @date 2024/08/28
 * @program bigmarket-14610-gwh
 * @description 抽奖请求参数
 **/
@Data
public class RaffleStrategyRequestDTO implements Serializable {
    private Long strategyId;
}

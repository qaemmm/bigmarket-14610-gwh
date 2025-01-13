package cn.bugstack.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fuzhouling
 * @date 2024/08/28
 * @program bigmarket-14610-gwh
 * @description 抽奖奖品列表查询请求参数
 **/
@Data
public class RaffleAwardListRequestDTO implements Serializable {
    @Deprecated
    private Long strategyId;
    private String userId;
    private Long activityId;
}

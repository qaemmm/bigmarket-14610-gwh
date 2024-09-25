package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/12
 * @program bigmarket-14610-gwh
 * @description 抽奖活动账户表月表
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityAccountMonthEntity {
    //用户ID
    private String userId;
    //活动ID'
    private Long activityId;
    //月（yyyy-mm）
    private String month;
    //月次数
    private Integer monthCount;
    //月次数-剩余
    private Integer monthCountSurplus;

}

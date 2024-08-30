package cn.bugstack.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/08/30
 * @program bigmarket-14610-gwh
 * @description 抽奖活动表
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleActivity {
    //自增ID
    private Long id;
    //活动ID
    private Long activityId;
    //活动名称
    private String activityName;
    //活动描述
    private String activityDesc;
    //开始时间
    private Date beginDateTime;
    //结束时间
    private Date endDateTime;
    //库存总量
    private Integer stockCount;
    //剩余库存
    private Integer stockCountSurplus ;
    //活动参与次数配置
    private Long activityCountId;
    //抽奖策略ID
    private Long strategyId;
    //活动状态
    private String state;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
}

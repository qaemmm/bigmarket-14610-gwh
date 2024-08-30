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
 * @description 抽奖活动次数配置表
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleActivityCount {
    //自增ID
    private Long id;
    //活动次数编号
    private Long activityCountId;
    //总次数
    private Integer totalCount;
    //日次数
    private Integer dayCount;
    //月次数
    private Integer monthCount;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
}

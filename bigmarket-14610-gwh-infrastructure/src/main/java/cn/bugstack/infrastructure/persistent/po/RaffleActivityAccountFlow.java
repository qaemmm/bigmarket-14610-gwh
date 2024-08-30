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
 * @description 抽奖活动账户流水表
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleActivityAccountFlow {
    //自增ID
    private Integer id;
    //用户ID
    private String userId;
    //活动ID
    private Long activityId;
    //总次数
    private Integer totalCount;
    //日次数
    private Integer dayCount;
    //月次数
    private Integer monthCount;
    //流水ID -
    private String flowId;
    //生成的唯一ID
    private String flowChannel;
    //流水渠道（activity-活动领取、sale-购买、redeem-兑换、free-免费赠送）
    private String bizId;
    //业务ID（外部透传，活动ID、订单ID
    private Date createTime;
    //创建时间
    private Date updateTime;
}

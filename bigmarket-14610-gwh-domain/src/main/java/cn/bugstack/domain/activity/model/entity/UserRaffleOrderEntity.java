package cn.bugstack.domain.activity.model.entity;

import cn.bugstack.domain.activity.model.valobj.UserRaffleOrderStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/13
 * @program bigmarket-14610-gwh
 * @description 用户抽奖订单实体信息
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRaffleOrderEntity {

    //用户ID
    private String userId;
    //活动ID
    private Long activityId;
    //活动名称
    private String activityName;
    //抽奖策略ID
    private Long strategyId;
    //订单ID
    private String orderId;
    //下单时间
    private Date orderTime;
    //订单状态；create-创建、used-已使用、cancel-已作废
//    private String orderState;
    //上一块本来是db映射过来的entity订单状态；
    private UserRaffleOrderStateVO orderState;
}

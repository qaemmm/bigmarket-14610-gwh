package cn.bugstack.domain.rebate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description 行为订单实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorRebateOrderEntity {
    //用户ID
    private String userId;
    //订单ID
    private String orderId;
    //行为类型（sign 签到、openai_pay 支付）
    private String behaviorType;
    //返利描述
    private String rebateDesc;
    //返利类型（sku 活动库存充值商品、integral 用户活动积分）
    private String rebateType;
    //返利配置【sku值，积分值】
    private String rebateConfig;
    //外部业务ID
    private String outBusinessNo;
    //业务ID - 拼接的唯一值
    private String bizId;
}

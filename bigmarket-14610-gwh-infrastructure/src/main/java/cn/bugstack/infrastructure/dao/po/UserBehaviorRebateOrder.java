package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description 用户行为返利订单
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBehaviorRebateOrder {
    //自增ID
    private Integer id;
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
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
}

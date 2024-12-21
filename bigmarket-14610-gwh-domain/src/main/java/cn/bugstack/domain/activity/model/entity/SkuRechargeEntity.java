package cn.bugstack.domain.activity.model.entity;

import cn.bugstack.domain.activity.model.valobj.OrderTradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description 活动商品充值实体对象
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuRechargeEntity {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 商品SKU - activity + activity count
     */
    private Long sku;

    /**
     * 幂等的唯一业务号，外部谁就是支付订单成功后，返回的业务号
     */
    private String outBusinessNo;

    /** 用户ID */
    private OrderTradeTypeVO orderTradeType = OrderTradeTypeVO.rebate_no_pay_trade;

}

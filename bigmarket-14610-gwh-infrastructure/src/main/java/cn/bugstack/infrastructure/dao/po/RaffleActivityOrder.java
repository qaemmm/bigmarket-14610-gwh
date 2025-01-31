package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/08/30
 * @program bigmarket-14610-gwh
 * @description 抽奖活动单
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleActivityOrder {
    //自增ID
    private Long id;
    //用户ID
    private String userId;
    //sku
    private Long sku;
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
    /**
     * 总次数
     */
    private Integer totalCount;

    /**
     * 日次数
     */
    private Integer dayCount;

    /**
     * 月次数
     */
    private Integer monthCount;
    //订单状态
    private String state;
    // 外部业务号
    private String outBusinessNo;
    /**
     * 支付金额【积分】
     */
    private BigDecimal payAmount;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
}


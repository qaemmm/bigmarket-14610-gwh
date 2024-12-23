package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.*;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description 抽奖活动账户额度服务
 **/
public interface IRaffleActivityAccountQuotaService {

    /**
     * 创建 sku 账户充值订单，给用户增加抽奖次数
     * <p>
     * 1. 在【打卡、签到、分享、对话、积分兑换】等行为动作下，创建出活动订单，给用户的活动账户【日、月】充值可用的抽奖次数。
     * 2. 对于用户可获得的抽奖次数，比如首次进来就有一次，则是依赖于运营配置的动作，在前端页面上。用户点击后，可以获得一次抽奖次数。
     *
     * @param skuRechargeEntity
     * @return
     */
    UnpaidActivityOrderEntity createOrder(SkuRechargeEntity skuRechargeEntity);


    /**
     * 订单出货 - 积分充值
     * @param deliveryOrderEntity 出货单实体对象
     */
    void updateOrder(DeliveryOrderEntity deliveryOrderEntity);


    /**
     * 获取用户的当天活动的参与次数
     *
     * @param userId 用户ID，activityId 活动ID
     * @return
     */
    Integer getUserTodayPartakeCount(String userId, Long activityId);

    /**
     * 通过用户id、活动id获取活动账户
     * @param userId
     * @param activityId
     * @return
     */
    ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId);

    /**
     * 用户参与次数
     * @param userId
     * @param activityId
     * @return
     */
    Integer queryUserPartakeCount(String userId, Long activityId);
}


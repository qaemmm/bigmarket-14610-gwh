package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.ActivityOrderEntity;
import cn.bugstack.domain.activity.model.entity.ActivityShopCartEntity;
import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description 抽奖活动订单接口
 **/
public interface IRaffleOrder {

    /**
     * 创建抽奖活动订单
     * @param activityShopCartEntity 活动sku实体，通过sku领取活动
     * @return 活动参与记录实体
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);

    /**
     * 创建 sku 账户充值订单，给用户增加抽奖次数
     * <p>
     * 1. 在【打卡、签到、分享、对话、积分兑换】等行为动作下，创建出活动订单，给用户的活动账户【日、月】充值可用的抽奖次数。
     * 2. 对于用户可获得的抽奖次数，比如首次进来就有一次，则是依赖于运营配置的动作，在前端页面上。用户点击后，可以获得一次抽奖次数。
     *
     * @param skuRechargeEntity
     * @return
     */
    String createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity);

}


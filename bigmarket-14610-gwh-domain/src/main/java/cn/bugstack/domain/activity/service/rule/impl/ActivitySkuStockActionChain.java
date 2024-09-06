package cn.bugstack.domain.activity.service.rule.impl;

import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.service.rule.AbstractActionChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description 商品库存处理【校验&扣减】
 **/
@Slf4j
@Component("activity_sku_stock_action")
public class ActivitySkuStockActionChain extends AbstractActionChain {

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-商品库存处理【校验&扣减】开始。");
        //todo --在ActivitySkuEntity可以获取到全部库存以及剩余库存；
        //获取到当前需要消耗的总的次数
        Integer totalCount = activityCountEntity.getTotalCount();
        //先对activitySkuEntity的库存进行校验，如果库存不足，则直接返回false；以及剩余库存扣减消耗的总的次数；
        Integer stockCountSurplus = activitySkuEntity.getStockCountSurplus();
        if (stockCountSurplus <= 0||stockCountSurplus-totalCount<0) {
            log.info("活动责任链-商品库存处理【校验&扣减】失败，库存不足。");
            return false;
        }



        return true;
    }

}

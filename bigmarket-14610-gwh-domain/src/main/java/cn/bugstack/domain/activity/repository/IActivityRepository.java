package cn.bugstack.domain.activity.repository;

import cn.bugstack.domain.activity.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
public interface IActivityRepository {
    ActivitySkuEntity queryActivityBySku(Long sku);

    ActivityCountEntity queryActivityCountByActivityCountId(Long activityCountId);

    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    void doSaveOrder(CreateOrderAggregate createOrderAggregate);

    void cacheActivitySkuStockCount(String cacheKey, Integer stockCount);

    boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO build);

    void clearActivitySkuStock(Long sku);

    ActivitySkuStockKeyVO takeQueueValve();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);
}

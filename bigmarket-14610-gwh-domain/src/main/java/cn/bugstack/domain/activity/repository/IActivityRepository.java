package cn.bugstack.domain.activity.repository;

import cn.bugstack.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.bugstack.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.bugstack.domain.activity.model.entity.*;
import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.Date;
import java.util.List;

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

    void doSaveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void cacheActivitySkuStockCount(String cacheKey, Integer stockCount);

    boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO build);

    void clearActivitySkuStock(Long sku);

    ActivitySkuStockKeyVO takeQueueValve();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void saveCteatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);

    ActivityAccountEntity queryActivityAccount(String userId, Long activityId);

    ActivityAccountMonthEntity queryActivityAccountMonth(String userId, Long activityId, String month);

    ActivityAccountDayEntity queryActivityAccountDay(String userId, Long activityId, String day);


    List<ActivitySkuEntity> queryActivityByActivityId(Long activityId);

    Integer getUserTodayPartakeCount(String userId, Long activityId);

    ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId);

    Integer queryUserPartakeCount(String userId, Long activityId);

    void doSaveCreditPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void doSaveNoPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void updateOrder(DeliveryOrderEntity deliveryOrderEntity);
}

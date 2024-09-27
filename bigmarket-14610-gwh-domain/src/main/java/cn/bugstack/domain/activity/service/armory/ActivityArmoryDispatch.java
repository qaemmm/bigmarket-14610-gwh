package cn.bugstack.domain.activity.service.armory;

import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/10
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Slf4j
@Service
public class ActivityArmoryDispatch implements IActivityDispatch ,IActivityArmory{

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public boolean assembleActivityByActivityId(Long activityId) {
        if (null == activityId) {
            return false;
        }
        //通过activityId预热活动
        List<ActivitySkuEntity> activitySkuEntities = activityRepository.queryActivityByActivityId(activityId);
        for(ActivitySkuEntity activitySkuEntity : activitySkuEntities){
            //缓存预热，加载活动库存
            cacheActivitySkuStockCount(activitySkuEntity.getSku(), activitySkuEntity.getStockCount());
            //预热活动次数
            activityRepository.queryActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        }
        //预热活动
        activityRepository.queryRaffleActivityByActivityId(activityId);

        return true;
    }

    @Override
    public boolean assembleActivitySku(Long sku) {
        //缓存预热，加载活动库存
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivityBySku(sku); ;
        //缓存活动库存
        cacheActivitySkuStockCount(sku, activitySkuEntity.getStockCount());
        Long activityId = activitySkuEntity.getActivityId();
        if(null==activityId)return false;
        //预热活动
        activityRepository.queryRaffleActivityByActivityId(activityId);

        //预热活动次数
        activityRepository.queryActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        return true;
    }

    @Override
    public void cacheActivitySkuStockCount(Long sku, Integer stockCount) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY+ sku;
        activityRepository.cacheActivitySkuStockCount(cacheKey,stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, Date endDateTime) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        return activityRepository.subtractionActivitySkuStock(sku,cacheKey, endDateTime);
    }
}

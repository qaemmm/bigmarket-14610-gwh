package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityCountDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import cn.bugstack.infrastructure.persistent.po.RaffleActivity;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityCount;
import cn.bugstack.infrastructure.persistent.po.RaffleActivitySku;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description 活动仓储服务
 **/
@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private IRaffleActivityDao raffleActivityDao;
    @Resource
    private IRaffleActivitySkuDao raffleActivitySkuDao;
    @Resource
    private IRaffleActivityCountDao raffleActivityCountDao;


    @Override
    public ActivitySkuEntity queryActivityBySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivityBySku(sku);
        return ActivitySkuEntity.builder()
                .activityCountId(raffleActivitySku.getActivityCountId())
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }

    @Override
    public ActivityCountEntity queryActivityCountByActivityCountId(Long activityCountId) {

        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity =  redisService.getValue(cacheKey);
        if (null != activityCountEntity) return activityCountEntity;
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryActivityCountByActivityCountId(activityCountId);

        activityCountEntity = ActivityCountEntity.builder()
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .totalCount(raffleActivityCount.getTotalCount())
                .activityCountId(raffleActivityCount.getActivityCountId())
                .build();
        redisService.setValue(cacheKey,activityCountEntity);
        return activityCountEntity;
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if(null!=activityEntity)return activityEntity;
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .activityDesc(raffleActivity.getActivityDesc())
                .activityName(raffleActivity.getActivityName())
                .activityId(raffleActivity.getActivityId())

//                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .build();
        redisService.setValue(cacheKey,activityEntity);
    return activityEntity;
    }

}


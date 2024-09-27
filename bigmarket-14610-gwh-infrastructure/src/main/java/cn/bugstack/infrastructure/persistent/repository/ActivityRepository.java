package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import cn.bugstack.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.bugstack.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityAccountDayEntity;
import cn.bugstack.domain.activity.model.entity.ActivityAccountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityAccountMonthEntity;
import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivityOrderEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.model.valobj.UserRaffleOrderStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityAccountDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityAccountDayDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityAccountMonthDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityCountDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityOrderDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import cn.bugstack.infrastructure.persistent.dao.IUserRaffleOrderDao;
import cn.bugstack.infrastructure.persistent.po.RaffleActivity;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccount;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccountDay;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccountMonth;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityCount;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityOrder;
import cn.bugstack.infrastructure.persistent.po.RaffleActivitySku;
import cn.bugstack.infrastructure.persistent.po.UserRaffleOrder;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    private IRaffleActivityAccountDao raffleActivityAccountDao;
    @Resource
    private IRaffleActivityOrderDao raffleActivityOrderDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private EventPublisher eventPublisher;

    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;

    @Resource
    private IRaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Resource
    private IRaffleActivityAccountMonthDao raffleActivityAccountMonthDao;

    @Resource
    private IUserRaffleOrderDao userRaffleOrderDao;

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
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .build();
        redisService.setValue(cacheKey,activityEntity);
    return activityEntity;
    }

    @Override
    public void doSaveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        // 订单对象
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(activityOrderEntity.getUserId());
        raffleActivityOrder.setSku(activityOrderEntity.getSku());
        raffleActivityOrder.setActivityId(activityOrderEntity.getActivityId());
        raffleActivityOrder.setActivityName(activityOrderEntity.getActivityName());
        raffleActivityOrder.setStrategyId(activityOrderEntity.getStrategyId());
        raffleActivityOrder.setOrderId(activityOrderEntity.getOrderId());
        raffleActivityOrder.setOrderTime(activityOrderEntity.getOrderTime());
        //todo ！！疑惑订单里的总月日次数是什么意思
        raffleActivityOrder.setTotalCount(activityOrderEntity.getTotalCount());
        raffleActivityOrder.setDayCount(activityOrderEntity.getDayCount());
        raffleActivityOrder.setMonthCount(activityOrderEntity.getMonthCount());
        raffleActivityOrder.setTotalCount(createQuotaOrderAggregate.getTotalCount());
        raffleActivityOrder.setDayCount(createQuotaOrderAggregate.getDayCount());
        raffleActivityOrder.setMonthCount(createQuotaOrderAggregate.getMonthCount());
        raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

        // 账户对象
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setUserId(createQuotaOrderAggregate.getUserId());
        raffleActivityAccount.setActivityId(createQuotaOrderAggregate.getActivityId());
        raffleActivityAccount.setTotalCount(createQuotaOrderAggregate.getTotalCount());
        raffleActivityAccount.setTotalCountSurplus(createQuotaOrderAggregate.getTotalCount());
        raffleActivityAccount.setDayCount(createQuotaOrderAggregate.getDayCount());
        raffleActivityAccount.setDayCountSurplus(createQuotaOrderAggregate.getDayCount());
        raffleActivityAccount.setMonthCount(createQuotaOrderAggregate.getMonthCount());
        raffleActivityAccount.setMonthCountSurplus(createQuotaOrderAggregate.getMonthCount());

        // 以用户ID作为切分键，通过 doRouter 设定路由【这样就保证了下面的操作，都是同一个链接下，也就保证了事务的特性】
        dbRouter.doRouter(createQuotaOrderAggregate.getUserId());
        //编程式事务
        transactionTemplate.execute(status -> {
           try{
               // 保存订单
               raffleActivityOrderDao.insert(raffleActivityOrder);
               //这里再保存账户前，需要判断一下是否存在这个账户，如果不存在，需要初始化一个账户
               // （但是再多线程的情况下）可能就是存在两个线程同时判断不存在，然后同时初始化，这个时候就会出现问题
               int count = raffleActivityAccountDao.updateAccoutQuota(raffleActivityAccount);
               if(count == 0){
                   raffleActivityAccountDao.insert(raffleActivityAccount);
               }
               return 1;
           }catch (DuplicateKeyException e){
               status.setRollbackOnly();
               log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
               throw new AppException(ResponseCode.INDEX_DUP.getCode());
           }finally {
               dbRouter.clear();
           }
        });


    }

    @Override
    public void cacheActivitySkuStockCount(String cacheKey, Integer stockCount) {
        // 缓存活动库存
        //todo -- 这一块通常我觉得是需要设置一个过期时间的，因为库存是会变化的，如果不设置过期时间，那么就会导致缓存的数据不准确
        redisService.setValue(cacheKey,stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime) {
        long surplus = redisService.decr(cacheKey);
        if(surplus == 0){
            //库存消耗没了以后，发送MQ消息，更新数据库库存
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(), activitySkuStockZeroMessageEvent.buildEventMesaage(sku));

        }else if (surplus <0){
            //没有库存了；
            redisService.setAtomicLong(cacheKey,0);
            return false;
        }
        // 1. 按照cacheKey decr 后的值，如 99、98、97 和 key 组成为库存锁的key进行使用。
        // 2. 加锁为了兜底，如果后续有恢复库存，手动处理等【运营是人来操作，会有这种情况发放，系统要做防护】，也不会超卖。因为所有的可用库存key，都被加锁了。
        // 3. 设置加锁时间为活动到期 + 延迟1天
        String lockKey = cacheKey + Constants.UNDERLINE + surplus;
        long expiredMillis = endDateTime.getTime() - System.currentTimeMillis()
                + TimeUnit.DAYS.toMillis(1);
        boolean lock = redisService.setNx(lockKey, expiredMillis, TimeUnit.MILLISECONDS);
        if(!lock){
            log.info("活动sku库存加锁失败{}",lockKey);
        }
        return true;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        //这里是延迟的方法消息到 Redis 队列中。以此来减缓消费。（这里是一个双重减缓，一个是延迟队列，一个是定时的任务调度）
        String cacheKey =  Constants.RedisKey.ACTIVITY_SKU_COUNT_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO, 3, TimeUnit.SECONDS);

    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        raffleActivitySkuDao.clearActivitySkuStock(sku);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValve() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> destinationQueue = redisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void clearQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        blockingQueue.clear();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuDao.updateActivitySkuStock(sku);
    }

    @Override
    public void saveCteatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        try {
            String userId = createPartakeOrderAggregate.getUserId();
            Long activityId = createPartakeOrderAggregate.getActivityId();
            ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccountEntity();
            ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
            ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
            UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();
            //统一切换路由，以下事务内的所有操作，都走一个路由
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                        try {
                            //1、更新总账户
                            int totalCount = raffleActivityAccountDao.updateActivityAccountSubstractQuota(
                                    RaffleActivityAccount.builder()
                                            .userId(userId)
                                            .activityId(activityId)
                                            .build());
                            if (1 != totalCount) {
                                status.setRollbackOnly();
                                log.warn("写入创建参与活动记录，更新总账户余额不足，异常 userId: {} activityId: {}", userId, activityId);
                                throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                            }

                            // 2、创建或者更新月账户，true -- 存在则更新， false -- 不存在则插入
                            if (createPartakeOrderAggregate.isExistAccountMonth()) {
                                int monthCount = raffleActivityAccountMonthDao.updateActivityAccountMonthSubstractQuota(
                                        RaffleActivityAccountMonth.builder()
                                                .userId(userId)
                                                .activityId(activityId)
                                                .month(activityAccountMonthEntity.getMonth())
                                                .build()
                                );
                                if (1 != monthCount) {
                                    status.setRollbackOnly();
                                    log.warn("写入创建参与活动记录，更新月账户余额不足，异常 userId: {} activityId: {} month:{}", userId, activityId, activityAccountMonthEntity.getMonth());
                                    throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
                                }
                                // 月账户存在，同时更新总账表中月镜像额度
                                raffleActivityAccountDao.updateActivityAccountMonthMirrorQuota(
                                        RaffleActivityAccount.builder()
                                                .userId(userId)
                                                .activityId(activityId)
                                                .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus())
                                                .build());
                            } else {
                                raffleActivityAccountMonthDao.insertActivityAccountMonth(
                                        RaffleActivityAccountMonth.builder()
                                                .userId(userId)
                                                .activityId(activityId)
                                                .month(activityAccountMonthEntity.getMonth())
                                                .monthCount(activityAccountMonthEntity.getMonthCount())
                                                .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus()-1)
                                                .build());
                                // 新创建月账户，则更新总账表中月镜像额度
                                raffleActivityAccountDao.updateActivityAccountMonthMirrorQuota(
                                        RaffleActivityAccount.builder()
                                                .userId(userId)
                                                .activityId(activityId)
                                                .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                                                .build());
                            }


                            // 3. 创建或更新日账户，true - 存在则更新，false - 不存在则插入
                            if (createPartakeOrderAggregate.isExistAccountDay()) {
                                int dayCount = raffleActivityAccountDayDao.updateActivityAccountDaySubstractQuota(
                                        RaffleActivityAccountDay.builder()
                                                .userId(userId)
                                                .activityId(activityId)
                                                .day(activityAccountDayEntity.getDay())
                                                .build()
                                );
                                if (1 != dayCount) {
                                    status.setRollbackOnly();
                                    log.warn("写入创建参与活动记录，更新日账户余额不足，异常 userId: {} activityId: {} day:{}", userId, activityId, activityAccountDayEntity.getDay());
                                    throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
                                }
                                // 日账户存在，同时更新总账表中日镜像额度
                                raffleActivityAccountDao.updateActivityAccountDayMirrorQuota(
                                        RaffleActivityAccount.builder()
                                                .userId(userId)
                                                .activityId(activityId)
                                                .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus())
                                                .build());
                            } else {
                                raffleActivityAccountDayDao.insertActivityAccountDay(
                                        RaffleActivityAccountDay.builder()
                                                .userId(userId)
                                                .activityId(activityId)
                                                .day(activityAccountDayEntity.getDay())
                                                .dayCount(activityAccountDayEntity.getDayCount())
                                                .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus()-1)
                                                .build());
                                // 新创建日账户，则更新总账表中日镜像额度
                                raffleActivityAccountDao.updateActivityAccountDayMirrorQuota(
                                        RaffleActivityAccount.builder()
                                                .userId(userId)
                                                .activityId(activityId)
                                                .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                                                .build());

                            }


                // 4. 写入参与活动订单
                userRaffleOrderDao.insert(UserRaffleOrder.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .activityName(userRaffleOrderEntity.getActivityName())
                        .orderId(userRaffleOrderEntity.getOrderId())
                        .orderTime(userRaffleOrderEntity.getOrderTime())
                        .orderState(userRaffleOrderEntity.getOrderState().getCode())
                        .strategyId(userRaffleOrderEntity.getStrategyId())
                        .build());

                return 1;
            }catch(DuplicateKeyException e){
                status.setRollbackOnly();
                log.error("写入创建参与活动记录，唯一索引冲突 userId: {} activityId: {}", userId, activityId, e);
                throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
            }
        });
    }finally {
            dbRouter.clear();
        }
    }

    @Override
    public UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(partakeRaffleActivityEntity.getUserId());
        userRaffleOrderReq.setActivityId(partakeRaffleActivityEntity.getActivityId());
        UserRaffleOrder userRaffleOrder = userRaffleOrderDao.queryNoUsedRaffleOrder(userRaffleOrderReq);
        if(null == userRaffleOrder){
            return null;
        }
        // 将userRaffleOrder转换为UserRaffleOrderEntity
         return UserRaffleOrderEntity.builder()
                 .userId(userRaffleOrder.getUserId())
                 .activityId(userRaffleOrder.getActivityId())
                 .activityName(userRaffleOrder.getActivityName())
                 .orderId(userRaffleOrder.getOrderId())
                 .orderTime(userRaffleOrder.getOrderTime())
                 .orderState(UserRaffleOrderStateVO.valueOf(userRaffleOrder.getOrderState()))
                 .strategyId(userRaffleOrder.getStrategyId())
                 .build();
    }

    @Override
    public ActivityAccountEntity queryActivityAccount(String userId, Long activityId) {
        RaffleActivityAccount raffleActivityAccountEntityReq = new RaffleActivityAccount();
        raffleActivityAccountEntityReq.setUserId(userId);
        raffleActivityAccountEntityReq.setActivityId(activityId);
                RaffleActivityAccount raffleActivityAccountEntity = raffleActivityAccountDao.queryActivityAccount(raffleActivityAccountEntityReq);
        if (null == raffleActivityAccountEntity) {
            return null;
        }
        //将raffleActivityAccountEntity转换为ActivityAccountEntity
        return ActivityAccountEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .totalCount(raffleActivityAccountEntity.getTotalCount())
                .totalCountSurplus(raffleActivityAccountEntity.getTotalCountSurplus())
                .dayCount(raffleActivityAccountEntity.getDayCount())
                .dayCountSurplus(raffleActivityAccountEntity.getDayCountSurplus())
                .monthCount(raffleActivityAccountEntity.getMonthCount())
                .monthCountSurplus(raffleActivityAccountEntity.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonth(String userId, Long activityId, String month) {
        RaffleActivityAccountMonth raffleActivityAccountMonthReq = new RaffleActivityAccountMonth();
        raffleActivityAccountMonthReq.setUserId(userId);
        raffleActivityAccountMonthReq.setActivityId(activityId);
        raffleActivityAccountMonthReq.setMonth(month);
        RaffleActivityAccountMonth raffleActivityAccountMonth = raffleActivityAccountMonthDao.queryActivityAccountMonth(raffleActivityAccountMonthReq);
        if(null == raffleActivityAccountMonth){
            return null;
        }
        return ActivityAccountMonthEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .month(month)
                .monthCount(raffleActivityAccountMonth.getMonthCount())
                .monthCountSurplus(raffleActivityAccountMonth.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDay(String userId, Long activityId, String day) {
        RaffleActivityAccountDay raffleActivityAccountDayReq = new RaffleActivityAccountDay();
        raffleActivityAccountDayReq.setUserId(userId);
        raffleActivityAccountDayReq.setActivityId(activityId);
        raffleActivityAccountDayReq.setDay(day);
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDay(raffleActivityAccountDayReq);
        if(null==raffleActivityAccountDay){
            return null;
        }
        return ActivityAccountDayEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .day(day)
                .dayCount(raffleActivityAccountDay.getDayCount())
                .dayCountSurplus(raffleActivityAccountDay.getDayCountSurplus())
                .build();
    }

    @Override
    public List<ActivitySkuEntity> queryActivityByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkuList = raffleActivitySkuDao.queryActivityByActivityId(activityId);
        //将RaffleActivitySku转换为ActivitySkuEntity
        if(null == raffleActivitySkuList || raffleActivitySkuList.isEmpty()){
            return Collections.emptyList();
        }
        return raffleActivitySkuList.stream().map(raffleActivitySku ->
                ActivitySkuEntity.builder()
                        .sku(raffleActivitySku.getSku())
                        .activityId(raffleActivitySku.getActivityId())
                        .activityCountId(raffleActivitySku.getActivityCountId())
                        .stockCount(raffleActivitySku.getStockCount())
                        .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                        .build()
        ).collect(Collectors.toList());
    }

}


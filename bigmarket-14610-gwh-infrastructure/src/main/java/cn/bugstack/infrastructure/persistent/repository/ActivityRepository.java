package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.activity.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivityOrderEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityAccountDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityCountDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityOrderDao;
import cn.bugstack.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import cn.bugstack.infrastructure.persistent.po.RaffleActivity;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccount;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityCount;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityOrder;
import cn.bugstack.infrastructure.persistent.po.RaffleActivitySku;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

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
    @Resource
    private IRaffleActivityAccountDao raffleActivityAccountDao;
    @Resource
    private IRaffleActivityOrderDao raffleActivityOrderDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IDBRouterStrategy dbRouter;


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

    @Override
    public void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        // 订单对象
        ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
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
        raffleActivityOrder.setTotalCount(createOrderAggregate.getTotalCount());
        raffleActivityOrder.setDayCount(createOrderAggregate.getDayCount());
        raffleActivityOrder.setMonthCount(createOrderAggregate.getMonthCount());
        raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

        // 账户对象
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setUserId(createOrderAggregate.getUserId());
        raffleActivityAccount.setActivityId(createOrderAggregate.getActivityId());
        raffleActivityAccount.setTotalCount(createOrderAggregate.getTotalCount());
        raffleActivityAccount.setTotalCountSurplus(createOrderAggregate.getTotalCount());
        raffleActivityAccount.setDayCount(createOrderAggregate.getDayCount());
        raffleActivityAccount.setDayCountSurplus(createOrderAggregate.getDayCount());
        raffleActivityAccount.setMonthCount(createOrderAggregate.getMonthCount());
        raffleActivityAccount.setMonthCountSurplus(createOrderAggregate.getMonthCount());

        // 以用户ID作为切分键，通过 doRouter 设定路由【这样就保证了下面的操作，都是同一个链接下，也就保证了事务的特性】
        dbRouter.doRouter(createOrderAggregate.getUserId());
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

}


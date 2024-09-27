package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.bugstack.domain.award.model.entity.TaskEntity;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.repository.IAwardRepository;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.infrastructure.persistent.dao.ITaskDao;
import cn.bugstack.infrastructure.persistent.dao.IUserAwardRecordDao;
import cn.bugstack.infrastructure.persistent.dao.IUserRaffleOrderDao;
import cn.bugstack.infrastructure.persistent.po.Task;
import cn.bugstack.infrastructure.persistent.po.UserAwardRecord;
import cn.bugstack.infrastructure.persistent.po.UserRaffleOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 中奖记录仓储实现.
 **/
@Slf4j
@Repository
public class AwardRepository implements IAwardRepository {
    @Resource
    private IUserAwardRecordDao userAwardRecordDao;
    @Resource
    private ITaskDao taskDao;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private IUserRaffleOrderDao userRaffleOrderDao;
    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Integer awardId = userAwardRecordEntity.getAwardId();

        UserAwardRecordEntity userAwardRecordEntityReq = userAwardRecordAggregate.getUserAwardRecordEntity();
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setStrategyId(userAwardRecordEntityReq.getStrategyId());
        userAwardRecord.setUserId(userAwardRecordEntityReq.getUserId());
        userAwardRecord.setAwardId(userAwardRecordEntityReq.getAwardId());
        userAwardRecord.setAwardTitle(userAwardRecordEntityReq.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntityReq.getAwardTime());
        userAwardRecord.setActivityId(userAwardRecordEntityReq.getActivityId());
        userAwardRecord.setOrderId(userAwardRecordEntityReq.getOrderId());
        userAwardRecord.setAwardState(userAwardRecordEntityReq.getAwardState().getCode());


        TaskEntity taskEntityReq = userAwardRecordAggregate.getTaskEntity();
        Task task = new Task();
        task.setTopic(taskEntityReq.getTopic());
        task.setMessage(JSON.toJSONString(taskEntityReq.getMessage()));
        task.setState(taskEntityReq.getState().getCode());
        task.setMessageId(taskEntityReq.getMessageId());
        task.setUserId(taskEntityReq.getUserId());

        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(userAwardRecordEntity.getUserId());
        userRaffleOrderReq.setOrderId(userAwardRecordEntity.getOrderId());

        try{
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try{
                    userAwardRecordDao.insert(userAwardRecord);
                    taskDao.insert(task);
                    //这一快可以理解成前面的事务是将订单流水记录进行插入，这一块是将订单状态进行更新
                    int count = userRaffleOrderDao.updateUserRaffleOrderStateUsed(userRaffleOrderReq);
                    if(1!=count){
                        status.setRollbackOnly();
                        log.error("写入中奖记录，更新用户抽奖订单状态失败 userId: {} activityId: {} awardId: {}", userId, activityId, awardId);
                        throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
                    }
                    return 1;
                }catch(DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("写入中奖记录，唯一索引冲突 userId: {} activityId: {} awardId: {}", userId, activityId, awardId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        }finally {
            dbRouter.clear();
        }
        //todo 这一块好像就是说可以使用线程池，思考一下
        //事务下面的操作这里不非得保证成功，在 SendMessageTaskJob 任务补偿中会扫描库表的 task 表来发送 MQ 消息。
        try {
            // 发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(task.getTopic(), task.getMessage());
            // 更新数据库记录，task 任务表
            taskDao.updateTaskSendMessageCompleted(task);
        } catch (Exception e) {
            log.error("写入中奖记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
            taskDao.updateTaskSendMessageFail(task);
        }
    }
}

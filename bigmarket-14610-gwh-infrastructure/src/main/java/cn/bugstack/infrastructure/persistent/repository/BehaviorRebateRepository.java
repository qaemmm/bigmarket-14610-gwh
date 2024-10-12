package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.bugstack.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.model.entity.TaskEntity;
import cn.bugstack.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import cn.bugstack.domain.rebate.repository.IBehaviorRebateRepository;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.infrastructure.persistent.dao.IDailyBehaviorRebateDao;
import cn.bugstack.infrastructure.persistent.dao.ITaskDao;
import cn.bugstack.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import cn.bugstack.infrastructure.persistent.po.DailyBehaviorRebate;
import cn.bugstack.infrastructure.persistent.po.Task;
import cn.bugstack.infrastructure.persistent.po.UserBehaviorRebateOrder;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description 行为返利仓储
 **/
@Repository
@Slf4j
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Resource
    private IDailyBehaviorRebateDao iDailyBehaviorRebateDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private IUserBehaviorRebateOrderDao userBehaviorRebateOrderDao;
    @Resource
    private ITaskDao taskDao;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<DailyBehaviorRebateVO> queryDailyRebateByBehaviorType(String rebateType) {
        List<DailyBehaviorRebate> dailyBehaviorRebates = iDailyBehaviorRebateDao.queryDailyRebateByBehaviorType(rebateType);
        if(null==dailyBehaviorRebates || dailyBehaviorRebates.isEmpty()) return new ArrayList<>();
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = new ArrayList<>();
        for(DailyBehaviorRebate dailyBehaviorRebate : dailyBehaviorRebates){
            DailyBehaviorRebateVO dailyBehaviorRebateVO =new DailyBehaviorRebateVO(
                    dailyBehaviorRebate.getBehaviorType(),
                    dailyBehaviorRebate.getRebateDesc(),
                    dailyBehaviorRebate.getRebateType(),
                    dailyBehaviorRebate.getRebateConfig()
            );

            dailyBehaviorRebateVOS.add(dailyBehaviorRebateVO);
        }
        return dailyBehaviorRebateVOS;
    }

    @Override
    public void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates) {
        //这一块参数传入了userId，是因为如果直接从聚合对象中拿的话，需要先get（0）.getUserId()比较麻烦吧
        try{
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
               try{
                   for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates) {
                       // 用户行为返利订单对象
                       BehaviorRebateOrderEntity behaviorRebateOrderEntity = behaviorRebateAggregate.getBehaviorRebateOrderEntity();
                       UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
                       userBehaviorRebateOrder.setUserId(behaviorRebateOrderEntity.getUserId());
                       userBehaviorRebateOrder.setOrderId(behaviorRebateOrderEntity.getOrderId());
                       userBehaviorRebateOrder.setBehaviorType(behaviorRebateOrderEntity.getBehaviorType());
                       userBehaviorRebateOrder.setRebateDesc(behaviorRebateOrderEntity.getRebateDesc());
                       userBehaviorRebateOrder.setRebateType(behaviorRebateOrderEntity.getRebateType());
                       userBehaviorRebateOrder.setRebateConfig(behaviorRebateOrderEntity.getRebateConfig());
                       userBehaviorRebateOrder.setBizId(behaviorRebateOrderEntity.getBizId());

                       userBehaviorRebateOrderDao.insert(userBehaviorRebateOrder);

                       TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
                       Task task = new Task();
                       task.setUserId(taskEntity.getUserId());
                       task.setTopic(taskEntity.getTopic());
                       task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                       task.setState(taskEntity.getState().getCode());
                       task.setMessageId(taskEntity.getMessageId());
                       taskDao.insert(task);
                   }
                   return 1;
               } catch (DuplicateKeyException e) {
                   status.setRollbackOnly();
                   log.error("写入返利记录，唯一索引冲突 userId: {}", userId, e);
                   throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
               }

            });
        } finally {
            dbRouter.clear();
        }

        for(BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates){
            TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
            Task task = new Task();
            task.setUserId(taskEntity.getUserId());
            task.setMessageId(taskEntity.getMessageId());
            //发送mq消息
            try {
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                log.info("写入返利记录，发送MQ消息 userId: {} topic: {}", userId, taskEntity.getTopic());
                taskDao.updateTaskSendMessageCompleted(task);
            } catch (Exception e) {
                log.error("写入返利记录，发送MQ消息失败 userId: {} topic: {}", userId, taskEntity.getTopic());
                taskDao.updateTaskSendMessageFail(task);
            }
        }
    }
}

package cn.bugstack.trigger.job;

import cn.bugstack.domain.task.model.entity.TaskEntity;
import cn.bugstack.domain.task.service.ITaskService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 发送mq消息定时任务
 **/
@Slf4j
@Component()
public class SendMessageTaskJob {
    @Resource
    private ITaskService taskService;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private RedissonClient redisson;

    /**
     * 本地化任务注解；@Scheduled(cron = "0/5 * * * * ?")
     * 分布式任务注解；@XxlJob("SendMessageTaskJob")
     */
    @Timed(value = "SendMessageTaskJob_DB1", description = "发送MQ消息任务队列")
    @XxlJob("SendMessageTaskJob_DB1")
    public void exec() {
        RLock lock = redisson.getLock("big-market-SendMessageTaskJob_DB1");
        boolean isLocked =false;
        try{
            isLocked = lock.tryLock(3,0, TimeUnit.SECONDS);
//            log.info("isLocked:{}",isLocked);
            //前面加锁，！isLocked就是false，没毛病
            if (!isLocked) {return;}
            int dbCount = dbRouter.dbCount();
            for(int dbIdx = 1; dbIdx <= dbCount; dbIdx++){
                int finalDbIdx = dbIdx;
               try{
                   dbRouter.setDBKey(finalDbIdx);
                   dbRouter.setTBKey(0);
                   //查询发送MQ失败和超时1分钟未发送的MQ,兜底处理
                   List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                   if (taskEntities.isEmpty()){
                       return ;
                   }
                   log.info("定时任务，扫描mq任务表发送消息，扫描到待发送消息：{}", taskEntities);
                   for(TaskEntity taskEntity : taskEntities){
                       executor.execute(() -> {
                           try{
                               taskService.sendMessage(taskEntity);
                               taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                           }catch (Exception e){
                               log.error("定时任务，扫描mq任务表发送消息失败", e);
                               taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                           }
                       });
                   }
               }finally{
                   dbRouter.clear();
               }
            }
        }catch (Exception e) {
            log.error("定时任务，扫描mq任务表发送消息失败", e);
        }finally {
            dbRouter.clear();
            if (!isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    /**
     * 本地化任务注解；@Scheduled(cron = "0/5 * * * * ?")
     * 分布式任务注解；@XxlJob("SendMessageTaskJob_DB2")
     */
    @XxlJob("SendMessageTaskJob_DB2")
    public void exec_db02() {
        // 为什么加锁？分布式应用N台机器部署互备，任务调度会有N个同时执行，那么这里需要增加抢占机制，谁抢占到谁就执行。完毕后，下一轮继续抢占。
        RLock lock = redisson.getLock("big-market-SendMessageTaskJob_DB2");
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(3, 0, TimeUnit.SECONDS);
            if (!isLocked) return;

            // 设置库表
            dbRouter.setDBKey(2);
            dbRouter.setTBKey(0);
            // 查询未发送的任务
            List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
            if (taskEntities.isEmpty()) return;
            // 发送MQ消息
            for (TaskEntity taskEntity : taskEntities) {
                try {
                    taskService.sendMessage(taskEntity);
                    taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                } catch (Exception e) {
                    log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                    taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                }
            }
        } catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        } finally {
            dbRouter.clear();
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}

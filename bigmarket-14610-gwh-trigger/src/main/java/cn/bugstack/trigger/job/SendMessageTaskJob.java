package cn.bugstack.trigger.job;

import cn.bugstack.domain.task.model.entity.TaskEntity;
import cn.bugstack.domain.task.service.ITaskService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try{
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
        }

    }

}

package cn.bugstack.domain.task.repository;

import cn.bugstack.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 任务仓储层接口
 **/
public interface ITaskRepository {
    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}

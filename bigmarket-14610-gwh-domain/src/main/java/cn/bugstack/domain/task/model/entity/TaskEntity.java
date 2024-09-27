package cn.bugstack.domain.task.model.entity;

import cn.bugstack.domain.award.event.SendAwardMessageEvent;
import cn.bugstack.domain.award.model.valobj.TaskStateVO;
import cn.bugstack.types.event.BaseEvent;
import lombok.Data;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 任务实体对象
 * @create 2024-04-06 10:49
 */
@Data
public class TaskEntity {

    /**
     * 活动ID
     */
    private String userId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息编号
     */
    private String messageId;
    /**
     * 消息主体
     */
    private String message;

}

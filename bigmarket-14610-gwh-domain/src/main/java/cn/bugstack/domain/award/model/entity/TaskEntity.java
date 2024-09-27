package cn.bugstack.domain.award.model.entity;

import cn.bugstack.domain.award.event.SendAwardMessageEvent;
import cn.bugstack.domain.award.model.valobj.TaskStateVO;
import cn.bugstack.types.event.BaseEvent;
import lombok.Data;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 任务实体
 **/
//todo -- 这一块虽然是和数据库中字段一一对应的，但是这边是属于是实体对象，
// 业务怎么方便怎么去使用，不一定非要和数据库字段一一对应（db怎么舒服怎么来）
@Data
public class TaskEntity {

    //用户ID
    private String userId;
    //消息主题
    private String topic;
    //消息ID
    private String messageId;
    //消息主体
    private BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> message;
    //任务状态；create-创建、completed-完成、fail-失败
    private TaskStateVO state;
}

package cn.bugstack.domain.rebate.event;

import cn.bugstack.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Component
public class SendRebateMessageEvent extends BaseEvent<SendRebateMessageEvent.SendRebateMessage> {
    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;


    @Override
    public EventMessage<SendRebateMessage> buildEventMessage(SendRebateMessage data) {
        return EventMessage.<SendRebateMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();

    }

    @Override
    public String topic() {
        return topic;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendRebateMessage {
        /**
         * 用户ID
         */
        private String userId;
        /**
         * 返利描述
         */
        private String rebateDesc;
        /**
         * 返利类型
         */
        private String rebateType;
        /**
         * 返利配置
         */
        private String rebateConfig;
        /**
         * 业务ID - 唯一ID，确保幂等
         */
        private String bizId;


    }
}

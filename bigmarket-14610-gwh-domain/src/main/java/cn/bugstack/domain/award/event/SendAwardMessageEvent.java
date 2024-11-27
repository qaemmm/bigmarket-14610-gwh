package cn.bugstack.domain.award.event;

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
 * @date 2024/09/25
 * @program用户奖品记录事件消息
 * @description todo desc...
 **/
@Component
public class SendAwardMessageEvent extends BaseEvent<SendAwardMessageEvent.SendAwardMessage> {

    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;

    @Override
    public EventMessage<SendAwardMessage> buildEventMessage(SendAwardMessage data) {
        return EventMessage.<SendAwardMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    //这里不知道为什么读取不了配置，后续看看
    // -- 因为这个消息是一个组件，我原本使用的new的方式来进行床架你的，所以不可以被spring容器识别到
    @Override
    public String topic() {
        return this.topic;
    }

    //这一块封装成了内部的消息，本来是可以直接把UserAwardRecordEntity传进来的，但是这里为了防止腐化，所以封装成了内部消息，
    // 因为如果直接使用UserAwardRecordEntity，后续可能会有字段的改动，这样会导致消息的改动，所以这里封装成了内部消息
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendAwardMessage {
        /**
         * 用户ID
         */
        private String userId;
        /**
         * 订单ID
         */
        private String orderId;
        /**
         * 奖品ID
         */
        private Integer awardId;
        /**
         * 奖品标题（名称）
         */
        private String awardTitle;
        /**
         * 奖品配置信息
         */
        private String awardConfig;

    }
}

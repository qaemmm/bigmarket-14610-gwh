package cn.bugstack.infrastructure.event;

import cn.bugstack.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fuzhouling
 * @date 2024/09/10
 * @program bigmarket-14610-gwh
 * @description 消息发送
 **/
@Slf4j
@Component
public class EventPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String topic, BaseEvent.EventMessage<?> message){
        try {
            String jsonString = JSON.toJSONString(message);
            rabbitTemplate.convertAndSend(topic, jsonString);
            log.info("发送消息成功 topic: {} message: {}", topic, message);
        }catch (Exception e){
            log.error("发送消息失败", e);
            throw e;
        }
    }
}

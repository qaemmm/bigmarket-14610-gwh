package cn.bugstack.trigger.listener;

import cn.bugstack.domain.award.event.SendAwardMessageEvent;
import cn.bugstack.domain.award.model.entity.DistributeAwardEntity;
import cn.bugstack.domain.award.service.IAwardService;
import cn.bugstack.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 发送消息奖励消费者
 **/
@Slf4j
@Component
public class SendAwardCustomer {
    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;

    @Resource
    private IAwardService awardService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_award}"))
    public void listener(String message) {
        try {
            log.info("监听用户奖品发送消息 topic: {} message: {}", topic, message);
            BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage>>() {
            }.getType());

            SendAwardMessageEvent.SendAwardMessage sendAwardMessage = eventMessage.getData();

            DistributeAwardEntity distributeAwardEntity = new DistributeAwardEntity();
            distributeAwardEntity.setOrderId(sendAwardMessage.getOrderId());
            distributeAwardEntity.setUserId(sendAwardMessage.getUserId());
            distributeAwardEntity.setAwardConfig(sendAwardMessage.getAwardConfig());
            distributeAwardEntity.setAwardId(sendAwardMessage.getAwardId());
            awardService.distributeAward(distributeAwardEntity);

        } catch (Exception e) {
            log.error("监听用户奖品发送消息，消费失败 topic: {} message: {}", topic, message);
            throw e;
        }
    }
}

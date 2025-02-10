package cn.bugstack.trigger.listener;

import cn.bugstack.domain.award.event.SendAwardMessageEvent;
import cn.bugstack.domain.award.model.entity.DistributeAwardEntity;
import cn.bugstack.domain.award.service.IAwardService;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.event.BaseEvent;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

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
    public void listener(String message, Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
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
            if (e instanceof AppException && ((AppException) e).getCode().equals(ResponseCode.ACTIVITY_STATE_ERROR.getCode())) {
                log.warn("抽奖记录已经更新为 completed，无需重试。消息直接确认。");
                try {
                    channel.basicAck(tag, false);
                } catch (IOException ioException) {
                    log.error("手动确认消息失败", ioException);
                }
            } else {
                // 其它异常则根据需要拒绝消息（例如重新入队）
                log.error("监听用户奖品发送消息异常，消息拒绝重试。", e);
                try {
                    channel.basicReject(tag, true);
                } catch (IOException ioException) {
                    log.error("消息拒绝失败", ioException);
                }
            }
        }
    }
}

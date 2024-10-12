package cn.bugstack.domain.rebate.service;

import cn.bugstack.domain.rebate.event.SendRebateMessageEvent;
import cn.bugstack.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.bugstack.domain.rebate.model.entity.BehaviorEntity;
import cn.bugstack.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.model.entity.TaskEntity;
import cn.bugstack.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import cn.bugstack.domain.rebate.model.valobj.TaskStateVO;
import cn.bugstack.domain.rebate.repository.IBehaviorRebateRepository;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.event.BaseEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Service
public class BehaviorRebateService implements IBehaviorRebateService {

    @Resource
    private IBehaviorRebateRepository behaviorRebateRepository;

    @Resource
    private SendRebateMessageEvent sendRebateMessageEvent;

    @Override
    public List<String> createOrder(BehaviorEntity behaviorEntity) {
        // 1. 查询返利配置
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = behaviorRebateRepository.queryDailyRebateByBehaviorType(behaviorEntity.getBehaviorTypeVO().getCode());
        if(dailyBehaviorRebateVOS.isEmpty()) return Collections.emptyList();
        List<String> orderIds = new ArrayList<>();
        // 2. 构建聚合对象
        List<BehaviorRebateAggregate> behaviorRebateAggregates = new ArrayList<>();
        for (DailyBehaviorRebateVO dailyBehaviorRebateVO : dailyBehaviorRebateVOS){
            String bizId = behaviorEntity.getUserId()+ Constants.UNDERLINE+ dailyBehaviorRebateVO.getRebateType()+Constants.UNDERLINE+ behaviorEntity.getOutBusinessNo();
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .rebateType(dailyBehaviorRebateVO.getRebateType())
                    .bizId(bizId)
                    .behaviorType(behaviorEntity.getBehaviorTypeVO().getCode())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .orderId(RandomStringUtils.randomAlphanumeric(11))
                    .build();

            // 构建事件消息、 MQ 消息对象
            BaseEvent.EventMessage<SendRebateMessageEvent.SendRebateMessage> rebateMessageEventMessage =
                    sendRebateMessageEvent.buildEventMesaage(SendRebateMessageEvent.SendRebateMessage.builder()
                            .userId(behaviorEntity.getUserId())
                            .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                            .rebateType(dailyBehaviorRebateVO.getRebateType())
                                    .bizId(bizId)
                                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                            .build());

            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setUserId(behaviorEntity.getUserId());
            taskEntity.setMessage(rebateMessageEventMessage);
            taskEntity.setTopic(sendRebateMessageEvent.topic());
            taskEntity.setMessageId(rebateMessageEventMessage.getId());
            taskEntity.setState(TaskStateVO.create);

            orderIds.add(behaviorRebateOrderEntity.getOrderId());
            // 组装任务对象
            BehaviorRebateAggregate build = BehaviorRebateAggregate.builder()
                    .userId(behaviorEntity.getUserId())
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .build();
            behaviorRebateAggregates.add(build);
        }

        // 3. 存储聚合对象数据
        behaviorRebateRepository.saveUserRebateRecord(behaviorEntity.getUserId(), behaviorRebateAggregates);
        // 4. 返回订单ID集合

        return orderIds;
    }
}

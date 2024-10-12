package cn.bugstack.domain.rebate.model.aggregate;

import cn.bugstack.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.model.entity.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description 行为订单聚合实体
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorRebateAggregate {
    private String userId;
    private BehaviorRebateOrderEntity behaviorRebateOrderEntity;
    private TaskEntity taskEntity;
}

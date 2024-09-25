package cn.bugstack.domain.activity.model.aggregate;

import cn.bugstack.domain.activity.model.entity.ActivityAccountDayEntity;
import cn.bugstack.domain.activity.model.entity.ActivityAccountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityAccountMonthEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/09/13
 * @program bigmarket-14610-gwh
 * @description 参与活动订单聚合对象
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePartakeOrderAggregate {

    private String userId;

    private Long activityId;

    private ActivityAccountEntity activityAccountEntity;

    private ActivityAccountMonthEntity activityAccountMonthEntity;

    private boolean isExistAccountMonth = true;

    private ActivityAccountDayEntity activityAccountDayEntity;

    private boolean isExistAccountDay = true;

    private UserRaffleOrderEntity userRaffleOrderEntity;
}

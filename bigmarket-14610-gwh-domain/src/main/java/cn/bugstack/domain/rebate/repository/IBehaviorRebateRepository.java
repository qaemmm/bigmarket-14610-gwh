package cn.bugstack.domain.rebate.repository;

import cn.bugstack.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.bugstack.domain.rebate.model.entity.BehaviorEntity;
import cn.bugstack.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description 行为返利仓储
 **/
public interface IBehaviorRebateRepository {

    List<DailyBehaviorRebateVO> queryDailyRebateByBehaviorType(String behaviorType) ;


    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates);

    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo);
}

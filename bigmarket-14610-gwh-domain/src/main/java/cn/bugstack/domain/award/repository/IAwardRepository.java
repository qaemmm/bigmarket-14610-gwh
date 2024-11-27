package cn.bugstack.domain.award.repository;

import cn.bugstack.domain.award.model.aggregate.GiveOutPrizesAggregate;
import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 奖品仓储服务
 **/
public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    String queryAwardConfig(Integer awardId);

    void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate);

    String queryAwardKey(Integer awardId);
}

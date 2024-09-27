package cn.bugstack.domain.award.repository;

import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
}

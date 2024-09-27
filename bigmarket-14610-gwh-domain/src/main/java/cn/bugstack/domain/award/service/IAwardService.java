package cn.bugstack.domain.award.service;

import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @description 奖品服务
 **/
public interface IAwardService {
    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);
}

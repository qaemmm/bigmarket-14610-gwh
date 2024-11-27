package cn.bugstack.domain.award.service;

import cn.bugstack.domain.award.model.entity.DistributeAwardEntity;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @description 奖品服务
 **/
public interface IAwardService {
    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);
    /**
     * 配送发货奖品
     */
    void distributeAward(DistributeAwardEntity distributeAwardEntity);

}

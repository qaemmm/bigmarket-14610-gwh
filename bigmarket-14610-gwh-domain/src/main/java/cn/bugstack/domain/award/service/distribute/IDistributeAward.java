package cn.bugstack.domain.award.service.distribute;

import cn.bugstack.domain.award.model.entity.DistributeAwardEntity;

public interface IDistributeAward {

    void giveOutPrizes(DistributeAwardEntity distributeAwardEntity);
}

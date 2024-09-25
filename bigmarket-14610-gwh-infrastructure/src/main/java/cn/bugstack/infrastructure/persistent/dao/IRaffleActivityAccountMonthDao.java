package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.domain.activity.model.entity.ActivityAccountMonthEntity;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccountMonth;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IRaffleActivityAccountMonthDao {
    @DBRouter
    RaffleActivityAccountMonth queryActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonthReq);

    void insertActivityAccountMonth(RaffleActivityAccountMonth build);

    int updateActivityAccountMonthSubstractQuota(RaffleActivityAccountMonth raftActivityAccountMonth);
}

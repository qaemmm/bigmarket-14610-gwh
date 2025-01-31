package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.RaffleActivityAccountMonth;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IRaffleActivityAccountMonthDao {
    @DBRouter
    RaffleActivityAccountMonth queryActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonthReq);

    void insertActivityAccountMonth(RaffleActivityAccountMonth build);

    int updateActivityAccountMonthSubstractQuota(RaffleActivityAccountMonth raftActivityAccountMonth);

    void addAccountQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);
}

package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.UserRaffleOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@DBRouterStrategy(splitTable = true)
@Mapper
public interface IUserRaffleOrderDao {

    void insert(@Param("userRaffleOrder")UserRaffleOrder userRaffleOrder);

    @DBRouter
    UserRaffleOrder queryNoUsedRaffleOrder(UserRaffleOrder userRaffleOrderReq);

    @DBRouter
    int updateUserRaffleOrderStateUsed(@Param("userRaffleOrder")UserRaffleOrder userRaffleOrder);
}

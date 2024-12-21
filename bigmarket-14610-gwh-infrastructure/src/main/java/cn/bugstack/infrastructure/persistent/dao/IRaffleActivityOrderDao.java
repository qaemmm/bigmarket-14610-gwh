package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/08/30
 * @program bigmarket-14610-gwh
 * @description 抽奖活动单
 **/
@DBRouterStrategy(splitTable = true)
@Mapper
public interface IRaffleActivityOrderDao {
    @DBRouter(key = "userId")
    void insert(RaffleActivityOrder raffleActivityOrder);

    @DBRouter
    List<RaffleActivityOrder> queryRaffleActivityOrderByUserId(String userId);
    @DBRouter
    RaffleActivityOrder queryRaffleActivityOrder(RaffleActivityOrder raffleActivityOrderReq);

//    @DBRouter todo --这一块我觉的不用加的原因是因为他本身就会去库中匹配数据，所以不用发分库，但是可以试试
    int updateOrderCompleted(RaffleActivityOrder raffleActivityOrderReq);
}

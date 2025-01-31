package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.RaffleActivityAccount;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author fuzhouling
 * @date 2024/08/30
 * @program bigmarket-14610-gwh
 * @description 抽奖活动账户表
 **/
@Mapper
//@DBRouterStrategy(splitTable = true)
public interface IRaffleActivityAccountDao {
    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    void insert(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryActivityAccount(RaffleActivityAccount raffleActivityAccountEntityReq);

    void updateActivityAccountDayMirrorQuota(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountMonthMirrorQuota(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountSubstractQuota(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryActivityAccountEntity(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryUserPartakeCount(RaffleActivityAccount raffleActivityAccount);


    RaffleActivityAccount queryAccountByUserId(RaffleActivityAccount raffleActivityAccount);
}

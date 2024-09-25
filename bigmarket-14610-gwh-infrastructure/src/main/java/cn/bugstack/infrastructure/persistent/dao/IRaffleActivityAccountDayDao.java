package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.domain.activity.model.entity.ActivityAccountDayEntity;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccount;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccountDay;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author fuzhouling
 * @date 2024/09/12
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Mapper
public interface IRaffleActivityAccountDayDao {
    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDayReq);

    void insertActivityAccountDay(RaffleActivityAccountDay build);

    int updateActivityAccountDaySubstractQuota(RaffleActivityAccountDay build);
}

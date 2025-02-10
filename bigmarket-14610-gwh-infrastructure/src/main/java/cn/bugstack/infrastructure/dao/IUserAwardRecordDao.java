package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.UserAwardRecord;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;


@DBRouterStrategy(splitTable = true)
@Mapper
public interface IUserAwardRecordDao {
    void insert(UserAwardRecord userAwardRecord);

    @DBRouter
    int updateAwardRecordCompletedState(UserAwardRecord userAwardRecordReq);


    @DBRouter
    UserAwardRecord queryUserAwardRecord(UserAwardRecord userAwardRecord);
}

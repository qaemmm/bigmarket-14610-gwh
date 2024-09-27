package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserAwardRecord;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;


@DBRouterStrategy(splitTable = true)
@Mapper
public interface IUserAwardRecordDao {
    void insert(UserAwardRecord userAwardRecord);
}

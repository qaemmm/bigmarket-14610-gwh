package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserCreditAccount;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//@DBRouterStrategy(splitTable = true)
public interface IUserCreditAccountDao {


    int updateAddAmount(UserCreditAccount userCreditAccountReq);

    void insert(UserCreditAccount userCreditAccountReq);

    UserCreditAccount queryUserCreditAccount(UserCreditAccount userCreditAccountReq);
}

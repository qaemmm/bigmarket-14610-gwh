package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author fuzhouling
 * @date 2024/08/30
 * @program bigmarket-14610-gwh
 * @description 抽奖活动账户表
 **/
@Mapper
public interface IRaffleActivityAccountDao {
    int updateAccoutQuota(RaffleActivityAccount raffleActivityAccount);

    void insert(RaffleActivityAccount raffleActivityAccount);
}

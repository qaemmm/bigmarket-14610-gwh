package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author fuzhouling
 * @date 2024/08/30
 * @program bigmarket-14610-gwh
 * @description 抽奖活动表
 **/
@Mapper
public interface IRaffleActivityDao {
    RaffleActivity selectByActivityId(Long activityId);

    RaffleActivity queryRaffleActivityByActivityId(Long activityId);

    Long queryActivityIdByStrategyId(Long strategyId);

    Long queryStrategyIdByActivityId(Long activityId);
}

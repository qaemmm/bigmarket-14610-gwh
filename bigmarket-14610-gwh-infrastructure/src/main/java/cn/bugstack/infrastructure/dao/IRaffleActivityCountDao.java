package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author fuzhouling
 * @date 2024/08/30
 * @program bigmarket-14610-gwh
 * @description 抽奖活动次数配置表
 **/
@Mapper
public interface IRaffleActivityCountDao {
    RaffleActivityCount queryActivityCountByActivityCountId(Long activityCountId);
}

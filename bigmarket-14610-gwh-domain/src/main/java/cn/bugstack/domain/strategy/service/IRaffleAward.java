package cn.bugstack.domain.strategy.service;


import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

import java.util.List;

public interface IRaffleAward {
    /**
     * 查询所有的抽奖奖品列表
     * @param strategyId 奖品策略id
     * @return 奖品列表集合
     */
    List<StrategyAwardEntity> queryRaffleAwardList(Long strategyId);

    /**
     * 查询所有的抽奖奖品列表
     *
     * @param activityId 奖品策略id
     * @return 奖品列表集合
     */
    List<StrategyAwardEntity> queryRaffleAwardListByActivityId(Long activityId);

    List<StrategyAwardStockKeyVO> queryOpenActivityStrategyAwardList();

}

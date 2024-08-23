package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockVO;

/**
 *
 * 抽奖库存相关服务，获取库存消耗队列
 * */
public interface IRaffleStock {
    /**
     * 获取库存消耗队列
     * */
    StrategyAwardStockVO takeQueue();

    /**
     * 更新策略奖品库存
     * */
    void updateStrategyAwardStock(Long strategyId,Integer awardId);
}

package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockVO;

public interface IRaffleStock {
    StrategyAwardStockVO takeQueue();

    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}

package cn.bugstack.domain.strategy.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;
import java.util.Map;

public interface IStrategyRepository {
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(Long strategyId, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    Integer getStrategyAwardAssemble(Long strategyId,Integer rateKey);

    int getRateRange(Long StragegyId);
}

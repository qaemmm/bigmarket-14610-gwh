package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.infrastructure.persistent.dao.IStrategyAwardDao;
import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import org.checkerframework.checker.units.qual.C;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/08/14
 * @program bigmarket-14610-gwh
 * @description 策略服务仓储实现
 **/
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;
    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY+ strategyId;
        List<StrategyAwardEntity> strategiesAwardEntities =  redisService.getValue(cacheKey);
        if(null!= strategiesAwardEntities&&!strategiesAwardEntities.isEmpty()){
            return strategiesAwardEntities;
        }
        //查询数据库
        List<StrategyAward> strategyAwards = strategyAwardDao.getStrategyListByStrategyId(strategyId);
        strategiesAwardEntities = new ArrayList<>();
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardId(strategyAward.getAwardId())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            strategiesAwardEntities.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey,strategiesAwardEntities);
        return strategiesAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(Long strategyId, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+strategyId, rateRange);
        Map<Integer,Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, Integer rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, rateKey);
    }

    @Override
    public int getRateRange(Long StragegyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+StragegyId);
    }
}

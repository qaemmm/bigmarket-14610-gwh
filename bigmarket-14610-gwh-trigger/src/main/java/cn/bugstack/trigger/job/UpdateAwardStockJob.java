package cn.bugstack.trigger.job;

import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockVO;
import cn.bugstack.domain.strategy.service.IRaffleAward;
import cn.bugstack.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fuzhouling
 * @date 2024/08/22
 * @program bigmarket-14610-gwh
 * @description 更新奖品库存任务；为了不让跟新库存的压力打到数据库中，这里采用redis更新库存，异步队列更新数据库，确保数据库的最红一致性。
 **/
@Slf4j
@Component
public class UpdateAwardStockJob {
    @Resource
    private IRaffleStock raffleStock;
    @Resource
    private IRaffleAward raffleAward;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
       try {
           //将open活动下的，所有策略奖品给查出来
           List<StrategyAwardStockKeyVO> strategyAwardStockKeyVOList = raffleAward.queryOpenActivityStrategyAwardList();
           for (StrategyAwardStockKeyVO strategyAwardStockKeyVO : strategyAwardStockKeyVOList){
               threadPoolExecutor.execute(()->{
                   StrategyAwardStockVO strategyAwardStockVO= raffleStock.takeQueue(strategyAwardStockKeyVO.getStrategyId(),strategyAwardStockKeyVO.getAwardId());
                   if (strategyAwardStockVO != null){
                       return;
                   }
                   log.info("定时任务，更新奖品消耗库存 strategyId:{} awardId:{}", strategyAwardStockVO.getStrategyId(), strategyAwardStockVO.getAwardId());
                   raffleStock.updateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
               });
           }
       }catch (Exception e){
           log.error("定时任务，更新数据库奖品库存异常",e);
       }
    }

}

package cn.bugstack.trigger.job;

import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockVO;
import cn.bugstack.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
       try {
           StrategyAwardStockVO strategyAwardStockVO = raffleStock.takeQueue();
           if (null == strategyAwardStockVO) {
               return;
           }
           Integer awardId = strategyAwardStockVO.getAwardId();
           Long strategyId = strategyAwardStockVO.getStrategyId();
           raffleStock.updateStrategyAwardStock(strategyId, awardId);
       }catch (Exception e){
           log.error("定时任务，更新数据库奖品库存异常",e);
       }
    }

}

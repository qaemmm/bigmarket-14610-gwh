package cn.bugstack.trigger.job;

import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.bugstack.domain.activity.service.IRaffleActivitySkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fuzhouling
 * @date 2024/09/10
 * @program bigmarket-14610-gwh
 * @description 更新活动sku库存任务
 **/
@Slf4j
@Component
public class UpdateActivitySkuStockJob {

    @Resource
    private IRaffleActivitySkuStockService skuStock;

    @Resource
    private ThreadPoolExecutor executor;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
        try{
            //获取当前活动的所有sku库存
            List<Long> skus = skuStock.querySkuList();
            for (Long sku : skus){
                //todo --这一块使用线程池，为什么？
                executor.execute(()->{
                    ActivitySkuStockKeyVO activitySkuStockKeyVO =null;
                    try{
                        activitySkuStockKeyVO = skuStock.takeQueueValve(sku);
                    }catch (InterruptedException e){
                        log.error("定时任务，更新活动sku库存失败 sku: {} ", sku);
                    }
                    if (null == activitySkuStockKeyVO) {
                        return;
                    }
                    log.info("定时任务，更新活动sku库存 sku:{} activityId:{}", activitySkuStockKeyVO.getSku(), activitySkuStockKeyVO.getActivityId());
                    skuStock.updateActivitySkuStock(activitySkuStockKeyVO.getSku());
                });
            }
        }catch (Exception e) {
            log.error("定时任务，更新活动sku库存失败", e);
        }
    }
}

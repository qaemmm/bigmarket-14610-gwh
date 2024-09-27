package cn.bugstack.trigger.job;

import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.bugstack.domain.activity.service.IRaffleActivitySkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
        try{
            ActivitySkuStockKeyVO activitySkuStockKeyVO = skuStock.takeQueueValve();
            if (null == activitySkuStockKeyVO) {
                return;
            }
            skuStock.updateActivitySkuStock(activitySkuStockKeyVO.getSku());

        }catch (Exception e) {
            log.error("定时任务，更新活动sku库存失败", e);
        }
    }
}

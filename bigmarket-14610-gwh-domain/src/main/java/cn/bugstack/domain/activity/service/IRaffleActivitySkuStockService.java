package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @description: 活动sku库存处理接口
 */
public interface IRaffleActivitySkuStockService {

    /**
     * 获取活动sku库存队列
     * @return 奖品库存Key信息
     * @throws InterruptedException
     */
    ActivitySkuStockKeyVO takeQueueValve() throws InterruptedException;


    /**
     * 获取活动sku库存队列
     * @return 奖品库存Key信息
     * @param sku
     * @throws InterruptedException
     */
    ActivitySkuStockKeyVO takeQueueValve(Long sku) throws InterruptedException;
    /**
     * 清空队列
     *
     */
    void clearQueueValue();

    /**
     * 清空队列
     *
     */
    void clearQueueValue(Long sku);

    /**
     *延迟认列*任务趋势更新活动shu库存
     *
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 缓存库存以消耗完毕，清空数据库库存
     *
     */
    void clearActivitySkuStock(Long sku);

    List<Long> querySkuList();

}

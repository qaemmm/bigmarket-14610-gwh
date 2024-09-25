package cn.bugstack.domain.activity.service.armory;

import java.util.Date;

public interface IActivityDispatch {

    /**
     * 缓存活动商品库存预热的
     * @param sku
     * @param stockCount
     */
    void cacheActivitySkuStockCount(Long sku,Integer stockCount);

    /**
     * 活动商品库存扣减
     * @param sku
     * @param endDateTime
     * @return
     */
    boolean subtractionActivitySkuStock(Long sku, Date endDateTime);
}

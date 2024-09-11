package cn.bugstack.domain.activity.service.armory;

import java.util.Date;

public interface IActivityDispatch {

    void cacheActivitySkuStockCount(Long sku,Integer stockCount);

    boolean subtractionActivitySkuStock(Long sku, Date endDateTime);
}

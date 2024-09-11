package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Mapper
public interface IRaffleActivitySkuDao {

    RaffleActivitySku queryActivityBySku(Long sku);

    void clearActivitySkuStock(Long sku);

    void updateActivitySkuStock(Long sku);
}

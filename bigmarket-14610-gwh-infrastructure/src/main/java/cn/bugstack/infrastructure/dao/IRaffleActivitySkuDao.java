package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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

    List<RaffleActivitySku> queryActivityByActivityId(Long activityId);

    List<Long> querySkuList();

}

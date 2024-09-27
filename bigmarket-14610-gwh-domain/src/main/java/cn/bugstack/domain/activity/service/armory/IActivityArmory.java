package cn.bugstack.domain.activity.service.armory;

/**
 * @author fuzhouling
 * @date 2024/09/10
 * @program bigmarket-14610-gwh
 * @description 活动装配
 **/
public interface IActivityArmory {

    /**
     * 构建活动策略
     *
     * @param activityId
     * @return 是否
     */
    boolean assembleActivityByActivityId(Long activityId);

    /**
     * 构建活动策略
     *
     * @param sku
     * @return 是否
     */
    boolean assembleActivitySku(Long sku);

}

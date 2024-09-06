package cn.bugstack.domain.activity.service.rule.impl;

import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.service.rule.AbstractActionChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description 基础信息【有效期、状态】
 **/
@Slf4j
@Component("activity_base_action")
public class ActivityBaseActionChain extends AbstractActionChain {

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-基础信息【有效期、状态】校验开始。");
        Date beginDateTime = activityEntity.getBeginDateTime();
        Date endDateTime = activityEntity.getEndDateTime();
        //如果当前时间小于起始时间，则该活动未开始；
        //如果当前时间大于结束时间，则该活动已结束；
        if (new Date().before(beginDateTime)) {
            log.info("活动责任链-基础信息【有效期、状态】校验失败，活动未开始。");
            return false;
        }
        if (new Date().after(endDateTime)) {
            log.info("活动责任链-基础信息【有效期、状态】校验失败，活动已结束。");
            return false;
        }
        ActivityStateVO state = activityEntity.getState();
        //如果活动状态不是进行中，则不允许下单；
        if (!state.equals(ActivityStateVO.create)) {
            log.info("活动责任链-基础信息【有效期、状态】校验失败，活动状态不是进行中。");
            return false;
        }


        return next().action(activitySkuEntity, activityEntity, activityCountEntity);
    }
}

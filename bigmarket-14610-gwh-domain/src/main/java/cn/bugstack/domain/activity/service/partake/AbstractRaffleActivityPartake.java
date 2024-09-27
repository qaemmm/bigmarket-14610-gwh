package cn.bugstack.domain.activity.service.partake;

import cn.bugstack.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.IRaffleActivityPartakeService;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/13
 * @program bigmarket-14610-gwh
 * @description 活动参与抽象类
 **/
@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {

    protected final IActivityRepository activityRepository;

    public AbstractRaffleActivityPartake(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public UserRaffleOrderEntity createOrder(String userId, Long activityId){
        return this.createOrder(PartakeRaffleActivityEntity.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .build()
        );
    }
    @Override
    public UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        //1、基础信息
        String userId = partakeRaffleActivityEntity.getUserId();
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        Date currentDate = new Date();

        //2、活动查询
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);

        //2.1 校验活动装填
        if(!ActivityStateVO.open.equals(activityEntity.getState())){
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        // 2.2 校验活动时间
        if(currentDate.before(activityEntity.getBeginDateTime()) || currentDate.after(activityEntity.getEndDateTime())){
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }

        //3.查询未被使用的活动参与订单记录
        UserRaffleOrderEntity userRaffleOrderEntity = activityRepository.queryNoUsedRaffleOrder(partakeRaffleActivityEntity);
        if(null != userRaffleOrderEntity){
            log.info("创建参与活动订单[已经创建没有使用] userId:{} activityId:{} userRaffleOrderEntity:{}", userId, activityId, JSON.toJSONString(userRaffleOrderEntity));
            return userRaffleOrderEntity;
        }
        //4、账户额度过滤&返回账户构建聚合对象--总额度、月额度、日额度（月、日分别有一个标志位）
        CreatePartakeOrderAggregate createPartakeOrderAggregate = this.doFilterAccount(userId, activityId,currentDate);

        //5、创建订单,这一块返回订单对象之后，赋值给聚合对象
        UserRaffleOrderEntity userRaffleOrder = this.buildUserRaffleOrder(userId,activityId,currentDate);

        //6、聚合对象赋值
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrder);

        //7、保存聚合对象--一个领域内的一个聚合是一个事务
        activityRepository.saveCteatePartakeOrderAggregate(createPartakeOrderAggregate);


        return userRaffleOrder;
    }

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate);

    //这块封装了账户额度的过滤（总额度，月额度、日额度），返回一个聚合对象
    protected abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate);
}

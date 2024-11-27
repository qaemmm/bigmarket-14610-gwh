package cn.bugstack.trigger.http;


import cn.bugstack.domain.activity.model.entity.ActivityAccountEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.activity.service.IRaffleActivityPartakeService;
import cn.bugstack.domain.activity.service.armory.IActivityArmory;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.model.valobj.AwardStateVO;
import cn.bugstack.domain.award.service.IAwardService;
import cn.bugstack.domain.rebate.model.entity.BehaviorEntity;
import cn.bugstack.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.bugstack.domain.rebate.service.IBehaviorRebateService;
import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.trigger.api.IRaffleActivityService;
import cn.bugstack.trigger.api.dto.ActivityDrawRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityDrawResponseDTO;
import cn.bugstack.trigger.api.dto.UserActivityAccountRequestDTO;
import cn.bugstack.trigger.api.dto.UserActivityAccountResponseDTO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/08/28
 * @program bigmarket-14610-gwh
 * @description 抽奖服务
 **/
@RestController
@Slf4j
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity/")
public class RaffleActivityController implements IRaffleActivityService {

    @Resource
    private IActivityArmory activityArmory;
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;

    @Resource
    private IAwardService awardService;

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @Resource
    private IBehaviorRebateService behaviorRebateService;

    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 活动装配 - 数据预热 | 把活动配置的对应的 sku 一起装配
     *
     * @param activityId 活动ID
     * @return 装配结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/armory">/api/v1/raffle/activity/armory</a>
     * 入参：{"activityId":100001,"userId":"xiaofuge"}
     * <p>
     * curl --request GET \
     * --url 'http://localhost:8091/api/v1/raffle/activity/armory?activityId=100301'
     */
    @RequestMapping(value ="armory",method = RequestMethod.GET)
    @Override
    public Response<Boolean> armory(@RequestParam Long activityId) {
        try{
            log.info("活动装配，数据预热，开始 activityId:{}", activityId);
            //活动装配-activitySkuEntity
            activityArmory.assembleActivityByActivityId(activityId);
            //策略装配-StrategyAwardEntity
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
            log.info("活动装配，数据预热，完成 activityId:{}", activityId);
            return response;
        }catch (Exception e){
            log.error("抽奖策略装配异常 activityId{}", activityId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 抽奖接口
     *
     * @param request 请求对象
     * @return 抽奖结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     * 入参：{"activityId":100001,"userId":"xiaofuge"}
     * <p>
     * curl --request POST \
     * --url http://localhost:8091/api/v1/raffle/activity/draw \
     * --header 'content-type: application/json' \
     * --data '{
     * "userId":"xiaofuge",
     * "activityId": 100301
     * }'
     */
    @RequestMapping(value = "draw", method = RequestMethod.POST)
    @Override
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO request) {
        try {
            log.info("活动抽奖 userId:{} activityId:{}", request.getUserId(), request.getActivityId());
            // 1. 参数校验
            if (StringUtils.isBlank(request.getUserId()) || null == request.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),
                        ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2、参与活动--创建与记录订单；
            UserRaffleOrderEntity orderEntity = raffleActivityPartakeService.createOrder(request.getUserId(), request.getActivityId());
            log.info("活动抽奖，创建订单 userId:{} activityId:{} orderId:{}", request.getUserId(), request.getActivityId(), orderEntity.getOrderId());
            // 3、抽奖策略--执行抽奖；
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId(orderEntity.getUserId())
                    .strategyId(orderEntity.getStrategyId())
                    .build());

            UserAwardRecordEntity userAwardRecord = UserAwardRecordEntity.builder()
                    .userId(orderEntity.getUserId())
                    .activityId(orderEntity.getActivityId())
                    .strategyId(orderEntity.getStrategyId())
                    .orderId(orderEntity.getOrderId())
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardTime(new Date())
                    .awardConfig(raffleAwardEntity.getAwardConfig())
                    .awardState(AwardStateVO.create)
                    .build();
            // 4、存放结果--写入中奖记录
            awardService.saveUserAwardRecord(userAwardRecord);

            // 5. 返回结果
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityDrawResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();

    } catch(AppException e){
        log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
        return Response.<ActivityDrawResponseDTO>builder()
                .code(e.getCode())
                .info(e.getInfo())
                .build();
    } catch(Exception e){
        log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
        return Response.<ActivityDrawResponseDTO>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .build();
    }
    }

    /*
        * 日历签到返利接口
        *
        * 这一块签到的话默认会创建一个订单，根据你日常的一个行为然后给你返利，
        * 然后组装成一个用户行为记录（这一块对用户订单-行为-返利配置-唯一订单id）+任务实体（这一块作为消息队列进行发送）
     */
    @RequestMapping(value = "calendar_sign_rebate", method = RequestMethod.POST)
    public Response<Boolean> calendarSignRebate(@RequestParam String userId){
        try{
            BehaviorEntity behaviorEntity = new BehaviorEntity();
            behaviorEntity.setUserId(userId);
            behaviorEntity.setBehaviorTypeVO(BehaviorTypeVO.SIGN);
            behaviorEntity.setOutBusinessNo(dateFormatDay.format(new Date()));
            List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);
            log.info("日历签到返利完成 userId:{} orderIds: {}", userId, JSON.toJSONString(orderIds));
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (AppException e) {
            log.error("日历签到返利异常 userId:{} ", userId, e);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("日历签到返利失败 userId:{}", userId);
            log.error("日历签到返利失败 ", e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }
    @RequestMapping(value = "is_calendar_sign_rebate", method = RequestMethod.POST)
    @Override
    public Response<Boolean> isCalendarSignRebate(@RequestParam String userId) {
       try{
           //操作user_behavior_rebate_order表,同时新增一个out_business_no字段
           String outBusinessNo = dateFormatDay.format(new Date());
           List<BehaviorRebateOrderEntity> behaviorEntityList =  behaviorRebateService.queryOrderByOutBusinessNo(userId, outBusinessNo);
           log.info("查询用户是否完成日历签到返利完成 userId:{} orders.size:{}", userId, behaviorEntityList.size());
           return Response.<Boolean>builder()
                       .code(ResponseCode.SUCCESS.getCode())
                       .info(ResponseCode.SUCCESS.getInfo())
                       .data(!behaviorEntityList.isEmpty())
                       .build();
       }catch (Exception e){
              log.error("查询用户是否完成日历签到返利异常 userId:{}", userId, e);
              return Response.<Boolean>builder()
                     .code(ResponseCode.UN_ERROR.getCode())
                     .info(ResponseCode.UN_ERROR.getInfo())
                     .data(false)
                     .build();
       }
    }
    @RequestMapping(value = "query_user_activity_account", method = RequestMethod.POST)
    @Override
    public Response<UserActivityAccountResponseDTO> queryUserActivityAccount(@RequestBody UserActivityAccountRequestDTO request) {
        //操作user_activity_account表--能够通过userId、activityId获取到总、月、日
        try {
            log.info("查询用户活动账户 userId:{} activityId:{}", request.getUserId(), request.getActivityId());
            ActivityAccountEntity activityAccountEntity = raffleActivityAccountQuotaService.queryActivityAccountEntity(request.getUserId(), request.getActivityId());
            UserActivityAccountResponseDTO userActivityAccountResponseDTO = UserActivityAccountResponseDTO.builder()
                    .totalCount(activityAccountEntity.getTotalCount())
                    .totalCountSurplus(activityAccountEntity.getTotalCountSurplus())
                    .dayCount(activityAccountEntity.getDayCount())
                    .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                    .monthCount(activityAccountEntity.getMonthCount())
                    .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                    .build();
            log.info("查询用户活动账户开始 userId:{} activityId:{} dto:{}", request.getUserId(), request.getActivityId(), JSON.toJSONString(userActivityAccountResponseDTO));
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userActivityAccountResponseDTO)
                    .build();
        }catch (Exception e){
            log.error("查询用户活动账户异常 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}

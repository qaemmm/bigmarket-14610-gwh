package cn.bugstack.domain.activity.service.quota;

import cn.bugstack.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.activity.service.quota.policy.ITradePolicy;
import cn.bugstack.domain.activity.service.quota.rule.IActionChain;
import cn.bugstack.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Slf4j
public abstract class AbstractRaffleActivityAccountQuota extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {
    private final Map<String, ITradePolicy> tradePolicyMap;
    public AbstractRaffleActivityAccountQuota(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory,Map<String, ITradePolicy> tradePolicyGroup) {
        super(activityRepository, defaultActivityChainFactory);
        this.tradePolicyMap =tradePolicyGroup;
    }


    public String createOrder(SkuRechargeEntity skuRechargeEntity){
        //1 参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if(null==userId||null == sku||null==outBusinessNo){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        //2 查询基础信息
        //2.1 通过sku查询活动信息
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        //2.2 查询活动信息
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //2.3（用户在活动上可参与的次数）
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        // 3、活动动作规则校验 todo 后续处理规则过滤流程，暂时也不处理责任链结果
        IActionChain actionChain = defaultActivityChainFactory.openActionChain();
        actionChain.action(activitySkuEntity, activityEntity, activityCountEntity);

        // 4、构建订单聚合对象
        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildOrderAggregate(skuRechargeEntity, activitySkuEntity, activityEntity, activityCountEntity);


        //5、交易策略
        ITradePolicy iTradePolicy = tradePolicyMap.get(skuRechargeEntity.getOrderTradeType().getCode());
        //6、通过不同策略来创建订单
        iTradePolicy.trade(createQuotaOrderAggregate);

        // 6、返回订单号
        return createQuotaOrderAggregate.getActivityOrderEntity().getOrderId();

    }

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);


}

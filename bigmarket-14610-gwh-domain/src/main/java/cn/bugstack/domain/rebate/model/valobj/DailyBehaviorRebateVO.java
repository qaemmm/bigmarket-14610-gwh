package cn.bugstack.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Getter
@AllArgsConstructor
public class DailyBehaviorRebateVO {
    //行为类型（sign 签到、openai_pay 支付）
    private String behaviorType;
    //返利描述
    private String rebateDesc;
    //返利类型（sku 活动库存充值商品、integral 用户活动积分）
    private String rebateType;
    //返利配置
    private String rebateConfig;
}

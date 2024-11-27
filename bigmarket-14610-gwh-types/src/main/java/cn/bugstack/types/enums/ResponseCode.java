package cn.bugstack.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    INDEX_DUP("0003", "唯一索引冲突"),

    STRATEGY_RULE_WIGHT_IS_NULL("ERR_BIZ_001","业务异常，策略规则中rule_weight权重规则已适用但未配置"),
    UN_ASSEMBLED_STRATEGY_ARMORY("ERR_BIZ_002","业务异常，未组装策略库存信息"),
    ACTIVITY_DATE_ERROR("ERR_BIZ_003","业务异常，时间错误"),
    ACTIVITY_STATE_ERROR("ERR_BIZ_004", "业务异常，活动状态错误"),
    ACTIVITY_SKU_STOCK_ERROR("ERR_BIZ_005", "业务异常，活动库存错误"),
    //总账户不足、月账户不足、日账户不足
    ACCOUNT_QUOTA_ERROR("ERR_BIZ_006", "业务异常，账户额度不足"),
    ACCOUNT_MONTH_QUOTA_ERROR("ERR_BIZ_007", "业务异常，月账户额度不足"),
    ACCOUNT_DAY_QUOTA_ERROR("ERR_BIZ_008", "业务异常，日账户额度不足"),
    AWARD_CONFIG_ERROR("ERR_CONFIG_001", "award_config 配置不是一个范围值，如 1,100"),
    ;

    private String code;
    private String info;

}

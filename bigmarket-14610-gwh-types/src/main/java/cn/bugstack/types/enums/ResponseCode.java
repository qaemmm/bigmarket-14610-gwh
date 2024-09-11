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
    ACTIVITY_SKU_STOCK_ERROR("ERR_BIZ_005", "业务异常，活动库存错误")
    ;

    private String code;
    private String info;

}

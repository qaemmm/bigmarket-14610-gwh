package cn.bugstack.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description 行为类型枚举
 **/
@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {
    SIGN("sign","签到"),
    OPENAI_PAY("openai_pay","开通AI");
    private final String code;
    private final String desc;

}

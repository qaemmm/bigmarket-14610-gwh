package cn.bugstack.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 任务状态值对象
 **/
@Getter
@AllArgsConstructor
public enum TradeTypeVO {
    FORWARD("forward","正向交易+积分"),
    REVERSE("reverse","逆向交易-积分")
    ;
    private final String code;
    private final String desc;
}

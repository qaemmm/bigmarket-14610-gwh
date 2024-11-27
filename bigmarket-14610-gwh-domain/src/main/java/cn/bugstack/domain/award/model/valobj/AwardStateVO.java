package cn.bugstack.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 奖品状态值对象
 **/
@Getter
@AllArgsConstructor
public enum AwardStateVO {

    create("create", "创建"),
    complete("complete", "发奖完成"),
    fail("fail", "发奖失败"),
    ;
    private final String code;
    private final String desc;


}

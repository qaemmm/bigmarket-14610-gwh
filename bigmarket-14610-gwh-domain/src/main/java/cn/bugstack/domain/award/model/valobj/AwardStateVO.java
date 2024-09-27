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
    used("completed", "已完成");
    private final String code;
    private final String desc;


}

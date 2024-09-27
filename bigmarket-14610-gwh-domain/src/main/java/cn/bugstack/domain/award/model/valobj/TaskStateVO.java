package cn.bugstack.domain.award.model.valobj;

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
public enum TaskStateVO {
    //任务状态；create-创建、completed-完成、fail-失败
    create("create", "创建"),
    completed("completed", "完成"),
    fail("fail", "失败");
    private final String code;
    private final String desc;
}

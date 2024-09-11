package cn.bugstack.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei 
 * @description 活动状态值对象
 * @create 2024-03-16 11:16
 */
@Getter
@AllArgsConstructor
public enum ActivityStateVO {

    create("create", "创建"),
    open("open", "打开"),
    close("close", "关闭"),
    ;
    private final String code;
    private final String desc;

}

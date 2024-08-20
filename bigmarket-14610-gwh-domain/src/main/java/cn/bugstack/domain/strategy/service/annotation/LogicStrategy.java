package cn.bugstack.domain.strategy.service.annotation;

import cn.bugstack.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Fuzhengwei bugstack.cn
 * @description 策略自定义枚举:增加一个自定义注解，方便对象往里注入
 * @create 2023-12-31 11:29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicStrategy {
    DefaultLogicFactory.LogicModel logicMode();
}

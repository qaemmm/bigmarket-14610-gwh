package cn.bugstack.domain.activity.service.quota.rule.factory;

import cn.bugstack.domain.activity.service.quota.rule.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description 责任链工厂
 **/
@Service
public class DefaultActivityChainFactory{

    private final IActionChain actionChain;

    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainGroup) {
        actionChain = actionChainGroup.get(ActionModel.ACTIVITY_BASE_ACTION.getCode());
        actionChain.appendNext(actionChainGroup.get(ActionModel.ACTIVITY_SKU_STOCK_ACTION.getCode()));
    }

    public IActionChain openActionChain() {
        return this.actionChain;
    }


    @Getter
    @AllArgsConstructor
    public enum ActionModel {
        ACTIVITY_BASE_ACTION("activity_base_action", "活动基本行为"),
        ACTIVITY_SKU_STOCK_ACTION("activity_sku_stock_action", "权重规则")
        ;

        private final String code;
        private final String info;

    }
}

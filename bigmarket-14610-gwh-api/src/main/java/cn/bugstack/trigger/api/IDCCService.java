package cn.bugstack.trigger.api;

import cn.bugstack.types.model.Response;

public interface IDCCService {
    /**
     * 更新配置
     * @param key
     * @param value
     * @return
     */
    Response<Boolean> updateConfig(String key, String value);
}

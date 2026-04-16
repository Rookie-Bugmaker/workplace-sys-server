package com.crrc.sys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 服务地址配置类
 * 从 application.yml 读取 Skywalking 和 Kibana 服务地址（通过 filters 引入不同环境配置）
 */
@Component
@RefreshScope
public class EnvironmentConfig {

    /**
     * Skywalking 链路追踪地址（从 filters 配置文件读取）
     */
    @Value("${service.address.skywalking:}")
    private String skywalking;

    /**
     * Kibana 日志管理地址（从 filters 配置文件读取）
     */
    @Value("${service.address.kibana:}")
    private String kibana;

    /**
     * 获取 Skywalking 地址
     */
    public String getSkywalking() {
        return skywalking;
    }

    /**
     * 获取 Kibana 地址
     */
    public String getKibana() {
        return kibana;
    }

    /**
     * 根据服务类型获取地址
     *
     * @param serviceType 服务类型：skywalking 或 kibana
     * @return 服务地址
     */
    public String getServiceAddress(String serviceType) {
        if (serviceType == null) {
            return null;
        }
        switch (serviceType.toLowerCase()) {
            case "skywalking":
                return skywalking;
            case "kibana":
                return kibana;
            default:
                return null;
        }
    }
}

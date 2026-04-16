package com.crrc.sys.controller;

import com.crrc.sys.config.EnvironmentConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务配置接口
 * 根据服务类型返回对应的服务地址配置
 */
@RestController
@RequestMapping("/api/service-config")
public class ServiceConfigController {

    @Autowired
    private EnvironmentConfig environmentConfig;

    /**
     * 根据服务类型获取服务地址配置
     *
     * @param serviceType 服务类型：skywalking 或 kibana
     * @return 服务地址配置
     */
    @GetMapping("/address")
    public Map<String, Object> getServiceAddress(@RequestParam String serviceType) {
        Map<String, Object> result = new HashMap<>();

        String address = environmentConfig.getServiceAddress(serviceType);
        if (address != null && !address.isEmpty()) {
            result.put("success", true);
            result.put("serviceType", serviceType);
            result.put("address", address);
            result.put("message", "获取" + serviceType + "地址成功");
        } else {
            result.put("success", false);
            result.put("message", "未找到" + serviceType + "服务地址，请检查配置文件是否已配置");
        }

        return result;
    }
}

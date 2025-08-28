package com.yunling.sys.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;

/**
 * @author 谭永强
 * @date 2024-12-03
 */
@RestController
public class StopController {

    private final static Logger logger = LoggerFactory.getLogger(StopController.class);
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    /**
     * 反注册
     *
     * @throws NacosException 异常
     */
    @GetMapping("/deregister")
    public void stop() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosDiscoveryProperties.getServerAddr());
        properties.put(PropertyKeyConst.NAMESPACE, nacosDiscoveryProperties.getNamespace());
        properties.put(PropertyKeyConst.USERNAME, nacosDiscoveryProperties.getUsername());
        properties.put(PropertyKeyConst.PASSWORD, nacosDiscoveryProperties.getPassword());
        String serviceName = nacosDiscoveryProperties.getService();
        final NamingService namingService = NamingFactory.createNamingService(properties);
        final List<Instance> instancesList = namingService.getAllInstances(serviceName);
        final NamingMaintainService maintainService = NamingMaintainFactory.createMaintainService(properties);
        for (Instance instance : instancesList) {
            if (instance.getIp().equals(nacosDiscoveryProperties.getIp())) {
                instance.setWeight(0);
                instance.setEnabled(false);
                maintainService.updateInstance(serviceName, instance);
                logger.info("取消注册，服务名={},ip={},权重={},上线={}", serviceName, instance.getIp(), instance.getWeight(), instance.isEnabled());
            }
        }
    }
}

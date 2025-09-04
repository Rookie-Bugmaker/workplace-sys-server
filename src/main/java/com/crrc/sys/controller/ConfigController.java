package com.crrc.sys.controller;

import com.crrc.sys.common.RSAUtils;
import com.crrc.sys.model.server.ConfigRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "系统配置")
@RestController
@Slf4j
@RequestMapping("config")
public class ConfigController {

    @Value("${crrc.config.captcha.enable:true}")
    private boolean captchaEnable;

    @Value("${springdoc.swagger-ui.host:localhost}")
    private String docHost;

    @Value("${springdoc.swagger-ui.port:9091}")
    private String docPort;

    @Resource
    private RSAUtils rsaUtils;

    @GetMapping("/get")
    @Operation(summary = "查询配置")
    public ConfigRespVO getConfig() throws Exception {
        ConfigRespVO configRespVO = new ConfigRespVO();
        configRespVO.setCaptchaEnable(captchaEnable);
        configRespVO.setPublicKey(rsaUtils.getPublicKey());
        configRespVO.setDocPort(docPort);
        configRespVO.setDocHost(docHost);
        return configRespVO;
    }
}


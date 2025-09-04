package com.crrc.sys.model.server;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
@Schema(description = "登录配置 Resp VO")
public class ConfigRespVO {


    @Value("${crrc.config.captcha.enable:true}")
    private boolean captchaEnable = false;

    @Schema(
            description = "RSA公钥",
            example = "xxx"
    )
    private String publicKey;


    private String docHost;

    private String docPort;
}

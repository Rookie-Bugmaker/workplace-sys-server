package com.crrc.sys.config;

import com.crrc.sys.common.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * @author 谭永强
 * @date 2025-09-16
 */
@Configuration
public class MyRestTemplateConfig {

    @Bean
    //@LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (!ObjectUtils.isEmpty(attributes)) {
                HttpServletRequest mainRequest = attributes.getRequest();
                headers.add(HttpHeaders.AUTHORIZATION, mainRequest.getHeader(HttpHeaders.AUTHORIZATION));
                headers.add(Constant.X_PROJECT_CODE, mainRequest.getHeader(Constant.X_PROJECT_CODE));
            }
            return execution.execute(request, body);
        }));
        return restTemplate;
    }
}


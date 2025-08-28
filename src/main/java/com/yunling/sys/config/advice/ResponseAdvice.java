package com.yunling.sys.config.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunling.sys.common.ResultData;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

/**
 * 全局响应类
 *
 * @author 谭永强
 * @date 2021-09-01
 */
@SuppressWarnings("all")
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 全局响应
     *
     * @param returnType 调用的方法类型
     * @param aClass     消息类
     * @return 默认结果为false，表示当前方法不使用全局响应，改为true表示当前方法使用全局响应
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
        //默认结果为false，表示当前方法不使用全局响应，改为true表示当前方法使用全局响应
        return true;
    }

    /**
     * @param o               返回的数据
     * @param methodParameter 调用的方法
     * @param mediaType       Content-Type类型
     * @param aClass          消息类
     * @param request         请求
     * @param response        响应
     * @return 统一响应体
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
        //当返回类型是String时，用的是StringHttpMessageConverter转换器，无法转换为Json格式
		//必须在方法体上标注@RequestMapping(produces = "application/json; charset=UTF-8")
		if (o instanceof String) {
            try {
                return objectMapper.writeValueAsString(ResultData.success(o));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        if (o instanceof SseEmitter) {
            return o;
        }
        if (o instanceof ResultData) {
            return o;
        }
        return ResultData.success(o);
    }
}

package com.cccr.sys.config.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.cccr.sys.annotation.AutoIdempotent;
import com.cccr.sys.common.RedisUtils;
import com.cccr.sys.common.ResultData;
import com.cccr.sys.common.ReturnCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 自动幂等拦截器
 *
 * @author 谭永强
 * @date 2023-08-15
 */
@Component
public class AutoIdempotentInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private RedisUtils redisUtils;

    /**
     * @param request  请求
     * @param response 响应
     * @param handler  处理
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断请求是否为方法的请求
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        //获取方法中是否有幂等性注解
        AutoIdempotent anno = method.getMethodAnnotation(AutoIdempotent.class);
        //若注解为空则直接返回
        if (Objects.isNull(anno)) {
            return true;
        }
        //限定时间
        long seconds = anno.seconds();
        //token
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(token)) {
            ResultData<String> resultData = ResultData.fail(ReturnCode.ACCESS_DENIED.getCode(), "token不能为空");
            write(response, JSON.toJSONString(resultData));
            return false;
        }
        String md5 = MD5Utils.md5Hex(token, StandardCharsets.UTF_8.toString());
        String key = md5 + ":" + request.getMethod() + ":" + request.getRequestURI();
        boolean flag = redisUtils.setNx(key, 1, seconds);
        if (!flag) {
            //说明不是第一次请求，直接报错
            ResultData<String> resultData = ResultData.fail(ReturnCode.RC999.getCode(), "请求已提交，请勿重复请求");
            write(response, JSON.toJSONString(resultData));
            return false;
        }
        //若为true则为第一次请求
        return true;
    }

    /**
     * 返回结果到前端
     *
     * @param response 响应
     * @param body     结果
     * @throws IOException 异常
     */
    private void write(HttpServletResponse response, String body) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ServletOutputStream os = response.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        os.close();
    }
}

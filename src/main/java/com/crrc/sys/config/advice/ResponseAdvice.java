package com.crrc.sys.config.advice;

import com.crrc.sys.common.ResultData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /**
     * 不进行全局响应处理的URI地址
     */
    private final String[] IGNORE_URI = {
            "/user/get_dept_users", "/user/get_user_lists", "/user/get_user_lists_by_user_name", "/user/get_user", "/user/get_user_with_role_by_username", "/user/get_users_with_role",
            "/user/get_users_by_usernames", "/user/get_user_page", "/user/delete_dept_of_usernames", "/user/create_user_dept_relative", "/dept/get_depts_by_user_id",
            "/dept/get_depts_by_user_name", "/menu/auto_config", "/role/get_role_users_by_roleid","/role/get_role_users_by_roleCode", "/dept/list", "/dept/get_list", "/dept/get_subdept_list",
            "/dept/get_depts_by_company_id", "/dept/get_dept_by_org_code", "/dept/get_dept_by_org_code_list", "/dept/get_dept_and_subDepts_by_orgCode", "/dept/get_depts_by_company_code",
            "/company/get_company_by_ids", "/company/get_all_company", "/company/get_company_by_plants", "/company/get_company_by_orgCodes",
            "/role/get_role_by_role_key", "/role/set_role_users",
            "/audit_log/create", "/service/deploy", "/datasource/get_datasource_list", "/datasource/get_decrypt_type_and_key",
            "/datasource/get_decrypt_type_and_key_global", "/global/fileData/saveFileData", "/global/fileData/listFileData",
            "/global/fileData/getFileData", "/idDesign/nextId", "/global/dict/get", "/global/dict/getName", "/global/dict/getCodes",
            "/global/dict/all", "/global/dict/getItem", "/global/dict/getItemName", "/global/dict/getItemCode", "/global/dict/getUserRealNameInDictExtends",
            "/global/dict/getDeptNameNameInDictExtends", "/globalSetting/value/match/all", "/globalSetting/value/match/get", "/globalSetting/value/match/like",
            "/org/person/findByCurrentUser", "/user/test/findUserPage", "/dept/person/find_person_by_dept_id", "/user/get_user_page_with_person_inform",
            "/user/get_user_page_with_user_inform", "/v3/api-docs","/v3/api-docs/swagger-config", "/dify/**", "/iam/**",
            "/health","/prometheus"

    };



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
    public Object beforeBodyWrite(Object obj, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
        String rawPath = request.getURI().getRawPath();
        Boolean flag = Arrays.stream(IGNORE_URI).map(o -> antPathMatcher.match(o, rawPath)).filter(o -> o).findFirst().orElse(false);
        if (flag) {
            return obj;
        }
        //当返回类型是String时，用的是StringHttpMessageConverter转换器，无法转换为Json格式
        //必须在方法体上标注@RequestMapping(produces = "application/json; charset=UTF-8")
        if (obj instanceof String) {
            try {
                return objectMapper.writeValueAsString(ResultData.success(obj));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        if (Collection.class.isAssignableFrom(methodParameter.getParameterType()) && ObjectUtils.isEmpty(obj)) {
            //根据前端需求，如果返回值是集合且没有数据，统一返回空数组，而不是返回null
            return ResultData.success(new ArrayList<>());
        }
        if (obj instanceof SseEmitter) {
            return obj;
        }
        if (obj instanceof ResultData) {
            return obj;
        }
        return ResultData.success(obj);
    }
}

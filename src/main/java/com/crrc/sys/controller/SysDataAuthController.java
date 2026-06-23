package com.crrc.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.service.SysDataAuthService;
import com.crrc.sys.service.SysProfileService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据权限-控制器
 *
 * @author 谭永强
 * @date 2023-03-23
 */
@RestController
@RequestMapping("/data/auth/")
public class SysDataAuthController {

    @Resource
    private SysDataAuthService dataAuthService;




    /**
     * 获取产线列表
     */
    @GetMapping("getProductionLineList")
    public List<Map<String, Object>> getProductionLineList() {
        String str = "[{\"id\":101,\"code\":\"DT_01\",\"name\":\"焊接生产1线\"},{\"id\":102,\"code\":\"DT_02\",\"name\":\"焊接生产2线\"},{\"id\":103,\"code\":\"DT_03\",\"name\":\"焊接生产3线\"},{\"id\":104,\"code\":\"DT_04\",\"name\":\"焊接生产4线\"}]";
        return JSON.parseObject(str, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    /**
     * 获取当前用户数据权限集合
     *
     * @return 结果: {"数据权限编码": ["数据权限值","数据权限值"]}。
     */
    @GetMapping("getDataAuth")
    public Map<String, List<String>> getDataAuth() {
        return dataAuthService.getDataAuth();
    }



}

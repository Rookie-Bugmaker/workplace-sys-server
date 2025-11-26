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
    private AuthClient authClient;
    @Resource
    private SysDataAuthService dataAuthService;
    @Resource
    private SysProfileService profileService;

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
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        SysUser sysUser = userResultData.getResult();
        //用户数据权限
        List<SysDataAuth> userDataAuthList = dataAuthService.findByObjectId(sysUser.getUserId());
        //角色数据权限
        List<SysDataAuth> roleDataAuthList = dataAuthService.findByObjectId(sysUser.getCurrentRoleId());
        //最终数据权限，
        //1.如果用户权限不存在，则取角色权限.
        //2.如果角色权限不存在，则取用户权限.
        //3.如果用户权限和角色权限都存在，则取并集。这里的并集这是并不是对相同的数据权限的值去并集，只是对数据权限的key去并集，例如:用户上设置了org_id=[1,2]，
        //角色上设置了org_id=[1,2,3,4]，最终的数据权限org_id=[1,2]。
        List<SysDataAuth> dataAuthList = new ArrayList<>();
        if (CollectionUtils.isEmpty(userDataAuthList)) {
            dataAuthList.addAll(roleDataAuthList);
        }
        if (CollectionUtils.isEmpty(roleDataAuthList)) {
            dataAuthList.addAll(userDataAuthList);
        }
        if (!CollectionUtils.isEmpty(userDataAuthList) && !CollectionUtils.isEmpty(roleDataAuthList)) {
            dataAuthList.addAll(userDataAuthList);
            for (SysDataAuth roleDataAuth : roleDataAuthList) {
                //查询该数据权限是否在用户中存在
                SysDataAuth sysDataAuth = userDataAuthList.stream().filter(o -> o.getProfileId().equals(roleDataAuth.getProfileId())).findFirst().orElse(null);
                if (ObjectUtils.isEmpty(sysDataAuth)) {
                    //不存在的情况
                    dataAuthList.add(roleDataAuth);
                }
            }
        }
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            List<Long> profileIdList = dataAuthList.stream().map(SysDataAuth::getProfileId).collect(Collectors.toList());
            //根据配置文件ID查询配置文件信息
            List<SysProfile> profileList = profileService.findByIds(profileIdList, LanguageEnum.ZH_CN.getValue());
            //如果配置文件都不存在，则说明数据权限失效了
            if (CollectionUtils.isEmpty(profileList)) {
                return null;
            }
            for (SysDataAuth sysDataAuth : dataAuthList) {
                //查找与数据权限ID相匹配的配置文件
                SysProfile sysProfile = profileList.stream().filter(o -> o.getProfileId().equals(sysDataAuth.getProfileId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(sysProfile)) {
                    String authCode = sysProfile.getProfileCode();
                    //判断该数据权限是否存在别名
                    if (!ObjectUtils.isEmpty(sysProfile.getFieldAlias())) {
                        authCode = (authCode + "," + sysProfile.getFieldAlias());
                    }
                    sysDataAuth.setAuthCode(authCode);
                }
            }
        }
        if (CollectionUtils.isEmpty(dataAuthList)) {
            return null;
        }
        Map<String, List<String>> dataAuthMap = new HashMap<>(dataAuthList.size());
        for (SysDataAuth dataAuth : dataAuthList) {
            //数据权限编码(该编码可能存在以(,)分隔的多个别名)，所以需要把每个别名都提取出来
            String authCode = dataAuth.getAuthCode();
            if (ObjectUtils.isEmpty(authCode)) {
                continue;
            }
            //数据权限值集
            List<String> valueList = dataAuth.getDataAuthValueList().stream().map(SysDataAuthValue::getAuthValue).collect(Collectors.toList());
            for (String code : authCode.split(",")) {
                if (ObjectUtils.isEmpty(code)) {
                    continue;
                }
                dataAuthMap.put(code.trim(), valueList);
            }
        }
        return dataAuthMap;
    }

}

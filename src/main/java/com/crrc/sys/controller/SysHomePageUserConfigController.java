package com.crrc.sys.controller;

import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.asserts.BizServiceAssert;
import com.crrc.sys.entity.homepage.SysHomePageUserConfig;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.service.homepage.SysHomePageUserConfigService;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 1 * @Author: fucheng
 * 2 * @Description: 用户主页配置
 * 3 * @Date: 2023/5/22
 */
@RestController
@RequestMapping("/homePage/userConfig/")
public class SysHomePageUserConfigController {

    @Resource
    private AuthClient authClient;
    @Resource
    private SysHomePageUserConfigService sysHomePageUserConfigService;

    /**
     * 保存用户自定义配置
     */
    @AutoIdempotent
    @PostMapping("add")
    public Integer saveUserConfig(SysHomePageUserConfig param) {
        Long userId = getUserId();
        param.setUserId(userId);
        param.setCreateBy(userId);
        param.setCreateDate(new Date());
        checkData(param);
        return sysHomePageUserConfigService.add(param);
    }

    /**
     * 查询当前用户主页配置
     */
    @GetMapping("currentUserConfig")
    public SysHomePageUserConfig currentUserConfig() {
        return sysHomePageUserConfigService.findByUserId(getUserId());
    }

    /**
     * 获取当前用户
     */
    private Long getUserId() {
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        return currentUser.getUserId();
    }

    /**
     * 校验参数
     */
    private void checkData(SysHomePageUserConfig param) {
        BizServiceAssert.isTrue(!ObjectUtils.isEmpty(param.getUserConfig()), "用户配置不能为空！");
    }
}

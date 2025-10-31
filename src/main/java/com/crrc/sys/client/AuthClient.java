package com.crrc.sys.client;

import com.crrc.sys.client.fallback.AuthClientFallBack;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.entity.user.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 认证中心-Feign客户端
 *
 * @author 谭永强
 * @date 2022-09-03
 */
@FeignClient(name = "auth-server", fallback = AuthClientFallBack.class)
public interface AuthClient {

    /**
     * 获取当前用户信息
     *
     * @return 结果
     */
    @PostMapping("currentUser")
    ResultData<SysUser> currentUser();

    /**
     * 更新当前用户信息
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @PutMapping("updateCurrentUser")
    ResultData<Boolean> updateCurrentUser(@RequestParam String projectCode, @RequestParam Long roleId, @RequestParam Long companyId, @RequestParam Long deptId);

    /**
     * 获取当前用户信息
     *
     * @param token token信息
     * @return 结果
     */
    @PostMapping("currentUser")
    ResultData<SysUser> currentUser(@RequestHeader(name = "Authorization", required = false) String token);

    /**
     * 清空用户缓存
     *
     * @param userName 用户名
     */
    @GetMapping("clear")
    ResultData<Void> clear(@RequestParam("userName") String userName);

    /**
     * 更新下一次强制修改密码缓存
     *
     * @param nextLoginUpdatePassword 下一次登录强制修改密码
     * @return 结果(true:更新成功)
     */
    @PutMapping("updateNextLoginUpdatePassword")
    ResultData<Boolean> updateNextLoginUpdatePassword(@RequestParam String nextLoginUpdatePassword);
}

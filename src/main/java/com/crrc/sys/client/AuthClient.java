package com.crrc.sys.client;

import com.crrc.sys.client.fallback.AuthClientFallBack;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.entity.user.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

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
     * 获取当前客户端ID
     *
     * @return 结果
     */
    @GetMapping("currentClientId")
    ResultData<String> currentClientId();

    /**
     * 切换当前角色
     *
     * @param roleId                  角色ID
     * @return 结果
     */
    @PostMapping("updateCurrentUser")
    ResultData<Boolean> updateCurrentUser(@RequestParam("roleId") Long roleId);

    /**
     * 获取当前用户信息
     *
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
}

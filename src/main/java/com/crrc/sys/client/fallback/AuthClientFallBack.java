package com.crrc.sys.client.fallback;

import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.entity.user.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 熔断器
 *
 * @author 谭永强
 * @date 2022-09-06
 */
@Component
public class AuthClientFallBack implements AuthClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ResultData<SysUser> currentUser() {
        logger.error("AuthClient.currentUser已进入熔断器");
        return ResultData.fallback();
    }

    @Override
    public ResultData<Boolean> updateCurrentUser(Long roleId) {
        logger.error("AuthClient.updateCurrentUser已进入熔断器, roleId={}", roleId);
        return ResultData.fallback();
    }

    @Override
    public ResultData<SysUser> currentUser(String token) {
        logger.error("AuthClient.currentUser已进入熔断器, token={}", token);
        return ResultData.fallback();
    }

    @Override
    public ResultData<Void> clear(String userName) {
        logger.error("AuthClient.clear已进入熔断器, userName={}", userName);
        return ResultData.fallback();
    }
}

package com.yunling.sys.client.fallback;

import com.yunling.sys.client.AuthClient;
import com.yunling.sys.common.ResultData;
import com.yunling.sys.entity.user.SysUser;
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
    public ResultData<String> currentClientId() {
        logger.error("AuthClient.currentClientId已进入熔断器");
        return ResultData.fallback();
    }

    @Override
    public ResultData<Boolean> updateCurrentUser(Long roleId, Long orgId) {
        logger.error("AuthClient.updateCurrentUser已进入熔断器, roleId={}, orgId={}", roleId, orgId);
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

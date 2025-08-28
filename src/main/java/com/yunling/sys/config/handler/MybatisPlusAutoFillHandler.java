package com.yunling.sys.config.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.yunling.sys.client.AuthClient;
import com.yunling.sys.common.ResultData;
import com.yunling.sys.config.exception.BizException;
import com.yunling.sys.entity.user.SysUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Mybatis Plus自动填充处理器
 * 统一处理数据库标准字段 create_date,create_by,last_update_login,...等字段
 * 例如: 业务中有一个实体 User,该实体对应一个USER表,保存/更新 user实体的时候只需要插入相应的业务数据,不需要管这些标准字段
 *
 * @author huangsen
 * @date 2023/03/13
 */
public class MybatisPlusAutoFillHandler implements MetaObjectHandler {

    @Resource
    private AuthClient authClient;

    private static final String CREATE_BY = "createBy";
    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_BY = "updateBy";
    private static final String UPDATE_TIME = "updateTime";

    /**
     * 获取当前请求的Token
     *
     * @return 不存在：true，存在：false
     */
    private boolean checkRequestToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = null;
        if (!ObjectUtils.isEmpty(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            token = request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        //如果token为空，说明当前请求是ERP数据同步|定时任务请求，所以没有token
        return (ObjectUtils.isEmpty(token) || "{{token_type}} {{access_token}}".equals(token));
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        //创建时间
        this.setFieldValByName(CREATE_TIME, new Date(), metaObject);
        //如果返回值为false，说明没有token，直接将创建人设置为-1
        if (checkRequestToken()) {
            //创建人
            this.setFieldValByName(CREATE_BY, -1L, metaObject);
            return;
        }
        //获取当前用户
        ResultData<SysUser> sysUserResultData = authClient.currentUser();
        if (!sysUserResultData.getSuccess()) {
            throw new BizException(sysUserResultData.getCode(), sysUserResultData.getMessage());
        }
        SysUser user = sysUserResultData.getResult();
        //创建人
        this.setFieldValByName(CREATE_BY, user.getUserId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //最后修改时间
        this.setFieldValByName(UPDATE_TIME, new Date(), metaObject);
        //如果返回值为true，说明没有token，直接将修改人设置为-1
        if (checkRequestToken()) {
            //最后修改人
            this.setFieldValByName(UPDATE_BY, -1L, metaObject);
            return;
        }
        //获取当前用户
        ResultData<SysUser> sysUserResultData = authClient.currentUser();
        if (!sysUserResultData.getSuccess()) {
            throw new BizException(sysUserResultData.getCode(), sysUserResultData.getMessage());
        }
        SysUser user = sysUserResultData.getResult();
        //最后修改人
        this.setFieldValByName(UPDATE_BY, user.getUserId(), metaObject);
    }
}
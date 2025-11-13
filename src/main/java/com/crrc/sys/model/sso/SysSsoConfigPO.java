package com.crrc.sys.model.sso;

import com.baomidou.mybatisplus.annotation.*;
import com.crrc.sys.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 谭永强
 * @date 2025-11-06
 */
@TableName("sys_sso_config")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysSsoConfigPO extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long id;
    /**
     * 配置名称
     */
    private String configName;
    /**
     * 配置编码
     */
    private String configCode;
    /**
     * 单点类型(ESC)
     */
    private String configType;
    /**
     * 图标
     */
    private Long fileHeaderId;
    /**
     * 应用ID
     */
    private String clientId;
    /**
     * 应用秘钥
     */
    private String clientSecret;
    /**
     * 登录地址
     */
    private String authorizeRequestUrl;
    /**
     * 获取token地址
     */
    private String tokenRequestUrl;
    /**
     * 获取用户地址
     */
    private String userRequestUrl;
    /**
     * 回调地址
     */
    private String redirectUrl;
    /**
     * 逻辑删除字段
     */
    @TableLogic
    private Integer deleted;
    /**
     * 文件ID
     * 由于图标只会存在1个，所以此处旧旧没有必要使用集合了
     */
    @TableField(exist = false)
    private Long fileId;
}

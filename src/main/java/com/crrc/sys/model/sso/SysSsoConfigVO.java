package com.crrc.sys.model.sso;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * @author 谭永强
 * @date 2025-12-02
 */
@Data
public class SysSsoConfigVO {

    /**
     * 主键
     */
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
     * 由于图标只会存在1个，所以此处旧就没有必要使用集合了
     */
    private Long fileId;
    /**
     * 所属项目
     */
    private Long projectId;
    /**
     * 所属项目编码
     */
    private String projectCode;
    /**
     * 所属项目名称
     */
    private String projectName;
}

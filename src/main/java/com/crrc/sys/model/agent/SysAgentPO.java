package com.crrc.sys.model.agent;

import com.baomidou.mybatisplus.annotation.*;
import com.crrc.sys.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Dify智能体实体
 *
 * @author 谭永强
 * @date 2025-11-12
 */
@TableName("sys_agent_config")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysAgentPO extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long id;
    /**
     * 智能体名称
     */
    private String agentName;
    /**
     * 智能体编码
     */
    private String agentCode;
    /**
     * API地址
     */
    private String baseUrl;
    /**
     * API密钥
     */
    private String apiKey;
    /**
     * 图标
     */
    private Long fileHeaderId;
    /**
     * 文件上传(Y:是、N:否)
     */
    private String fileUpload;
    /**
     * 描述
     */
    private String agentDesc;
    /**
     * 逻辑删除字段
     */
    @TableLogic
    private Integer deleted;
    /**
     * 文件ID
     * 由于图标只会存在1个，所以此处旧就没有必要使用集合了
     */
    @TableField(exist = false)
    private Long fileId;
}

package com.crrc.sys.model.agent;

import lombok.Data;

/**
 * @author 谭永强
 * @date 2025-11-13
 */
@Data
public class SysAgentDTO {

    /**
     * 当前页码(page=-1查询全部)
     */
    private Integer page;
    /**
     * 每页查询数据条数
     */
    private Integer pageSize;
    /**
     * 名称
     */
    private String agentName;
    /**
     * 编码
     */
    private String agentCode;
    /**
     * 描述
     */
    private String agentDesc;
}

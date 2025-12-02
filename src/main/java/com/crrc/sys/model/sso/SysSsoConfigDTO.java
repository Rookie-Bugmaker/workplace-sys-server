package com.crrc.sys.model.sso;

import lombok.Data;

/**
 * @author 谭永强
 * @date 2025-11-06
 */
@Data
public class SysSsoConfigDTO {

    /**
     * 当前页码(page=-1查询全部)
     */
    private Integer page;
    /**
     * 每页查询数据条数
     */
    private Integer pageSize;
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
     * 项目编码()
     */
    private String projectCode;
}

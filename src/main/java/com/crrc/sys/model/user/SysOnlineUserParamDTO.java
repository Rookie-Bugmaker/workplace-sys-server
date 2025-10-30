package com.crrc.sys.model.user;

import lombok.Data;

/**
 * 在线用户参数对象
 *
 * @author 谭永强
 * @date 2025-10-30
 */
@Data
public class SysOnlineUserParamDTO {

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 每页查询数据条数
     * pageSize<=0时查询全部
     */
    private Integer pageSize;
    /**
     * 客户端类型(Browser:浏览器)
     */
    private String clientType = "Browser";
    /**
     * 用户名
     */
    private String userName;
    /**
     * 员工姓名
     */
    private String fullName;
}

package com.crrc.sys.model.user;

import lombok.Data;

/**
 * 在线用户数据传输对象
 *
 * @author 谭永强
 * @date 2025-10-30
 */
@Data
public class SysOnlineUserVO {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 客户端类型(Browser:浏览器)
     */
    private String clientType;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 员工姓名
     */
    private String fullName;
    /**
     * 在线状态(Y:在线、N:离线)
     */
    private String onlineStatus;
}

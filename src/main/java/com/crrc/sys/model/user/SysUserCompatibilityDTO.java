package com.crrc.sys.model.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象(兼容旧平台)
 *
 * @author 谭永强
 * @date 2025-09-05
 */
@Data
public class SysUserCompatibilityDTO {

    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 账号状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phonenumber;
    /**
     * 用户性别（0男，1女，2未知）
     */
    private Integer sex;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 用户类型（0管理员，1普通用户）
     */
    private Integer userType;

    /**
     * 是否部门领导
     */
    private Boolean leader;
    /**
     * 密码修改时间
     */
    private LocalDateTime passwordUpdateTime;
}

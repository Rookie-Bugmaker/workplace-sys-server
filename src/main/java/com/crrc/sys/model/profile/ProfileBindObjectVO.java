package com.crrc.sys.model.profile;

import lombok.Data;

import java.util.List;

/**
 * 配置文件绑定的角色/用户-返回项（配置文件维度）
 *
 * @author 李江涛
 * @date 2026-08-31
 */
@Data
public class ProfileBindObjectVO {

    /**
     * 对象ID(用户ID或角色ID)
     */
    private Long objectId;
    /**
     * 名称(角色:roleName、用户:userName)
     */
    private String name;
    /**
     * 编码(角色:roleCode、用户:userName)
     */
    private String code;
    /**
     * 对象类型(user:用户、role:角色)
     */
    private String authType;
    /**
     * 该对象在该配置文件下拥有的权限值集合(含权限值描述)
     */
    private List<AuthValueItem> authValues;
}

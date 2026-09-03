package com.crrc.sys.model.profile;

import lombok.Data;

/**
 * 配置文件绑定角色/用户查询入参（配置文件维度，分页）
 * 在原有 profileId + authType + language 基础上增加分页与检索条件：
 * 1.objectSearchParam：对象模糊关键字，authType=user 时匹配用户名或用户描述(姓名)，
 *   authType=role 时匹配角色编码或角色名称；
 * 2.authValue：权限值过滤，仅返回拥有该权限值的对象(对象级过滤，命中对象仍展示其全部权限值)。
 *
 * @author 李江涛
 * @date 2026-09-03
 */
@Data
public class ProfileBindObjectQueryDTO {

    /**
     * 配置文件ID
     */
    private Long profileId;
    /**
     * 对象类型(user:用户、role:角色)
     */
    private String authType;
    /**
     * 语言(角色名称多语言，取值：zh-CN/en-US)
     */
    private String language;
    /**
     * 当前页码(缺省按 1 处理)
     */
    private Integer page;
    /**
     * 每页条数(缺省按默认大小处理)
     */
    private Integer pageSize;
    /**
     * 对象模糊查询关键字(可选)
     * user：匹配 sys_user.user_name 或 sys_org_person_tl.full_name(用户描述/姓名)
     * role：匹配 sys_role_b.role_code 或 sys_role_tl.role_name
     */
    private String objectSearchParam;
    /**
     * 权限值过滤(可选)：仅返回拥有该权限值的对象，命中对象在 authValues 中仍展示其全部权限值
     */
    private String authValue;
}

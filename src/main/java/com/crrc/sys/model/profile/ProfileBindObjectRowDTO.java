package com.crrc.sys.model.profile;

import lombok.Data;

/**
 * 配置文件绑定对象分页查询行投影(数据库联查直接映射)
 * 字段对应联查结果列：用户分支携带 userName/fullName，角色分支携带 roleCode/roleName/roleDescription，
 * 服务层据此组装 {@link ProfileBindObjectVO} 的 name/code。
 *
 * @author 李江涛
 * @date 2026-09-03
 */
@Data
public class ProfileBindObjectRowDTO {

    /**
     * 数据权限ID(sys_data_auth.auth_id)
     */
    private Long authId;
    /**
     * 对象ID(sys_data_auth.object_id，即 userId 或 roleId)
     */
    private Long objectId;
    /**
     * 用户名(sys_user.user_name，仅用户分支)
     */
    private String userName;
    /**
     * 姓名(sys_org_person_tl.full_name，仅用户分支，作为用户描述/姓名)
     */
    private String fullName;
    /**
     * 角色编码(sys_role_b.role_code，仅角色分支)
     */
    private String roleCode;
    /**
     * 角色名称(sys_role_tl.role_name，仅角色分支)
     */
    private String roleName;
}

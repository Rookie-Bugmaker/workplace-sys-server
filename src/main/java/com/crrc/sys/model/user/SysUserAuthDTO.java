package com.crrc.sys.model.user;

import java.util.List;

/**
 * 用户权限对象
 */
public class SysUserAuthDTO {
    /**
     * 角色编码列表
     */
    private List<String> roleCodeList;

    /**
     * 数据权限映射
     */
    private AuthInformDTO authInform;

    public List<String> getRoleCodeList() {
        return roleCodeList;
    }

    public void setRoleCodeList(List<String> roleCodeList) {
        this.roleCodeList = roleCodeList;
    }

    public AuthInformDTO getAuthInform() {
        return authInform;
    }

    public void setAuthInform(AuthInformDTO authInform) {
        this.authInform = authInform;
    }
}

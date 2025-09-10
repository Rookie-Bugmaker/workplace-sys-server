package com.crrc.sys.model.role;

import java.util.List;

/**
 * @author 谭永强
 * @date 2025-09-10
 */
public class SysRoleCompanyDeptDTO {

    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 部门ID集合
     */
    private List<Long> deptIdList;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<Long> getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(List<Long> deptIdList) {
        this.deptIdList = deptIdList;
    }
}

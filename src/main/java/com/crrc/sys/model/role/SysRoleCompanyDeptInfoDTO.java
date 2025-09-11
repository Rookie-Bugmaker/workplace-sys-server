package com.crrc.sys.model.role;

import com.crrc.sys.entity.dept.Dept;

import java.util.List;

/**
 * @author 谭永强
 * @date 2025-09-10
 */
public class SysRoleCompanyDeptInfoDTO {

    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 部门ID集合
     */
    private List<Dept> deptList;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Dept> getDeptList() {
        return deptList;
    }

    public void setDeptList(List<Dept> deptList) {
        this.deptList = deptList;
    }
}

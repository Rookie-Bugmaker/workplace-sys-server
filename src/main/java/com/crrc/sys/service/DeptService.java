package com.crrc.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.model.dept.DeptListRespVO;
import com.crrc.sys.model.dept.DeptOfUserDTO;

import java.util.List;

public interface DeptService extends IService<Dept> {
    List<Long> getSubDeptId(Long deptId);

    List<DeptOfUserDTO> getDeptByUserIdList(List<Long> userIds);

    List<DeptListRespVO> getDeptListResp(List<Dept> deptList);

    List<Dept> getDeptListByCompanyId(Long companyId);

    Dept getDeptByOrgCode(String orgCode);
    /**
     * 根据组织编码获取部门及其所有子部门
     * @param orgCode 组织编码
     * @return 部门列表
     */
    List<Dept> getDeptAndSubDeptsByOrgCode(String orgCode);
}

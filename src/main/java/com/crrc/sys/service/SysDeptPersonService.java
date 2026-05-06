package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.model.dept.person.SysDeptPersonDTO;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;

import java.util.List;

/**
 * 部门人员接口
 *
 * @author 谭永强
 * @date 2025-09-03
 */
public interface SysDeptPersonService extends IService<SysDeptPersonPO> {

    /**
     * 根据人员ID查询部门人员信息(批量)
     *
     * @param personIdList 人员ID集合
     * @return 结果
     */
    List<SysDeptPersonPO> findByPersonId(List<Long> personIdList);

    /**
     * 根据部门ID查询让部门人员列表
     *
     * @param deptId 部门ID
     * @return 部门人员列表
     */
    List<SysDeptPersonPO> findByDeptId(Long deptId);

    /**
     * 根据部门ID查询让部门人员列表(分页)
     *
     * @param deptId 部门ID
     * @return 部门人员列表
     */
    PaginationGrid<SysDeptPersonPO> findPageByDeptId(Integer page, Integer pageSize, Long deptId);

    /**
     * 根据条件查询部门人员列表(分页)
     *
     * @param page         页码
     * @param pageSize     每页条数
     * @param dto          查询条件(deptId/position，选填)
     * @param personIdList 人员ID列表(选填)
     * @return 部门人员列表
     */
    PaginationGrid<SysDeptPersonPO> findPageByCondition(Integer page, Integer pageSize, SysDeptPersonDTO dto, List<Long> personIdList);
}

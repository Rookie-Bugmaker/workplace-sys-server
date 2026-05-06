package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.mapper.SysDeptPersonMapper;
import com.crrc.sys.model.dept.person.SysDeptPersonDTO;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.service.SysDeptPersonService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 部门人员接口实现类
 *
 * @author 谭永强
 * @date 2025-09-03
 */
@Service
public class SysDeptPersonServiceImpl extends ServiceImpl<SysDeptPersonMapper, SysDeptPersonPO> implements SysDeptPersonService {

    /**
     * 根据人员ID查询部门人员信息(批量)
     *
     * @param personIdList 人员ID集合
     * @return 结果
     */
    @Override
    public List<SysDeptPersonPO> findByPersonId(List<Long> personIdList) {
        if (CollectionUtils.isEmpty(personIdList)) {
            return null;
        }
        LambdaQueryWrapper<SysDeptPersonPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDeptPersonPO::getPersonId, personIdList);
        return super.baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据部门ID查询让部门人员列表
     *
     * @param deptId 部门ID
     * @return 部门人员列表
     */
    @Override
    public List<SysDeptPersonPO> findByDeptId(Long deptId) {
        if (ObjectUtils.isEmpty(deptId)) {
            return null;
        }
        LambdaQueryWrapper<SysDeptPersonPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptPersonPO::getDeptId, deptId);
        return super.baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据部门ID查询让部门人员列表(分页查询)
     *
     * @param page     当前页码
     * @param pageSize 查询数据条数
     * @param deptId   部门ID
     * @return 结果
     */
    @Override
    public PaginationGrid<SysDeptPersonPO> findPageByDeptId(Integer page, Integer pageSize, Long deptId) {
        IPage<SysDeptPersonPO> iPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysDeptPersonPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptPersonPO::getDeptId, deptId);
        iPage = super.baseMapper.selectPage(iPage, queryWrapper);
        return PaginationGrid.success(iPage);
    }

    /**
     * 根据条件查询部门人员列表(分页)
     *
     * @param page         页码
     * @param pageSize     每页条数
     * @param dto          查询条件(deptId/position/personIdList，选填)
     * @param personIdList 根据personName/jobNo模糊查询得到的人员ID列表(选填)
     * @return 结果
     */
    @Override
    public PaginationGrid<SysDeptPersonPO> findPageByCondition(Integer page, Integer pageSize, SysDeptPersonDTO dto, List<Long> personIdList) {
        IPage<SysDeptPersonPO> iPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysDeptPersonPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!ObjectUtils.isEmpty(dto.getDeptId()), SysDeptPersonPO::getDeptId, dto.getDeptId());
        queryWrapper.like(!ObjectUtils.isEmpty(dto.getPosition()), SysDeptPersonPO::getPosition, dto.getPosition());
        queryWrapper.in(!CollectionUtils.isEmpty(personIdList), SysDeptPersonPO::getPersonId, personIdList);
        iPage = super.baseMapper.selectPage(iPage, queryWrapper);
        return PaginationGrid.success(iPage);
    }
}

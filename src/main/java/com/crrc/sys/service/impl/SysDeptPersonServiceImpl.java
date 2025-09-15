package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.mapper.SysDeptPersonMapper;
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
}

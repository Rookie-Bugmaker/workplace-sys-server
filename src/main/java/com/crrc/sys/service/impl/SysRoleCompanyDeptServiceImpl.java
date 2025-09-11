package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.role.SysRoleCompanyDept;
import com.crrc.sys.mapper.SysRoleCompanyDeptMapper;
import com.crrc.sys.service.SysRoleCompanyDeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @author 谭永强
 * @date 2025-09-10
 */
@Service
public class SysRoleCompanyDeptServiceImpl extends ServiceImpl<SysRoleCompanyDeptMapper, SysRoleCompanyDept> implements SysRoleCompanyDeptService {


    /**
     * 根据角色删除角色部门数据
     *
     * @param roleId 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByRoleId(Long roleId) {
        if (ObjectUtils.isEmpty(roleId)) {
            return;
        }
        LambdaQueryWrapper<SysRoleCompanyDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleCompanyDept::getRoleId, roleId);
        super.remove(queryWrapper);
    }
}

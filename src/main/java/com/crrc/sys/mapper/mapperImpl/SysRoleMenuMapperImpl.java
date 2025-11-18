package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.role.*;
import com.crrc.sys.entity.role.SysRoleMenu;
import com.crrc.sys.entity.role.SysRoleMenuDBEntity;
import com.crrc.sys.mapper.SysRoleMenuMapper;
import com.crrc.sys.mapper.mapperService.SysRoleMenuMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色菜单关系表-mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-17
 */
@Repository
public class SysRoleMenuMapperImpl extends ServiceImpl<SysRoleMenuMapperService, SysRoleMenuDBEntity> implements SysRoleMenuMapper {

    @Resource
    private SysRoleMenuMapperService sysRoleMenuMapperService;

    @Override
    public void deleteByMenuIds(List<Long> menuIdList) {
        LambdaUpdateWrapper<SysRoleMenuDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysRoleMenuDBEntity::getMenuId, menuIdList);
        sysRoleMenuMapperService.delete(wrapper);
    }

    @Override
    public void insert(List<SysRoleMenu> roleMenuList) {
        List<SysRoleMenuDBEntity> sysRoleMenuDBEntities = convertToDB(roleMenuList);
        super.saveBatch(sysRoleMenuDBEntities);
    }

    @Override
    public void deleteByRoleId(long roleId) {
        LambdaUpdateWrapper<SysRoleMenuDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysRoleMenuDBEntity::getRoleId, roleId);
        sysRoleMenuMapperService.delete(wrapper);
    }

    @Override
    public void deleteByRoleIds(List<Long> roleIds) {
        LambdaUpdateWrapper<SysRoleMenuDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysRoleMenuDBEntity::getRoleId, roleIds);
        sysRoleMenuMapperService.delete(wrapper);
    }

    @Override
    public List<SysRoleMenu> findByRoleId(long roleId) {
        LambdaQueryWrapper<SysRoleMenuDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuDBEntity::getRoleId, roleId)
                .eq(SysRoleMenuDBEntity::getChildFlag, 0);
        List<SysRoleMenuDBEntity> sysRoleMenuDBEntities = sysRoleMenuMapperService.selectList(wrapper);
        return convertFromDB(sysRoleMenuDBEntities);
    }

    private List<SysRoleMenuDBEntity> convertToDB(List<SysRoleMenu> dataList) {
        List<SysRoleMenuDBEntity> entityList = BeanUtils.toBean(dataList, SysRoleMenuDBEntity.class);
        return entityList;
    }

    private List<SysRoleMenu> convertFromDB(List<SysRoleMenuDBEntity> entityList) {
        List<SysRoleMenu> dataList = BeanUtils.toBean(entityList, SysRoleMenu.class);
        return dataList;
    }
}
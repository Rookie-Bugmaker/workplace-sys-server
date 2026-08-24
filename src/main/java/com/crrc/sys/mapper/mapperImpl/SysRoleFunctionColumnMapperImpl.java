package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.role.SysRoleFunctionColumn;
import com.crrc.sys.entity.role.SysRoleFunctionColumnDBEntity;
import com.crrc.sys.mapper.SysRoleFunctionColumnMapper;
import com.crrc.sys.mapper.mapperService.SysRoleFunctionColumnMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色列权限关系表-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-17
 */
@Repository
public class SysRoleFunctionColumnMapperImpl extends ServiceImpl<SysRoleFunctionColumnMapperService, SysRoleFunctionColumnDBEntity> {

    @Resource
    private SysRoleFunctionColumnMapperService sysRoleFunctionColumnMapperService;


    public void insert(List<SysRoleFunctionColumn> functionColumnList) {
        List<SysRoleFunctionColumnDBEntity> sysRoleFunctionColumnDBEntities = convertToDB(functionColumnList);
        super.saveBatch(sysRoleFunctionColumnDBEntities);
    }


    public void deleteByRoleIds(List<Long> roleIdList) {
        LambdaUpdateWrapper<SysRoleFunctionColumnDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysRoleFunctionColumnDBEntity::getRoleId, roleIdList);
        sysRoleFunctionColumnMapperService.delete(wrapper);
    }


    public List<SysRoleFunctionColumn> findByRoleId(long roleId) {
        LambdaQueryWrapper<SysRoleFunctionColumnDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleFunctionColumnDBEntity::getRoleId, roleId);
        List<SysRoleFunctionColumnDBEntity> sysRoleFunctionColumnDBEntities = sysRoleFunctionColumnMapperService.selectList(wrapper);
        return convertFromDB(sysRoleFunctionColumnDBEntities);
    }

    private List<SysRoleFunctionColumnDBEntity> convertToDB(List<SysRoleFunctionColumn> dataList) {
        List<SysRoleFunctionColumnDBEntity> entityList = BeanUtils.toBean(dataList, SysRoleFunctionColumnDBEntity.class);
        return entityList;
    }

    private List<SysRoleFunctionColumn> convertFromDB(List<SysRoleFunctionColumnDBEntity> entityList) {
        List<SysRoleFunctionColumn> dataList = BeanUtils.toBean(entityList, SysRoleFunctionColumn.class);
        return dataList;
    }

    public List<SysRoleFunctionColumn> findByRoleIds(List<Long> roleIds) {
        LambdaQueryWrapper<SysRoleFunctionColumnDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleFunctionColumnDBEntity::getRoleId, roleIds);
        List<SysRoleFunctionColumnDBEntity> sysRoleFunctionColumnDBEntities = sysRoleFunctionColumnMapperService.selectList(wrapper);
        return convertFromDB(sysRoleFunctionColumnDBEntities);
    }
}

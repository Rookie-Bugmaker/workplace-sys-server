package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.role.SysRoleColumnValueSet;
import com.crrc.sys.entity.role.SysRoleColumnValueSetDBEntity;
import com.crrc.sys.mapper.SysRoleColumnValueSetMapper;
import com.crrc.sys.mapper.mapperService.SysRoleColumnValueSetMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色列权限值集表-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-20
 */
@Repository
public class SysRoleColumnValueSetMapperImpl extends ServiceImpl<SysRoleColumnValueSetMapperService, SysRoleColumnValueSetDBEntity> {

    @Resource
    private SysRoleColumnValueSetMapperService sysRoleColumnValueSetMapperService;


    public void insert(List<SysRoleColumnValueSet> roleMenuColumnValueSets) {
        super.saveBatch(convertToDB(roleMenuColumnValueSets));
    }


    public void deleteByRoleIds(List<Long> roleIdList) {
        LambdaUpdateWrapper<SysRoleColumnValueSetDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysRoleColumnValueSetDBEntity::getRoleId, roleIdList);
        sysRoleColumnValueSetMapperService.delete(wrapper);
    }


    public List<SysRoleColumnValueSet> findByRoleId(long roleId) {
        LambdaQueryWrapper<SysRoleColumnValueSetDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleColumnValueSetDBEntity::getRoleId, roleId);
        List<SysRoleColumnValueSetDBEntity> dbEntities = sysRoleColumnValueSetMapperService.selectList(wrapper);
        return convertFromDB(dbEntities);
    }

    /**
     * 弃用
     */
    @Deprecated

    public List<SysRoleColumnValueSet> findByRoleIdAndBlockCode(long roleId, String blockCode) {
        return null;
    }

    private SysRoleColumnValueSetDBEntity convertToDB(SysRoleColumnValueSet data) {
        SysRoleColumnValueSetDBEntity entity = BeanUtils.toBean(data, SysRoleColumnValueSetDBEntity.class);
        return entity;
    }

    private SysRoleColumnValueSet convertFromDB(SysRoleColumnValueSetDBEntity entity) {
        SysRoleColumnValueSet data = BeanUtils.toBean(entity, SysRoleColumnValueSet.class);
        return data;
    }

    private List<SysRoleColumnValueSetDBEntity> convertToDB(List<SysRoleColumnValueSet> dataList) {
        List<SysRoleColumnValueSetDBEntity> entityList = BeanUtils.toBean(dataList, SysRoleColumnValueSetDBEntity.class);
        return entityList;
    }

    private List<SysRoleColumnValueSet> convertFromDB(List<SysRoleColumnValueSetDBEntity> entityList) {
        List<SysRoleColumnValueSet> dataList = BeanUtils.toBean(entityList, SysRoleColumnValueSet.class);
        return dataList;
    }
}
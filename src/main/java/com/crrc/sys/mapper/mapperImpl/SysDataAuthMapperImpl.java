package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.api.*;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthDBEntity;
import com.crrc.sys.mapper.SysDataAuthMapper;
import com.crrc.sys.mapper.mapperService.SysDataAuthMapperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-13
 */
@Repository
public class SysDataAuthMapperImpl implements SysDataAuthMapper {

    @Resource
    private SysDataAuthMapperService sysDataAuthMapperService;

    @Override
    public void insertList(List<SysDataAuth> dataAuthList) {
        List<SysDataAuthDBEntity> sysDataAuthDBEntities = convertToDB(dataAuthList);
        sysDataAuthDBEntities.forEach(sysDataAuthDBEntity -> {
            sysDataAuthMapperService.insert(sysDataAuthDBEntity);
        });
    }

    @Override
    public void delete(long objectId) {
        LambdaUpdateWrapper<SysDataAuthDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysDataAuthDBEntity::getObjectId, objectId);
        sysDataAuthMapperService.delete(wrapper);
    }

    @Override
    public void deleteByObjectIds(List<Long> objectIds) {
        LambdaUpdateWrapper<SysDataAuthDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysDataAuthDBEntity::getObjectId, objectIds);
        sysDataAuthMapperService.delete(wrapper);
    }

    @Override
    public List<SysDataAuth> findByObjectId(long objectId) {
        LambdaQueryWrapper<SysDataAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataAuthDBEntity::getObjectId, objectId);
        List<SysDataAuthDBEntity> sysDataAuthDBEntities = sysDataAuthMapperService.selectList(wrapper);
        return convertFromDB(sysDataAuthDBEntities);
    }

    private SysDataAuth convertFromDB(SysDataAuthDBEntity entity) {
        SysDataAuth data = BeanUtils.toBean(entity, SysDataAuth.class);
        return data;
    }

    private List<SysDataAuthDBEntity> convertToDB(List<SysDataAuth> dataList) {
        List<SysDataAuthDBEntity> entityList = BeanUtils.toBean(dataList, SysDataAuthDBEntity.class);
        return entityList;
    }

    private SysDataAuthDBEntity convertToDB(SysDataAuth data) {
        SysDataAuthDBEntity sysDataAuthDBEntity = BeanUtils.toBean(data, SysDataAuthDBEntity.class);
        return sysDataAuthDBEntity;
    }

    private List<SysDataAuth> convertFromDB(List<SysDataAuthDBEntity> entityList) {
        List<SysDataAuth> dataList = BeanUtils.toBean(entityList, SysDataAuth.class);
        return dataList;
    }
}
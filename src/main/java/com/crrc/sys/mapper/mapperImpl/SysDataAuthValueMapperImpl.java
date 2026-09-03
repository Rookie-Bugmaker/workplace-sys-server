package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthDBEntity;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.auth.SysDataAuthValueDBEntity;
import com.crrc.sys.mapper.SysDataAuthMapper;
import com.crrc.sys.mapper.SysDataAuthValueMapper;
import com.crrc.sys.mapper.mapperService.SysDataAuthMapperService;
import com.crrc.sys.mapper.mapperService.SysDataAuthValueMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mapper实现类，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-13
 */
@Repository
public class SysDataAuthValueMapperImpl extends ServiceImpl<SysDataAuthValueMapperService, SysDataAuthValueDBEntity> {

    @Resource
    private SysDataAuthValueMapperService sysDataAuthValueMapperService;

    @Resource
    private SysDataAuthMapperService sysDataAuthMapperService;


    public void insertList(List<SysDataAuthValue> dataAuthValueList) {
        List<SysDataAuthValueDBEntity> sysDataAuthValueDBEntities = convertToDB(dataAuthValueList);
        super.saveBatch(sysDataAuthValueDBEntities);
    }


    public void delete(long objectId) {
        LambdaQueryWrapper<SysDataAuthDBEntity> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.select(SysDataAuthDBEntity::getAuthId)
                .eq(SysDataAuthDBEntity::getObjectId, objectId);
        List<Long> authIds = sysDataAuthMapperService
                .selectList(wrapper1)
                .stream().map(SysDataAuthDBEntity::getAuthId)
                .collect(Collectors.toList());
        if(authIds != null && authIds.size() > 0) {
            LambdaUpdateWrapper<SysDataAuthValueDBEntity> wrapper2 = new LambdaUpdateWrapper<>();
            wrapper2.in(SysDataAuthValueDBEntity::getAuthId, authIds);
            sysDataAuthValueMapperService.delete(wrapper2);
        }
    }


    public void deleteByObjectIds(List<Long> objectIds) {
        LambdaQueryWrapper<SysDataAuthDBEntity> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.select(SysDataAuthDBEntity::getAuthId)
                .in(SysDataAuthDBEntity::getObjectId, objectIds);
        List<Long> authIds = sysDataAuthMapperService
                .selectList(wrapper1)
                .stream().map(SysDataAuthDBEntity::getAuthId)
                .collect(Collectors.toList());
        if(authIds != null && authIds.size() > 0) {
            LambdaUpdateWrapper<SysDataAuthValueDBEntity> wrapper2 = new LambdaUpdateWrapper<>();
            wrapper2.in(SysDataAuthValueDBEntity::getAuthId, authIds);
            sysDataAuthValueMapperService.delete(wrapper2);
        }
    }


    public List<SysDataAuthValue> findByAuthIds(List<Long> authIdList) {
        LambdaQueryWrapper<SysDataAuthValueDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysDataAuthValueDBEntity::getAuthId, authIdList);
        List<SysDataAuthValueDBEntity> sysDataAuthValueDBEntities = sysDataAuthValueMapperService.selectList(wrapper);
        return convertFromDB(sysDataAuthValueDBEntities);
    }

    /**
     * 根据数据权限ID集合删除权限值(级联删除)
     *
     * @param authIds 数据权限ID集合
     */
    public void deleteByAuthIds(List<Long> authIds) {
        if (authIds == null || authIds.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<SysDataAuthValueDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysDataAuthValueDBEntity::getAuthId, authIds);
        sysDataAuthValueMapperService.delete(wrapper);
    }

    /**
     * 按数据权限ID + 权限值集合精确删除权限值
     * 用于保留对象的权限值差集：只删除该权限下被移除的值，保留其余已存在的值行。
     *
     * @param authId     数据权限ID
     * @param authValues 待删除的权限值集合
     */
    public void deleteByAuthIdAndValues(long authId, List<String> authValues) {
        if (authValues == null || authValues.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<SysDataAuthValueDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysDataAuthValueDBEntity::getAuthId, authId)
                .in(SysDataAuthValueDBEntity::getAuthValue, authValues);
        sysDataAuthValueMapperService.delete(wrapper);
    }

    /**
     * 按数据权限ID + 权限值更新权限值描述
     * 用于保留对象的权限值描述更新：同一权限值已存在但前端传入了新的描述时，仅更新描述字段。
     *
     * @param authId        数据权限ID
     * @param authValue     权限值(唯一键定位行)
     * @param authValueDesc 新的权限值描述
     */
    public void updateDescByAuthIdAndValue(long authId, String authValue, String authValueDesc) {
        if (authValue == null || authValue.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<SysDataAuthValueDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysDataAuthValueDBEntity::getAuthId, authId)
                .eq(SysDataAuthValueDBEntity::getAuthValue, authValue)
                .set(SysDataAuthValueDBEntity::getAuthValueDesc, authValueDesc);
        sysDataAuthValueMapperService.update(wrapper);
    }

    private SysDataAuthValue convertFromDB(SysDataAuthValueDBEntity entity) {
        SysDataAuthValue data = BeanUtils.toBean(entity, SysDataAuthValue.class);
        return data;
    }

    private List<SysDataAuthValueDBEntity> convertToDB(List<SysDataAuthValue> dataList) {
        List<SysDataAuthValueDBEntity> entityList = BeanUtils.toBean(dataList, SysDataAuthValueDBEntity.class);
        return entityList;
    }

    private SysDataAuthValueDBEntity convertToDB(SysDataAuthValue data) {
        SysDataAuthValueDBEntity entity = BeanUtils.toBean(data, SysDataAuthValueDBEntity.class);
        return entity;
    }

    private List<SysDataAuthValue> convertFromDB(List<SysDataAuthValueDBEntity> entityList) {
        List<SysDataAuthValue> dataList = BeanUtils.toBean(entityList, SysDataAuthValue.class);
        return dataList;
    }
}
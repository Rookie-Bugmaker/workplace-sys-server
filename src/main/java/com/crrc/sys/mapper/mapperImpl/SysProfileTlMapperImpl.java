package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.entity.profile.SysProfileTl;
import com.crrc.sys.entity.profile.SysProfileTlDBEntity;
import com.crrc.sys.mapper.SysProfileTlMapper;
import com.crrc.sys.mapper.mapperService.SysProfileTlMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置文件国际化-mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-17
 */
@Repository
public class SysProfileTlMapperImpl extends ServiceImpl<SysProfileTlMapperService, SysProfileTlDBEntity> implements SysProfileTlMapper {

    @Resource
    private SysProfileTlMapperService sysProfileTlMapperService;

    @Override
    public void insertList(List<SysProfileTl> tlList) {
        List<SysProfileTlDBEntity> entities = convertToDB(tlList);
        super.saveBatch(entities);
    }

    @Override
    public void update(SysProfile profile) {
        LambdaUpdateWrapper<SysProfileTlDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysProfileTlDBEntity::getProfileName, profile.getProfileName())
                .set(SysProfileTlDBEntity::getLastUpdateBy, profile.getLastUpdateBy())
                .set(SysProfileTlDBEntity::getLastUpdateDate, profile.getLastUpdateDate())
                .set(SysProfileTlDBEntity::getLastUpdateLogin, profile.getLastUpdateLogin())
                .eq(SysProfileTlDBEntity::getProfileId, profile.getProfileId())
                .eq(SysProfileTlDBEntity::getLanguage, profile.getLanguage());

        sysProfileTlMapperService.update(wrapper);
    }

    @Override
    public void delete(List<Long> profileIdList) {
        LambdaQueryWrapper<SysProfileTlDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysProfileTlDBEntity::getProfileId, profileIdList);
        sysProfileTlMapperService.delete(wrapper);
    }

    private SysProfileTlDBEntity convertToDB(SysProfileTl data) {
        SysProfileTlDBEntity entity = BeanUtils.toBean(data, SysProfileTlDBEntity.class);
        return entity;
    }

    private SysProfileTl convertFromDB(SysProfileTlDBEntity entity) {
        SysProfileTl data = BeanUtils.toBean(entity, SysProfileTl.class);
        return data;
    }

    private List<SysProfileTlDBEntity> convertToDB(List<SysProfileTl> dataList) {
        List<SysProfileTlDBEntity> entityList = BeanUtils.toBean(dataList, SysProfileTlDBEntity.class);
        return entityList;
    }

    private List<SysProfileTl> convertFromDB(List<SysProfileTlDBEntity> entityList) {
        List<SysProfileTl> dataList = BeanUtils.toBean(entityList, SysProfileTl.class);
        return dataList;
    }
}
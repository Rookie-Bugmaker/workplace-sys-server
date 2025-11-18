package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.homepage.*;
import com.crrc.sys.entity.homepage.SysHomePageUserConfig;
import com.crrc.sys.entity.homepage.SysHomePageUserConfigDBEntity;
import com.crrc.sys.mapper.SysHomePageUserConfigMapper;
import com.crrc.sys.mapper.mapperService.SysHomePageUserConfigMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户主页自定义配置-mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-18
 */
@Repository
public class SysHomePageUserConfigMapperImpl extends ServiceImpl<SysHomePageUserConfigMapperService, SysHomePageUserConfigDBEntity> implements SysHomePageUserConfigMapper {

    @Resource
    private SysHomePageUserConfigMapperService sysHomePageUserConfigMapperService;

    @Override
    public Integer add(SysHomePageUserConfig param) {
        SysHomePageUserConfigDBEntity sysHomePageUserConfigDBEntity = convertToDB(param);
        return sysHomePageUserConfigMapperService.insert(sysHomePageUserConfigDBEntity);
    }

    @Override
    public SysHomePageUserConfig findByUserId(Long userId) {
        LambdaQueryWrapper<SysHomePageUserConfigDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysHomePageUserConfigDBEntity::getUserId, userId);
        SysHomePageUserConfigDBEntity entity = sysHomePageUserConfigMapperService.selectOne(wrapper);
        return convertFromDB(entity);
    }

    @Override
    public Integer deleteByUserId(Long userId) {
        LambdaUpdateWrapper<SysHomePageUserConfigDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysHomePageUserConfigDBEntity::getUserId, userId);
        return sysHomePageUserConfigMapperService.delete(wrapper);
    }

    private SysHomePageUserConfigDBEntity convertToDB(SysHomePageUserConfig data) {
        SysHomePageUserConfigDBEntity entity = BeanUtils.toBean(data, SysHomePageUserConfigDBEntity.class);
        return entity;
    }

    private SysHomePageUserConfig convertFromDB(SysHomePageUserConfigDBEntity entity) {
        SysHomePageUserConfig data = BeanUtils.toBean(entity, SysHomePageUserConfig.class);
        return data;
    }
}
package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.api.SysApiAuth;
import com.crrc.sys.entity.api.SysApiAuthDBEntity;
import com.crrc.sys.entity.function.column.SysFunctionColumn;
import com.crrc.sys.entity.function.column.SysFunctionColumnBDBEntity;
import com.crrc.sys.entity.function.column.SysFunctionColumnTlDBEntity;
import com.crrc.sys.entity.homepage.*;
import com.crrc.sys.mapper.SysHomePageComponentMapper;
import com.crrc.sys.mapper.mapperService.SysHomePageComponentMapperService;
import com.crrc.sys.mapper.mapperService.SysHomePageComponentUserMapperService;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主页组件用户分配-mapper实现类，通过MyBatisPlus方案重写SQL
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Repository
public class SysHomePageComponentMapperImpl extends ServiceImpl<SysHomePageComponentUserMapperService, SysHomePageComponentUserDBEntity> {

    @Resource
    private SysHomePageComponentUserMapperService sysHomePageComponentUserMapperService;

    @Resource
    private SysHomePageComponentMapperService sysHomePageComponentMapperService;


    public Integer add(SysHomePageComponent param) {
        SysHomePageComponentDBEntity sysHomePageComponentDBEntity = convertToDB(param);
        return sysHomePageComponentMapperService.insert(sysHomePageComponentDBEntity);
    }


    public List<SysHomePageComponent> findList(HomePageParam param) {
        LambdaQueryWrapper<SysHomePageComponentDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(param.getHomePageTitle()), SysHomePageComponentDBEntity::getHomePageTitle, param.getHomePageTitle())
                .like(StringUtils.isNotBlank(param.getFunctionName()), SysHomePageComponentDBEntity::getFunctionName, param.getFunctionName())
                .eq(StringUtils.isNotBlank(param.getStatus()), SysHomePageComponentDBEntity::getStatus, param.getStatus());
        List<SysHomePageComponentDBEntity> sysHomePageComponentDBEntities = sysHomePageComponentMapperService.selectList(wrapper);
        return convertFromDB(sysHomePageComponentDBEntities);
    }

    public IPage<SysHomePageComponent> findPage(HomePageParam param) {
        LambdaQueryWrapper<SysHomePageComponentDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(param.getHomePageTitle()), SysHomePageComponentDBEntity::getHomePageTitle, param.getHomePageTitle())
                .like(StringUtils.isNotBlank(param.getFunctionName()), SysHomePageComponentDBEntity::getFunctionName, param.getFunctionName())
                .eq(StringUtils.isNotBlank(param.getStatus()), SysHomePageComponentDBEntity::getStatus, param.getStatus());
        Page<SysHomePageComponentDBEntity> midPage = sysHomePageComponentMapperService.selectPage(
                new Page<>(param.getPage(), param.getPageSize()), wrapper);
        IPage<SysHomePageComponent> resultPage = new Page<>(midPage.getCurrent(), midPage.getSize(), midPage.getTotal());
        resultPage.setRecords(convertFromDB(midPage.getRecords()));
        return resultPage;
    }


    public Integer deleteById(Long componentId) {
        return sysHomePageComponentMapperService.deleteById(componentId);
    }


    public Integer updateById(SysHomePageComponent param) {
        LambdaUpdateWrapper<SysHomePageComponentDBEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .set(SysHomePageComponentDBEntity::getHomePageTitle, param.getHomePageTitle())
                .set(SysHomePageComponentDBEntity::getFunctionName, param.getFunctionName())
                .set(SysHomePageComponentDBEntity::getFunctionCode, param.getFunctionCode())
                .set(SysHomePageComponentDBEntity::getRefreshRate, param.getRefreshRate())
                .set(SysHomePageComponentDBEntity::getLoadingType, param.getLoadingType())
                .set(SysHomePageComponentDBEntity::getUserType, param.getUserType())
                .set(SysHomePageComponentDBEntity::getRemarks, param.getRemarks())
                .set(SysHomePageComponentDBEntity::getStatus, param.getStatus())
                .set(SysHomePageComponentDBEntity::getShowStatus, param.getShowStatus())
                .set(SysHomePageComponentDBEntity::getFunctionUrl, param.getFunctionUrl())
                .set(SysHomePageComponentDBEntity::getLastUpdateDate, param.getLastUpdateDate())
                .set(SysHomePageComponentDBEntity::getLastUpdateBy, param.getLastUpdateBy())
                .set(SysHomePageComponentDBEntity::getLastUpdateLogin, param.getLastUpdateLogin())
                .eq(SysHomePageComponentDBEntity::getComponentId, param.getComponentId());

        return sysHomePageComponentMapperService.update(updateWrapper);
    }


    public Integer addUser(List<SysHomePageComponentUser> list) {
        List<SysHomePageComponentUserDBEntity> sysHomePageComponentUserDBEntities = convertToDB(list);
        return super.saveBatch(sysHomePageComponentUserDBEntities) ? sysHomePageComponentUserDBEntities.size() : 0;
    }


    public List<Long> findUserByComponentId(Long componentId) {
        LambdaQueryWrapper<SysHomePageComponentUserDBEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .select(SysHomePageComponentUserDBEntity::getUserId)
                .eq(SysHomePageComponentUserDBEntity::getComponentId, componentId);

        return sysHomePageComponentUserMapperService.selectList(queryWrapper)
                .stream()
                .map(SysHomePageComponentUserDBEntity::getUserId)
                .collect(Collectors.toList());
    }


    public List<SysHomePageComponent> findComponentByUserId(Long userId) {
        MPJLambdaWrapper<SysHomePageComponentUserDBEntity> wrapper = JoinWrappers.lambda(SysHomePageComponentUserDBEntity.class)
                .selectAll(SysHomePageComponentDBEntity.class)
                .innerJoin(SysHomePageComponentDBEntity.class, SysHomePageComponentDBEntity::getComponentId, SysHomePageComponentUserDBEntity::getComponentId)
                .eq(SysHomePageComponentDBEntity::getLoadingType, "2")
                .eq(SysHomePageComponentUserDBEntity::getUserId, userId);
        return sysHomePageComponentUserMapperService.selectJoinList(SysHomePageComponent.class, wrapper);
    }


    public List<SysHomePageComponent> findComponentByUserType(String userType) {
        LambdaQueryWrapper<SysHomePageComponentDBEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(SysHomePageComponentDBEntity::getLoadingType, 1)
                .like(SysHomePageComponentDBEntity::getUserType, userType);
        List<SysHomePageComponentDBEntity> sysHomePageComponentDBEntities = sysHomePageComponentMapperService.selectList(queryWrapper);
        return convertFromDB(sysHomePageComponentDBEntities);
    }


    public Integer deleteUserByComponentId(Long componentId) {
        LambdaQueryWrapper<SysHomePageComponentUserDBEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysHomePageComponentUserDBEntity::getComponentId, componentId);

        return sysHomePageComponentUserMapperService.delete(queryWrapper);
    }

    private SysHomePageComponentDBEntity convertToDB(SysHomePageComponent data) {
        SysHomePageComponentDBEntity entity = BeanUtils.toBean(data, SysHomePageComponentDBEntity.class);
        return entity;
    }

    private List<SysHomePageComponentUserDBEntity> convertToDB(List<SysHomePageComponentUser> dataList) {
        List<SysHomePageComponentUserDBEntity> entityList = BeanUtils.toBean(dataList, SysHomePageComponentUserDBEntity.class);
        return entityList;
    }

    private List<SysHomePageComponent> convertFromDB(List<SysHomePageComponentDBEntity> entityList) {
        List<SysHomePageComponent> dataList = BeanUtils.toBean(entityList, SysHomePageComponent.class);
        return dataList;
    }
}
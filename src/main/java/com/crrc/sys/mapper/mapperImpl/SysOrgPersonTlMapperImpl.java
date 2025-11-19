package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonTl;
import com.crrc.sys.entity.org.person.SysOrgPersonTlDBEntity;
import com.crrc.sys.mapper.SysOrgPersonTlMapper;
import com.crrc.sys.mapper.mapperService.SysOrgPersonTlMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织架构-人员国际化-mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-17
 */
@Repository
public class SysOrgPersonTlMapperImpl extends ServiceImpl<SysOrgPersonTlMapperService, SysOrgPersonTlDBEntity> implements SysOrgPersonTlMapper {

    @Resource
    private SysOrgPersonTlMapperService sysOrgPersonTlMapperService;

    @Override
    public void insertList(List<SysOrgPersonTl> personTlList) {
        // 转换为数据库实体
        List<SysOrgPersonTlDBEntity> entities = convertToDB(personTlList);
        // 批量保存
        super.saveBatch(entities);
    }

    @Override
    public void update(SysOrgPerson person) {
        LambdaUpdateWrapper<SysOrgPersonTlDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysOrgPersonTlDBEntity::getPersonId, person.getPersonId())
                .eq(SysOrgPersonTlDBEntity::getLanguage, person.getLanguage())
                .set(SysOrgPersonTlDBEntity::getFullName, person.getFullName())
                .set(SysOrgPersonTlDBEntity::getLastUpdateBy, person.getLastUpdateBy())
                .set(SysOrgPersonTlDBEntity::getLastUpdateDate, person.getLastUpdateDate())
                .set(SysOrgPersonTlDBEntity::getLastUpdateLogin, person.getLastUpdateLogin());

        sysOrgPersonTlMapperService.update(wrapper);
    }

    @Override
    public void delete(List<Long> personIdList) {
        LambdaQueryWrapper<SysOrgPersonTlDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysOrgPersonTlDBEntity::getPersonId, personIdList);
        sysOrgPersonTlMapperService.delete(wrapper);
    }

    private SysOrgPersonTlDBEntity convertToDB(SysOrgPersonTl data) {
        SysOrgPersonTlDBEntity entity = BeanUtils.toBean(data, SysOrgPersonTlDBEntity.class);
        return entity;
    }

    private SysOrgPersonTl convertFromDB(SysOrgPersonTlDBEntity entity) {
        SysOrgPersonTl data = BeanUtils.toBean(entity, SysOrgPersonTl.class);
        return data;
    }

    private List<SysOrgPersonTlDBEntity> convertToDB(List<SysOrgPersonTl> dataList) {
        List<SysOrgPersonTlDBEntity> entityList = BeanUtils.toBean(dataList, SysOrgPersonTlDBEntity.class);
        return entityList;
    }

    private List<SysOrgPersonTl> convertFromDB(List<SysOrgPersonTlDBEntity> entityList) {
        List<SysOrgPersonTl> dataList = BeanUtils.toBean(entityList, SysOrgPersonTl.class);
        return dataList;
    }
}
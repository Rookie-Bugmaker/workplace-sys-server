package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.MyBatisUtil;
import com.crrc.sys.entity.homepage.SysHomePageComponent;
import com.crrc.sys.entity.homepage.SysHomePageComponentDBEntity;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonBDBEntity;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.entity.org.person.SysOrgPersonTlDBEntity;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserDBEntity;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.mapper.SysOrgPersonMapper;
import com.crrc.sys.mapper.mapperService.SysOrgPersonBMapperService;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织架构-人员-mapper实现类，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-17
 */
@Repository
public class SysOrgPersonMapperImpl extends ServiceImpl<SysOrgPersonBMapperService, SysOrgPersonBDBEntity> {

    @Resource
    private SysOrgPersonBMapperService sysOrgPersonBMapperService;


    public List<SysOrgPerson> findByFuzzy(SysOrgPersonFindByPageParam param) {
        MPJLambdaWrapper<SysOrgPersonBDBEntity> wrapper = JoinWrappers.lambda(SysOrgPersonBDBEntity.class)
                .selectAll(SysOrgPersonBDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, param.getLanguage())
                );

        if (param.getStatus() != null) {
            wrapper.eq(SysOrgPersonBDBEntity::getStatus, param.getStatus());
        }

        if (!CollectionUtils.isEmpty(param.getPersonTypeList())) {
            wrapper.in(SysOrgPersonBDBEntity::getPersonType, param.getPersonTypeList());
        }

        if (StringUtils.hasText(param.getFullName()) && StringUtils.hasText(param.getJobNo())) {
            if("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName())
                        .or()
                        .like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo()));
            }else {
                wrapper.and(w -> w.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName())
                        .like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo()));
            }

        } else {
            if (StringUtils.hasText(param.getFullName())) {
                wrapper.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName());
            }
            if (StringUtils.hasText(param.getJobNo())) {
                wrapper.like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo());
            }
        }
        wrapper.orderByDesc(SysOrgPersonBDBEntity::getPersonId);
        return sysOrgPersonBMapperService.selectJoinList(SysOrgPerson.class, wrapper);
    }

    public IPage<SysOrgPerson> findByFuzzyPage(SysOrgPersonFindByPageParam param) {
        MPJLambdaWrapper<SysOrgPersonBDBEntity> wrapper = JoinWrappers.lambda(SysOrgPersonBDBEntity.class)
                .selectAll(SysOrgPersonBDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, param.getLanguage())
                );

        if (param.getStatus() != null) {
            wrapper.eq(SysOrgPersonBDBEntity::getStatus, param.getStatus());
        }

        if (!CollectionUtils.isEmpty(param.getPersonTypeList())) {
            wrapper.in(SysOrgPersonBDBEntity::getPersonType, param.getPersonTypeList());
        }

        if (StringUtils.hasText(param.getFullName()) && StringUtils.hasText(param.getJobNo())) {
            if("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName())
                        .or()
                        .like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo()));
            }else {
                wrapper.and(w -> w.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName())
                        .like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo()));
            }

        } else {
            if (StringUtils.hasText(param.getFullName())) {
                wrapper.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName());
            }
            if (StringUtils.hasText(param.getJobNo())) {
                wrapper.like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo());
            }
        }
        wrapper.orderByDesc(SysOrgPersonBDBEntity::getPersonId);
        Page<SysOrgPerson> page = sysOrgPersonBMapperService.selectJoinPage(
                MyBatisUtil.makePage(param.getPage(), param.getPageSize()), SysOrgPerson.class, wrapper);
        return page;
    }


    public int insert(SysOrgPerson person) {
        SysOrgPersonBDBEntity entity = convertToDB(person);
        return sysOrgPersonBMapperService.insert(entity);
    }


    public int update(SysOrgPerson person) {
        LambdaUpdateWrapper<SysOrgPersonBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysOrgPersonBDBEntity::getPersonId, person.getPersonId())
                .set(SysOrgPersonBDBEntity::getGender, person.getGender())
                .set(SysOrgPersonBDBEntity::getJobNo, person.getJobNo())
                .set(SysOrgPersonBDBEntity::getPersonType, person.getPersonType())
                .set(SysOrgPersonBDBEntity::getPhone, person.getPhone())
                .set(SysOrgPersonBDBEntity::getEmail, person.getEmail())
                .set(SysOrgPersonBDBEntity::getStatus, person.getStatus())
                .set(SysOrgPersonBDBEntity::getInductionDate, person.getInductionDate())
                .set(SysOrgPersonBDBEntity::getQuitDate, person.getQuitDate())
                .set(SysOrgPersonBDBEntity::getLastUpdateBy, person.getLastUpdateBy())
                .set(SysOrgPersonBDBEntity::getLastUpdateDate, person.getLastUpdateDate())
                .set(SysOrgPersonBDBEntity::getLastUpdateLogin, person.getLastUpdateLogin());

        return sysOrgPersonBMapperService.update(wrapper);
    }


    public SysOrgPerson findById(long personId, String language) {
        MPJLambdaWrapper<SysOrgPersonBDBEntity> wrapper = JoinWrappers.lambda(SysOrgPersonBDBEntity.class)
                .selectAll(SysOrgPersonBDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, language)
                )
                .eq(SysOrgPersonBDBEntity::getPersonId, personId);
        return sysOrgPersonBMapperService.selectJoinOne(SysOrgPerson.class, wrapper);
    }


    public int delete(List<Long> personIdList) {
        LambdaQueryWrapper<SysOrgPersonBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysOrgPersonBDBEntity::getPersonId, personIdList);
        return sysOrgPersonBMapperService.delete(wrapper);
    }


    public SysOrgPerson findByJobNo(String jobNo) {
        MPJLambdaWrapper<SysOrgPersonBDBEntity> wrapper = JoinWrappers.lambda(SysOrgPersonBDBEntity.class);
        wrapper.selectAll(SysOrgPersonBDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, LanguageEnum.ZH_CN.getValue())
                )
                .eq(SysOrgPersonBDBEntity::getJobNo, jobNo);
        return sysOrgPersonBMapperService.selectJoinOne(SysOrgPerson.class, wrapper);
    }


    public List<SysOrgPerson> findByJobNos(List<String> jobNos) {
        MPJLambdaWrapper<SysOrgPersonBDBEntity> wrapper = JoinWrappers.lambda(SysOrgPersonBDBEntity.class);
        wrapper.selectAll(SysOrgPersonBDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, LanguageEnum.ZH_CN.getValue())
                );
        if (!CollectionUtils.isEmpty(jobNos)) {
            wrapper.in(SysOrgPersonBDBEntity::getJobNo, jobNos);
        }
        return sysOrgPersonBMapperService.selectJoinList(SysOrgPerson.class, wrapper);
    }


    public List<SysOrgPerson> findByPersonIds(List<Long> personIdList, String language) {
        MPJLambdaWrapper<SysOrgPersonBDBEntity> wrapper = JoinWrappers.lambda(SysOrgPersonBDBEntity.class)
                .selectAll(SysOrgPersonBDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, language)
                )
                .in(SysOrgPersonBDBEntity::getPersonId, personIdList);
        return sysOrgPersonBMapperService.selectJoinList(SysOrgPerson.class, wrapper);
    }


    public List<SysOrgPerson> findByIds(List<Long> personIds, String language) {
        MPJLambdaWrapper<SysOrgPersonBDBEntity> wrapper = JoinWrappers.lambda(SysOrgPersonBDBEntity.class)
                .selectAll(SysOrgPersonBDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, language)
                )
                .in(SysOrgPersonBDBEntity::getPersonId, personIds);
        return sysOrgPersonBMapperService.selectJoinList(SysOrgPerson.class, wrapper);
    }


    public boolean insertBatch(List<SysOrgPerson> persons) {
        List<SysOrgPersonBDBEntity> entities = convertToDB(persons);
        return super.saveBatch(entities);
    }


    public List<SysOrgPerson> findByQuitDate(Date nowDate) {
        LambdaQueryWrapper<SysOrgPersonBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(SysOrgPersonBDBEntity::getQuitDate)
                .le(SysOrgPersonBDBEntity::getQuitDate, nowDate);
        List<SysOrgPersonBDBEntity> entities = sysOrgPersonBMapperService.selectList(wrapper);
        return convertFromDB(entities);
    }


    public void updateStatus(List<Long> personIdList, int status) {
        LambdaUpdateWrapper<SysOrgPersonBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysOrgPersonBDBEntity::getPersonId, personIdList)
                .set(SysOrgPersonBDBEntity::getStatus, status);
        sysOrgPersonBMapperService.update(wrapper);
    }


    public List<SysOrgPerson> findByPhoneList(List<String> phoneList) {
        LambdaQueryWrapper<SysOrgPersonBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysOrgPersonBDBEntity::getPhone, phoneList);
        List<SysOrgPersonBDBEntity> entities = sysOrgPersonBMapperService.selectList(wrapper);
        return convertFromDB(entities);
    }

    private SysOrgPersonBDBEntity convertToDB(SysOrgPerson data) {
        SysOrgPersonBDBEntity entity = BeanUtils.toBean(data, SysOrgPersonBDBEntity.class);
        return entity;
    }

    private SysOrgPerson convertFromDB(SysOrgPersonBDBEntity entity) {
        SysOrgPerson data = BeanUtils.toBean(entity, SysOrgPerson.class);
        return data;
    }

    private List<SysOrgPersonBDBEntity> convertToDB(List<SysOrgPerson> dataList) {
        List<SysOrgPersonBDBEntity> entityList = BeanUtils.toBean(dataList, SysOrgPersonBDBEntity.class);
        return entityList;
    }

    private List<SysOrgPerson> convertFromDB(List<SysOrgPersonBDBEntity> entityList) {
        List<SysOrgPerson> dataList = BeanUtils.toBean(entityList, SysOrgPerson.class);
        return dataList;
    }

    public Page<SysOrgPerson> findPersonPageWithoutCreatedUser(Long personId, String jobNo, String personType, Integer page, Integer pageSize) {
        MPJLambdaWrapper<SysOrgPersonBDBEntity> wrapper = JoinWrappers.lambda(SysOrgPersonBDBEntity.class);
        wrapper.selectAll(SysOrgPersonBDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class, fn -> fn
                                .eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, LanguageEnum.ZH_CN.getValue())
                )
                .leftJoin(SysUserDBEntity.class, SysUserDBEntity::getUserRelationId, SysOrgPersonBDBEntity::getPersonId)
                .isNull(SysUserDBEntity::getUserId)
                .notExists(SysUserDBEntity.class, w -> {
                    return w.select(SysUserDBEntity::getUserName)
                            .eq(SysUserDBEntity::getUserName, SysOrgPersonBDBEntity::getJobNo); //剔除工号与用户名相同的数据
                });
        if (StringUtils.hasText(jobNo)) {
            wrapper.like(SysOrgPersonBDBEntity::getJobNo, jobNo);
        }
        if (personId != null) {
            wrapper.eq(SysOrgPersonBDBEntity::getPersonId, personId);
        }
        if (StringUtils.hasText(personType)) {
            wrapper.eq(SysOrgPersonBDBEntity::getPersonType, personType);
        }
        wrapper.orderByDesc(SysOrgPersonBDBEntity::getPersonId);
        return sysOrgPersonBMapperService.selectJoinPage(new Page<>(page, pageSize), SysOrgPerson.class, wrapper);
    }
}
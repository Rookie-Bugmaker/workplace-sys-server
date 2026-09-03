package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.MyBatisUtil;
import com.crrc.sys.entity.api.*;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthDBEntity;
import com.crrc.sys.entity.auth.SysDataAuthValueDBEntity;
import com.crrc.sys.entity.org.person.SysOrgPersonBDBEntity;
import com.crrc.sys.entity.org.person.SysOrgPersonTlDBEntity;
import com.crrc.sys.entity.role.SysRoleBDBEntity;
import com.crrc.sys.entity.role.SysRoleTlDBEntity;
import com.crrc.sys.entity.user.SysUserDBEntity;
import com.crrc.sys.enums.DataAuthTypeEnum;
import com.crrc.sys.mapper.SysDataAuthMapper;
import com.crrc.sys.mapper.mapperService.SysApiMapperService;
import com.crrc.sys.mapper.mapperService.SysDataAuthMapperService;
import com.crrc.sys.model.profile.ProfileBindObjectRowDTO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mapper实现类，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-13
 */
@Repository
public class SysDataAuthMapperImpl extends ServiceImpl<SysDataAuthMapperService, SysDataAuthDBEntity> {

    @Resource
    private SysDataAuthMapperService sysDataAuthMapperService;


    public void insertList(List<SysDataAuth> dataAuthList) {
        List<SysDataAuthDBEntity> sysDataAuthDBEntities = convertToDB(dataAuthList);
        super.saveBatch(sysDataAuthDBEntities);
    }


    /**
     * 根据配置文件ID + 对象类型查询已绑定的对象ID集合(配置文件维度)
     * 用于全量替换语义下计算差集
     *
     * @param profileId 配置文件ID
     * @param authType  对象类型(user:用户、role:角色)
     * @return 已绑定的对象ID集合
     */
    public List<Long> findObjectIdsByProfileIdAndAuthType(long profileId, String authType) {
        LambdaQueryWrapper<SysDataAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysDataAuthDBEntity::getObjectId)
                .eq(SysDataAuthDBEntity::getProfileId, profileId)
                .eq(SysDataAuthDBEntity::getAuthType, authType);
        return sysDataAuthMapperService.selectList(wrapper)
                .stream().map(SysDataAuthDBEntity::getObjectId).collect(Collectors.toList());
    }


    public void delete(long objectId) {
        LambdaUpdateWrapper<SysDataAuthDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysDataAuthDBEntity::getObjectId, objectId);
        sysDataAuthMapperService.delete(wrapper);
    }


    public void deleteByObjectIds(List<Long> objectIds) {
        LambdaUpdateWrapper<SysDataAuthDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysDataAuthDBEntity::getObjectId, objectIds);
        sysDataAuthMapperService.delete(wrapper);
    }


    public List<SysDataAuth> findByObjectId(long objectId) {
        LambdaQueryWrapper<SysDataAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataAuthDBEntity::getObjectId, objectId);
        List<SysDataAuthDBEntity> sysDataAuthDBEntities = sysDataAuthMapperService.selectList(wrapper);
        return convertFromDB(sysDataAuthDBEntities);
    }


    /**
     * 按配置文件ID + 对象类型查询绑定关系(配置文件维度)
     *
     * @param profileId 配置文件ID
     * @param authType  对象类型(user:用户、role:角色)
     * @return 绑定关系集合
     */
    public List<SysDataAuth> findByProfileIdAndAuthType(long profileId, String authType) {
        LambdaQueryWrapper<SysDataAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataAuthDBEntity::getProfileId, profileId)
                .eq(SysDataAuthDBEntity::getAuthType, authType);
        List<SysDataAuthDBEntity> sysDataAuthDBEntities = sysDataAuthMapperService.selectList(wrapper);
        return convertFromDB(sysDataAuthDBEntities);
    }

    /**
     * 分页查询配置文件绑定的用户(配置文件维度，支持用户名/用户描述模糊查询 + 权限值过滤)
     * 主表 sys_data_auth(authType=user)，关联 sys_user 取用户名、sys_org_person_tl 取姓名(用户描述)。
     * authValue 非空时按 sys_data_auth_value 过滤，并以 authId 分组去重，避免一对多导致分页重复。
     *
     * @param page             分页对象
     * @param profileId        配置文件ID
     * @param objectSearchParam 对象模糊关键字(匹配 sys_user.user_name 或 sys_org_person_tl.full_name)
     * @param authValue        权限值过滤(为空表示不限)
     * @return 分页行投影
     */
    public IPage<ProfileBindObjectRowDTO> findBindUserPage(Page<ProfileBindObjectRowDTO> page,
                                                           long profileId,
                                                           String objectSearchParam,
                                                           String authValue) {
        MPJLambdaWrapper<SysDataAuthDBEntity> wrapper = new MPJLambdaWrapper<SysDataAuthDBEntity>()
                .select(SysDataAuthDBEntity::getAuthId, SysDataAuthDBEntity::getObjectId)
                .select(SysUserDBEntity::getUserName)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysUserDBEntity.class, SysUserDBEntity::getUserId, SysDataAuthDBEntity::getObjectId)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                )
                .eq(SysDataAuthDBEntity::getProfileId, profileId)
                .eq(SysDataAuthDBEntity::getAuthType, DataAuthTypeEnum.USER.getValue());

        if (StringUtils.isNotBlank(objectSearchParam)) {
            String kw = objectSearchParam.trim();
            wrapper.and(w -> w.like(SysUserDBEntity::getUserName, kw)
                    .or()
                    .like(SysOrgPersonTlDBEntity::getFullName, kw));
        }
        if (StringUtils.isNotBlank(authValue)) {
            wrapper.innerJoin(SysDataAuthValueDBEntity.class,
                            SysDataAuthValueDBEntity::getAuthId, SysDataAuthDBEntity::getAuthId)
                    .eq(SysDataAuthValueDBEntity::getAuthValue, authValue.trim());
        }
        //按 authId 分组去重：对象可能有多行权限值，避免分页计数与记录重复
        wrapper.groupBy(SysDataAuthDBEntity::getAuthId)
                .orderByDesc(SysDataAuthDBEntity::getAuthId);

        return sysDataAuthMapperService.selectJoinPage(page, ProfileBindObjectRowDTO.class, wrapper);
    }

    /**
     * 分页查询配置文件绑定的角色(配置文件维度，支持角色编码/角色名称模糊查询 + 权限值过滤)
     * 主表 sys_data_auth(authType=role)，关联 sys_role_b 取角色编码、sys_role_tl 取角色名称。
     * authValue 非空时按 sys_data_auth_value 过滤，并以 authId 分组去重。
     *
     * @param page             分页对象
     * @param profileId        配置文件ID
     * @param language         角色名称多语言
     * @param objectSearchParam 对象模糊关键字(匹配 sys_role_b.role_code 或 sys_role_tl.role_name)
     * @param authValue        权限值过滤(为空表示不限)
     * @return 分页行投影
     */
    public IPage<ProfileBindObjectRowDTO> findBindRolePage(Page<ProfileBindObjectRowDTO> page,
                                                           long profileId,
                                                           String language,
                                                           String objectSearchParam,
                                                           String authValue) {
        MPJLambdaWrapper<SysDataAuthDBEntity> wrapper = new MPJLambdaWrapper<SysDataAuthDBEntity>()
                .select(SysDataAuthDBEntity::getAuthId, SysDataAuthDBEntity::getObjectId)
                .select(SysRoleBDBEntity::getRoleCode)
                .select(SysRoleTlDBEntity::getRoleName)
                .innerJoin(SysRoleBDBEntity.class, SysRoleBDBEntity::getRoleId, SysDataAuthDBEntity::getObjectId)
                .innerJoin(SysRoleTlDBEntity.class,
                        fn -> fn.eq(SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                                .eq(SysRoleTlDBEntity::getLanguage, language)
                )
                .eq(SysDataAuthDBEntity::getProfileId, profileId)
                .eq(SysDataAuthDBEntity::getAuthType, DataAuthTypeEnum.ROLE.getValue());

        if (StringUtils.isNotBlank(objectSearchParam)) {
            String kw = objectSearchParam.trim();
            wrapper.and(w -> w.like(SysRoleBDBEntity::getRoleCode, kw)
                    .or()
                    .like(SysRoleTlDBEntity::getRoleName, kw));
        }
        if (StringUtils.isNotBlank(authValue)) {
            wrapper.innerJoin(SysDataAuthValueDBEntity.class,
                            SysDataAuthValueDBEntity::getAuthId, SysDataAuthDBEntity::getAuthId)
                    .eq(SysDataAuthValueDBEntity::getAuthValue, authValue.trim());
        }
        wrapper.groupBy(SysDataAuthDBEntity::getAuthId)
                .orderByDesc(SysDataAuthDBEntity::getAuthId);

        return sysDataAuthMapperService.selectJoinPage(page, ProfileBindObjectRowDTO.class, wrapper);
    }

    /**
     * 按配置文件ID + 对象类型 + 对象ID集合删除绑定关系(差集删除)
     * 同时返回被删除记录的 authId 集合，用于级联删除 sys_data_auth_value
     *
     * @param profileId  配置文件ID
     * @param authType   对象类型(user:用户、role:角色)
     * @param objectIds  待删除的对象ID集合
     * @return 被删除记录的 authId 集合
     */
    public List<Long> deleteByProfileIdAndAuthType(long profileId, String authType, List<Long> objectIds) {
        if (objectIds == null || objectIds.isEmpty()) {
            return new ArrayList<>();
        }
        //1.先查出待删除记录的 authId(用于级联删除权限值)
        LambdaQueryWrapper<SysDataAuthDBEntity> query = new LambdaQueryWrapper<>();
        query.select(SysDataAuthDBEntity::getAuthId)
                .eq(SysDataAuthDBEntity::getProfileId, profileId)
                .eq(SysDataAuthDBEntity::getAuthType, authType)
                .in(SysDataAuthDBEntity::getObjectId, objectIds);
        List<Long> authIds = sysDataAuthMapperService.selectList(query)
                .stream().map(SysDataAuthDBEntity::getAuthId).collect(Collectors.toList());
        //2.删除主表
        if (!authIds.isEmpty()) {
            LambdaUpdateWrapper<SysDataAuthDBEntity> delete = new LambdaUpdateWrapper<>();
            delete.in(SysDataAuthDBEntity::getAuthId, authIds);
            sysDataAuthMapperService.delete(delete);
        }
        return authIds;
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
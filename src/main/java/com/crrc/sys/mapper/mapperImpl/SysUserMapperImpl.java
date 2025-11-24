package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.org.person.*;
import com.crrc.sys.entity.profile.SysProfileBDBEntity;
import com.crrc.sys.entity.role.SysRoleBDBEntity;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserDBEntity;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.entity.user.SysUserRoleDBEntity;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.mapper.SysUserMapper;
import com.crrc.sys.mapper.mapperService.SysUserMapperService;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.model.user.SysOnlineUserParamDTO;
import com.crrc.sys.model.user.SysOnlineUserVO;
import com.crrc.sys.model.user.SysUserInfoDBJoinDTO;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户表-mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-20
 */
@Repository
public class SysUserMapperImpl extends ServiceImpl<SysUserMapperService, SysUserDBEntity> implements SysUserMapper {

    @Resource
    private SysUserMapperService sysUserMapperService;

    @Override
    public SysUser findByUserName(String userName) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                )
                .eq(SysUserDBEntity::getUserName, userName);
        SysUserInfoDBJoinDTO sysUserInfoDBJoinDTO = sysUserMapperService.selectJoinOne(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTO);
    }

    @Override
    public List<SysUser> findByFuzzy(SysUserFindByPageParam param) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );

        if (param.getRoleId() != null) {
            wrapper.innerJoin(SysUserRoleDBEntity.class,
                        fn -> fn.eq(SysUserRoleDBEntity::getUserId, SysUserDBEntity::getUserId)
                                .eq(SysUserRoleDBEntity::getRoleId, param.getRoleId())
                    );
        }
        if(StringUtils.isNotBlank(param.getDisableState())) {
            wrapper.eq(SysUserDBEntity::getDisableState, param.getDisableState());
        }
        if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isBlank(param.getUserDescription())) {
            wrapper.like(SysUserDBEntity::getUserName, param.getUserName());
        } else if (StringUtils.isBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            wrapper.like(SysUserDBEntity::getUserDescription, param.getUserDescription());
        } else if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            if("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .or()
                        .like(SysUserDBEntity::getUserDescription, param.getUserDescription()));
            } else {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .like(SysUserDBEntity::getUserDescription, param.getUserDescription()));
            }
        }

        List<SysUserInfoDBJoinDTO> sysUserInfoDBJoinDTOS = sysUserMapperService.selectJoinList(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTOS);
    }

    public List<SysUser> findByFuzzyAndDept(SysUserFindByPageParam param, List<Long> deptIdList) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );

        if (param.getRoleId() != null) {
            wrapper.innerJoin(SysUserRoleDBEntity.class,
                    fn -> fn.eq(SysUserRoleDBEntity::getUserId, SysUserDBEntity::getUserId)
                            .eq(SysUserRoleDBEntity::getRoleId, param.getRoleId())
            );
        }

        if(deptIdList != null && deptIdList.size() > 0) {
            wrapper.innerJoin(SysDeptPersonPO.class, SysDeptPersonPO::getPersonId, SysUserDBEntity::getUserRelationId);
            wrapper.in(SysDeptPersonPO::getDeptId, deptIdList);
        }
        if(StringUtils.isNotBlank(param.getDisableState())) {
            wrapper.eq(SysUserDBEntity::getDisableState, param.getDisableState());
        }
        if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isBlank(param.getUserDescription())) {
            wrapper.like(SysUserDBEntity::getUserName, param.getUserName());
        } else if (StringUtils.isBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            wrapper.like(SysUserDBEntity::getUserDescription, param.getUserDescription());
        } else if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            if("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .or()
                        .like(SysUserDBEntity::getUserDescription, param.getUserDescription()));
            } else {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .like(SysUserDBEntity::getUserDescription, param.getUserDescription()));
            }
        }
        List<SysUserInfoDBJoinDTO> sysUserInfoDBJoinDTOS = sysUserMapperService.selectJoinList(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTOS);
    }

    @Override
    public int insert(SysUser user) {
        SysUserDBEntity entity = convertToDB(user);
        return sysUserMapperService.insert(entity);
    }

    @Override
    public int update(SysUser user) {
        SysUserDBEntity entity = convertToDB(user);
        LambdaUpdateWrapper<SysUserDBEntity> wrapper = new LambdaUpdateWrapper<SysUserDBEntity>()
                .eq(SysUserDBEntity::getUserId, user.getUserId())
                .set(SysUserDBEntity::getUserName, entity.getUserName())
                .set(SysUserDBEntity::getUserDescription, entity.getUserDescription())
                .set(StringUtils.isNotBlank(entity.getPassword()), SysUserDBEntity::getPassword, entity.getPassword())
                .set(StringUtils.isNotBlank(entity.getPassword()), SysUserDBEntity::getPasswordUpdateDate, entity.getPasswordUpdateDate())
                .set(SysUserDBEntity::getUserType, entity.getUserType())
                .set(SysUserDBEntity::getUserRelationId, entity.getUserRelationId())
                .set(SysUserDBEntity::getLastUpdateBy, entity.getLastUpdateBy())
                .set(SysUserDBEntity::getLastUpdateDate, entity.getLastUpdateDate())
                .set(SysUserDBEntity::getLastUpdateLogin, entity.getLastUpdateLogin())
                .set(SysUserDBEntity::getDisableState, entity.getDisableState())
                .set(SysUserDBEntity::getPasswordExpireDate, entity.getPasswordExpireDate())
                .set(SysUserDBEntity::getNextLoginUpdatePassword, entity.getNextLoginUpdatePassword());

        return sysUserMapperService.update(wrapper);
    }

    @Override
    public int delete(List<Long> userIds) {
        return sysUserMapperService.deleteBatchIds(userIds);
    }

    @Override
    public SysUser findById(long userId) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                )
                .eq(SysUserDBEntity::getUserId, userId);
        SysUserInfoDBJoinDTO sysUserInfoDBJoinDTO = sysUserMapperService.selectJoinOne(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTO);
    }

    @Override
    public int updateLastLoginInfoId(long loginId, long userId) {
        LambdaUpdateWrapper<SysUserDBEntity> wrapper = new LambdaUpdateWrapper<SysUserDBEntity>()
                .eq(SysUserDBEntity::getUserId, userId)
                .set(SysUserDBEntity::getLastLoginInfoId, loginId);

        return sysUserMapperService.update(wrapper);
    }

    @Override
    public int updateLoginError(SysUser errorUser) {
        LambdaUpdateWrapper<SysUserDBEntity> wrapper = new LambdaUpdateWrapper<SysUserDBEntity>()
                .eq(SysUserDBEntity::getUserId, errorUser.getUserId())
                .set(SysUserDBEntity::getLoginErrorCount, errorUser.getLoginErrorCount())
                .set(SysUserDBEntity::getDisableDeadlineDate, errorUser.getDisableDeadlineDate())
                .set(SysUserDBEntity::getDisableState, errorUser.getDisableState());

        return sysUserMapperService.update(wrapper);
    }

    @Override
    public List<SysUser> findByUserIds(List<Long> userIds) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );
        if(userIds != null && userIds.size() > 0) {
            wrapper.in(SysUserDBEntity::getUserId, userIds);
        }
        List<SysUserInfoDBJoinDTO> sysUserInfoDBJoinDTOS = sysUserMapperService.selectJoinList(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTOS);
    }

    @Override
    public List<SysUserDTO> findByUserIdListWithFullName(List<Long> userIds) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class, SysOrgPersonTlDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .eq(SysUserDBEntity::getDisableState, "N")
                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                .in(SysUserDBEntity::getUserId, userIds)
                .orderByDesc(SysUserDBEntity::getCreateDate);
        return sysUserMapperService.selectJoinList(SysUserDTO.class, wrapper);
    }

    @Override
    public int updatePassword(SysUser user) {
        LambdaUpdateWrapper<SysUserDBEntity> wrapper = new LambdaUpdateWrapper<SysUserDBEntity>()
                .eq(SysUserDBEntity::getUserId, user.getUserId())
                .set(SysUserDBEntity::getPassword, user.getPassword())
                .set(SysUserDBEntity::getPasswordUpdateDate, user.getPasswordUpdateDate())
                .set(SysUserDBEntity::getPasswordExpireDate, user.getPasswordExpireDate())
                .set(SysUserDBEntity::getNextLoginUpdatePassword, user.getNextLoginUpdatePassword());

        return sysUserMapperService.update(wrapper);
    }

    @Override
    public List<SysUser> findByRelationIds(List<Long> relationIdList) {
        LambdaQueryWrapper<SysUserDBEntity> wrapper = new LambdaQueryWrapper<SysUserDBEntity>()
                .in(SysUserDBEntity::getUserRelationId, relationIdList);

        List<SysUserDBEntity> entities = sysUserMapperService.selectList(wrapper);
        return convertFromDB(entities);
    }

    @Override
    public List<SysUser> findByUserNameList(List<String> userNameList) {
        LambdaQueryWrapper<SysUserDBEntity> wrapper = new LambdaQueryWrapper<SysUserDBEntity>();
        if (userNameList != null && userNameList.size() > 0) {
            wrapper.in(SysUserDBEntity::getUserName, userNameList);
        }
        List<SysUserDBEntity> entities = sysUserMapperService.selectList(wrapper);
        return convertFromDB(entities);
    }

    @Override
    public List<SysUser> findUsersByRoleCode(List<String> roleCode) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysRoleBDBEntity::getRoleCode)
                .innerJoin(SysUserRoleDBEntity.class, SysUserRoleDBEntity::getUserId, SysUserDBEntity::getUserId)
                .innerJoin(SysRoleBDBEntity.class, SysRoleBDBEntity::getRoleId, SysUserRoleDBEntity::getRoleId)
                .in(SysRoleBDBEntity::getRoleCode, roleCode)
                .eq(SysUserDBEntity::getDisableState, "N")
                .orderByDesc(SysUserDBEntity::getCreateDate);

        return sysUserMapperService.selectJoinList(SysUser.class, wrapper);
    }

    @Override
    public List<SysUser> findUsersByOrgCode(String orgCode, Long companyId) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(Dept::getOrgCode)
                .innerJoin(SysDeptPersonPO.class, SysDeptPersonPO::getPersonId, SysUserDBEntity::getUserRelationId)
                .innerJoin(Dept.class, Dept::getId, SysDeptPersonPO::getDeptId)
                .eq(Dept::getOrgCode, orgCode)
                .eq(SysUserDBEntity::getDisableState, "N")
                .eq(SysDeptPersonPO::getCompanyId, companyId)
                .orderByDesc(SysUserDBEntity::getCreateDate);

        return sysUserMapperService.selectJoinList(SysUser.class, wrapper);
    }

    @Override
    public List<SysUserDTO> findUserByRoleCodeAndCompanyId(String roleCode, Long companyId) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysRoleBDBEntity::getRoleCode)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .select(SysDeptPersonPO::getCompanyId)
                .select(SysDeptPersonPO::getDeptId)
                .innerJoin(SysUserRoleDBEntity.class, SysUserRoleDBEntity::getUserId, SysUserDBEntity::getUserId)
                .innerJoin(SysRoleBDBEntity.class, SysRoleBDBEntity::getRoleId, SysUserRoleDBEntity::getRoleId)
                .innerJoin(SysDeptPersonPO.class, SysDeptPersonPO::getPersonId, SysUserDBEntity::getUserRelationId)
                .innerJoin(SysOrgPersonTlDBEntity.class, SysOrgPersonTlDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .eq(SysRoleBDBEntity::getRoleCode, roleCode)
                .eq(SysDeptPersonPO::getCompanyId, companyId)
                .eq(SysUserDBEntity::getDisableState, "N")
                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                .groupBy(SysUserDBEntity::getUserName)
                .orderByDesc(SysUserDBEntity::getCreateDate);

        return sysUserMapperService.selectJoinList(SysUserDTO.class,wrapper);
    }

    @Override
    public List<SysUserDTO> findUserByRoleCode(String roleCode, Long companyId, Long deptId) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysRoleBDBEntity::getRoleCode)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .select(SysDeptPersonPO::getCompanyId)
                .select(SysDeptPersonPO::getDeptId)
                .innerJoin(SysUserRoleDBEntity.class, SysUserRoleDBEntity::getUserId, SysUserDBEntity::getUserId)
                .innerJoin(SysRoleBDBEntity.class, SysRoleBDBEntity::getRoleId, SysUserRoleDBEntity::getRoleId)
                .innerJoin(SysDeptPersonPO.class, SysDeptPersonPO::getPersonId, SysUserDBEntity::getUserRelationId)
                .innerJoin(SysOrgPersonTlDBEntity.class, SysOrgPersonTlDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .eq(SysRoleBDBEntity::getRoleCode, roleCode)
                .eq(SysDeptPersonPO::getCompanyId, companyId)
                .eq(SysDeptPersonPO::getDeptId, deptId)
                .eq(SysUserDBEntity::getDisableState, "N")
                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                .groupBy(SysUserDBEntity::getUserName)
                .orderByDesc(SysUserDBEntity::getCreateDate);

        return sysUserMapperService.selectJoinList(SysUserDTO.class, wrapper);
    }

    @Override
    public List<SysUserDTO> findUsersByfullName(String fullName, Long companyId) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysDeptPersonPO::getCompanyId)
                .select(SysDeptPersonPO::getDeptId)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .innerJoin(SysOrgPersonTlDBEntity.class, SysOrgPersonTlDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .innerJoin(SysDeptPersonPO.class, SysDeptPersonPO::getPersonId, SysUserDBEntity::getUserRelationId)
                .eq(SysUserDBEntity::getDisableState, "N")
                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                .eq(SysOrgPersonTlDBEntity::getFullName, fullName)
                .eq(SysDeptPersonPO::getCompanyId, companyId)
                .orderByDesc(SysUserDBEntity::getCreateDate);

        return sysUserMapperService.selectJoinList(SysUserDTO.class, wrapper);
    }

    @Override
    public boolean insertAll(List<SysUser> list) {
        List<SysUserDBEntity> entities = convertToDB(list);
        return super.saveBatch(entities);
    }

    @Override
    public List<SysUser> findByUserIdsAndFuzzy(List<Long> userIdList, String userName, String fullName) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );
        if(userIdList != null && userIdList.size() > 0) {
            wrapper.in(SysUserDBEntity::getUserId, userIdList);
        }
        if (StringUtils.isNotBlank(userName)) {
            wrapper.like(SysUserDBEntity::getUserName, userName);
        }
        if (StringUtils.isNotBlank(fullName)) {
            wrapper.like(SysUserDBEntity::getUserDescription, fullName);
        }

        List<SysUserInfoDBJoinDTO> sysUserInfoDBJoinDTOS = sysUserMapperService.selectJoinList(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTOS);
    }

    @Override
    public List<SysOnlineUserVO> findByUserNamesAndFuzzy(List<String> userNameList, SysOnlineUserParamDTO search) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .select(SysUserDBEntity::getUserId)
                .select(SysUserDBEntity::getUserName)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );
        if(userNameList != null && userNameList.size() > 0) {
            wrapper.in(SysUserDBEntity::getUserName, userNameList);
        }
        if (StringUtils.isNotBlank(search.getUserName())) {
            wrapper.like(SysUserDBEntity::getUserName, search.getUserName());
        }
        if (StringUtils.isNotBlank(search.getFullName())) {
            wrapper.like(SysOrgPersonTlDBEntity::getFullName, search.getFullName());
        }

        return sysUserMapperService.selectJoinList(SysOnlineUserVO.class, wrapper);
    }
    public List<SysUser> findByPersonParam(SysOrgPersonFindByPageParam param) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = JoinWrappers.lambda(SysUserDBEntity.class)
                .selectAll(SysUserDBEntity.class)
                .innerJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .innerJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );

        if (param.getStatus() != null) {
            wrapper.eq(SysOrgPersonBDBEntity::getStatus, param.getStatus());
        }

        if (!CollectionUtils.isEmpty(param.getPersonTypeList())) {
            wrapper.in(SysOrgPersonBDBEntity::getPersonType, param.getPersonTypeList());
        }

        if (org.springframework.util.StringUtils.hasText(param.getFullName()) && org.springframework.util.StringUtils.hasText(param.getJobNo())) {
            if("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName())
                        .or()
                        .like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo()));
            }else {
                wrapper.and(w -> w.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName())
                        .like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo()));
            }

        } else {
            if (org.springframework.util.StringUtils.hasText(param.getFullName())) {
                wrapper.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName());
            }
            if (org.springframework.util.StringUtils.hasText(param.getJobNo())) {
                wrapper.like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo());
            }
        }
        wrapper.orderByDesc(SysUserDBEntity::getUserId);
        return sysUserMapperService.selectJoinList(SysUser.class, wrapper);
    }

    private SysUserDBEntity convertToDB(SysUser data) {
        SysUserDBEntity entity = BeanUtils.toBean(data, SysUserDBEntity.class);
        return entity;
    }

    private SysUser convertFromDB(SysUserDBEntity entity) {
        SysUser data = BeanUtils.toBean(entity, SysUser.class);
        return data;
    }

    private List<SysUserDBEntity> convertToDB(List<SysUser> dataList) {
        List<SysUserDBEntity> entityList = BeanUtils.toBean(dataList, SysUserDBEntity.class);
        return entityList;
    }

    private List<SysUser> convertFromDB(List<SysUserDBEntity> entityList) {
        List<SysUser> dataList = BeanUtils.toBean(entityList, SysUser.class);
        return dataList;
    }

    private SysUser convertFromJoinDB(SysUserInfoDBJoinDTO entity) {
        SysUser data = BeanUtils.toBean(entity, SysUser.class);
        if(StringUtils.isNotBlank(entity.getFullName())) {
            data.setUserDescription(entity.getFullName());
        }
        return data;
    }

    private List<SysUser> convertFromJoinDB(List<SysUserInfoDBJoinDTO> entityList) {
        List<SysUser> dataList = new ArrayList<>();
        entityList.forEach(entity -> {
            SysUser data = BeanUtils.toBean(entity, SysUser.class);
            if(StringUtils.isNotBlank(entity.getFullName())) {
                data.setUserDescription(entity.getFullName());
            }
            dataList.add(data);
        });
        return dataList;
    }
}
package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.MyBatisUtil;
import com.crrc.sys.common.SortContextHolder;
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
 * 系统用户表-mapper实现类，通过MyBatisPlus方案重写SQL
 *
 * @author 李江涛
 * @date 2025-11-20
 */
@Repository
public class SysUserMapperImpl extends ServiceImpl<SysUserMapperService, SysUserDBEntity> {

    @Resource
    private SysUserMapperService sysUserMapperService;

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
        if (StringUtils.isNotBlank(param.getDisableState())) {
            wrapper.eq(SysUserDBEntity::getDisableState, param.getDisableState());
        }
        if(StringUtils.isNotBlank(param.getUserType())) {
            wrapper.eq(SysUserDBEntity::getUserType, param.getUserType());
        }
        if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isBlank(param.getUserDescription())) {
            wrapper.like(SysUserDBEntity::getUserName, param.getUserName());
        } else if (StringUtils.isBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            wrapper.and(w -> w.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                    .or()
                    .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
            );
        } else if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            if ("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .or(
                                w1 -> w1.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                                        .or()
                                        .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
                        )
                );
            } else {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .and(w1 -> w1.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                                .or()
                                .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
                        )
                );
            }
        }

        List<SysUserInfoDBJoinDTO> sysUserInfoDBJoinDTOS = sysUserMapperService.selectJoinList(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTOS);
    }

    public IPage<SysUser> findByFuzzyPage(SysUserFindByPageParam param) {
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
        if (StringUtils.isNotBlank(param.getDisableState())) {
            wrapper.eq(SysUserDBEntity::getDisableState, param.getDisableState());
        }
        if(StringUtils.isNotBlank(param.getUserType())) {
            wrapper.eq(SysUserDBEntity::getUserType, param.getUserType());
        }
        if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isBlank(param.getUserDescription())) {
            wrapper.like(SysUserDBEntity::getUserName, param.getUserName());
        } else if (StringUtils.isBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            wrapper.and(w -> w.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                    .or()
                    .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
            );
        } else if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            if ("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .or(
                                w1 -> w1.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                                        .or()
                                        .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
                        )
                );
            } else {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .and(w1 -> w1.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                                .or()
                                .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
                        )
                );
            }
        }

        String sortField = SortContextHolder.getSortField();
        boolean isAsc = SortContextHolder.isAsc();
        wrapper.orderBy(StringUtils.isNotBlank(sortField), isAsc, sortField);

        Page<SysUserInfoDBJoinDTO> midPage = sysUserMapperService.selectJoinPage(
                MyBatisUtil.makePage(param.getPage(), param.getPageSize()), SysUserInfoDBJoinDTO.class, wrapper);
        IPage<SysUser> resultPage = new Page<>(midPage.getCurrent(), midPage.getSize(), midPage.getTotal());
        resultPage.setRecords(convertFromJoinDB(midPage.getRecords()));
        return resultPage;
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

        if (deptIdList != null && deptIdList.size() > 0) {
            wrapper.innerJoin(SysDeptPersonPO.class, SysDeptPersonPO::getPersonId, SysUserDBEntity::getUserRelationId);
            wrapper.in(SysDeptPersonPO::getDeptId, deptIdList);
        }
        if (StringUtils.isNotBlank(param.getDisableState())) {
            wrapper.eq(SysUserDBEntity::getDisableState, param.getDisableState());
        }
        if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isBlank(param.getUserDescription())) {
            wrapper.like(SysUserDBEntity::getUserName, param.getUserName());
        } else if (StringUtils.isBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            wrapper.and(w -> w.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                    .or()
                    .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
            );
        } else if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            if ("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .or(
                                w1 -> w1.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                                        .or()
                                        .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
                        )
                );
            } else {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .and(w1 -> w1.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                                .or()
                                .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
                        )
                );
            }
        }
        List<SysUserInfoDBJoinDTO> sysUserInfoDBJoinDTOS = sysUserMapperService.selectJoinList(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTOS);
    }

    public IPage<SysUser> findByFuzzyAndDeptPage(SysUserFindByPageParam param, List<Long> deptIdList) {
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

        if (deptIdList != null && deptIdList.size() > 0) {
            wrapper.innerJoin(SysDeptPersonPO.class, SysDeptPersonPO::getPersonId, SysUserDBEntity::getUserRelationId);
            wrapper.in(SysDeptPersonPO::getDeptId, deptIdList);
        }
        if (StringUtils.isNotBlank(param.getDisableState())) {
            wrapper.eq(SysUserDBEntity::getDisableState, param.getDisableState());
        }
        if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isBlank(param.getUserDescription())) {
            wrapper.like(SysUserDBEntity::getUserName, param.getUserName());
        } else if (StringUtils.isBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            wrapper.and(w -> w.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                    .or()
                    .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
            );
        } else if (StringUtils.isNotBlank(param.getUserName()) && StringUtils.isNotBlank(param.getUserDescription())) {
            if ("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .or(
                                w1 -> w1.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                                        .or()
                                        .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
                        )
                );
            } else {
                wrapper.and(w -> w.like(SysUserDBEntity::getUserName, param.getUserName())
                        .and(w1 -> w1.like(SysUserDBEntity::getUserDescription, param.getUserDescription())
                                .or()
                                .like(SysOrgPersonTlDBEntity::getFullName, param.getUserDescription())
                        )
                );
            }
        }
        Page<SysUserInfoDBJoinDTO> midPage = sysUserMapperService.selectJoinPage(
                MyBatisUtil.makePage(param.getPage(), param.getPageSize()), SysUserInfoDBJoinDTO.class, wrapper);
        IPage<SysUser> resultPage = new Page<>(midPage.getCurrent(), midPage.getSize(), midPage.getTotal());
        resultPage.setRecords(convertFromJoinDB(midPage.getRecords()));
        return resultPage;
    }

    public int insert(SysUser user) {
        SysUserDBEntity entity = convertToDB(user);
        return sysUserMapperService.insert(entity);
    }


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


    public int delete(List<Long> userIds) {
        return sysUserMapperService.deleteBatchIds(userIds);
    }


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


    public int updateLastLoginInfoId(long loginId, long userId) {
        LambdaUpdateWrapper<SysUserDBEntity> wrapper = new LambdaUpdateWrapper<SysUserDBEntity>()
                .eq(SysUserDBEntity::getUserId, userId)
                .set(SysUserDBEntity::getLastLoginInfoId, loginId);

        return sysUserMapperService.update(wrapper);
    }


    public int updateLoginError(SysUser errorUser) {
        LambdaUpdateWrapper<SysUserDBEntity> wrapper = new LambdaUpdateWrapper<SysUserDBEntity>()
                .eq(SysUserDBEntity::getUserId, errorUser.getUserId())
                .set(SysUserDBEntity::getLoginErrorCount, errorUser.getLoginErrorCount())
                .set(SysUserDBEntity::getDisableDeadlineDate, errorUser.getDisableDeadlineDate())
                .set(SysUserDBEntity::getDisableState, errorUser.getDisableState());

        return sysUserMapperService.update(wrapper);
    }


    public List<SysUser> findByUserIds(List<Long> userIds) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );
        if (userIds != null && userIds.size() > 0) {
            wrapper.in(SysUserDBEntity::getUserId, userIds);
        }
        List<SysUserInfoDBJoinDTO> sysUserInfoDBJoinDTOS = sysUserMapperService.selectJoinList(SysUserInfoDBJoinDTO.class, wrapper);
        return convertFromJoinDB(sysUserInfoDBJoinDTOS);
    }


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


    public int updatePassword(SysUser user) {
        LambdaUpdateWrapper<SysUserDBEntity> wrapper = new LambdaUpdateWrapper<SysUserDBEntity>()
                .eq(SysUserDBEntity::getUserId, user.getUserId())
                .set(SysUserDBEntity::getPassword, user.getPassword())
                .set(SysUserDBEntity::getPasswordUpdateDate, user.getPasswordUpdateDate())
                .set(SysUserDBEntity::getPasswordExpireDate, user.getPasswordExpireDate())
                .set(SysUserDBEntity::getNextLoginUpdatePassword, user.getNextLoginUpdatePassword());

        return sysUserMapperService.update(wrapper);
    }


    public List<SysUser> findByRelationIds(List<Long> relationIdList) {
        LambdaQueryWrapper<SysUserDBEntity> wrapper = new LambdaQueryWrapper<SysUserDBEntity>()
                .in(SysUserDBEntity::getUserRelationId, relationIdList);

        List<SysUserDBEntity> entities = sysUserMapperService.selectList(wrapper);
        return convertFromDB(entities);
    }


    public List<SysUser> findByUserNameList(List<String> userNameList) {
        LambdaQueryWrapper<SysUserDBEntity> wrapper = new LambdaQueryWrapper<SysUserDBEntity>();
        if (userNameList != null && userNameList.size() > 0) {
            wrapper.in(SysUserDBEntity::getUserName, userNameList);
        }
        List<SysUserDBEntity> entities = sysUserMapperService.selectList(wrapper);
        return convertFromDB(entities);
    }

    //
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

        return sysUserMapperService.selectJoinList(SysUserDTO.class, wrapper);
    }


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
                .in(SysRoleBDBEntity::getRoleCode, roleCode.split(","))  // 修改为in查询，支持多个角色编码
                .eq(SysDeptPersonPO::getCompanyId, companyId)
                .eq(SysDeptPersonPO::getDeptId, deptId)
                .eq(SysUserDBEntity::getDisableState, "N")
                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                .groupBy(SysUserDBEntity::getUserName)
                .orderByDesc(SysUserDBEntity::getCreateDate);

        return sysUserMapperService.selectJoinList(SysUserDTO.class, wrapper);
    }


    /**
     * 根据公司/部门/角色（均可多个）及模糊关键字查询启用用户（服务端分页）
     * 公司、部门、角色之间为AND关系；用户名、用户描述、工号各自独立模糊（分别传入，传哪个查哪个）
     * 公司/部门/角色/工号均按需动态join，无对应条件时不关联相关表；人员表(SysOrgPersonB)总是关联以返回 jobNo
     *
     * @param page          分页对象
     * @param companyIdList 公司ID集合（sys_dept_person.company_id，即Company.id）
     * @param deptIdList    部门ID集合（sys_dept_person.dept_id，即Dept.id）
     * @param roleCodes     角色编码集合
     * @param userName      用户名模糊关键字
     * @param userDescription 用户描述模糊关键字
     * @param jobNo         工号模糊关键字
     * @return 分页用户信息
     */
    public IPage<SysUserDTO> findUsersByMultiCondition(Page<SysUserDTO> page,
                                                       @Param("companyIdList") List<Long> companyIdList,
                                                       @Param("deptIdList") List<Long> deptIdList,
                                                       @Param("roleCodes") List<String> roleCodes,
                                                       @Param("userName") String userName,
                                                       @Param("userDescription") String userDescription,
                                                       @Param("jobNo") String jobNo) {
        boolean needDeptPerson = !CollectionUtils.isEmpty(companyIdList) || !CollectionUtils.isEmpty(deptIdList);
        boolean needRole = !CollectionUtils.isEmpty(roleCodes);
        boolean needJobNo = StringUtils.isNotBlank(jobNo);
        boolean needUserName = StringUtils.isNotBlank(userName);
        boolean needUserDesc = StringUtils.isNotBlank(userDescription);

        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName,SysOrgPersonTlDBEntity::getPersonId);
        // 取fullName（姓名）
        wrapper.innerJoin(SysOrgPersonTlDBEntity.class,
                fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                        .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN"));
        // 总是关联人员表，以便返回 jobNo 字段（前端用户表固定展示 jobNo 列）
        wrapper.select(SysOrgPersonBDBEntity::getJobNo);
        wrapper.innerJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId);
        // 公司/部门过滤：关联部门人员表
        if (needDeptPerson) {
            wrapper.select(SysDeptPersonPO::getCompanyId)
                    .select(SysDeptPersonPO::getDeptId);
            wrapper.innerJoin(SysDeptPersonPO.class, SysDeptPersonPO::getPersonId, SysUserDBEntity::getUserRelationId);
            if (!CollectionUtils.isEmpty(companyIdList)) {
                wrapper.in(SysDeptPersonPO::getCompanyId, companyIdList);
            }
            if (!CollectionUtils.isEmpty(deptIdList)) {
                wrapper.in(SysDeptPersonPO::getDeptId, deptIdList);
            }
        }
        // 角色过滤：关联用户角色表+角色表
        if (needRole) {
            wrapper.select(SysRoleBDBEntity::getRoleCode);
            wrapper.innerJoin(SysUserRoleDBEntity.class, SysUserRoleDBEntity::getUserId, SysUserDBEntity::getUserId)
                    .innerJoin(SysRoleBDBEntity.class, SysRoleBDBEntity::getRoleId, SysUserRoleDBEntity::getRoleId)
                    .in(SysRoleBDBEntity::getRoleCode, roleCodes);
        }
        // 启用用户
        wrapper.eq(SysUserDBEntity::getDisableState, "N");
        // 用户名模糊
        if (needUserName) {
            wrapper.like(SysUserDBEntity::getUserName, userName.trim());
        }
        // 用户描述模糊
        if (needUserDesc) {
            wrapper.like(SysOrgPersonTlDBEntity::getFullName, userDescription.trim());
        }
        // 工号模糊
        if (needJobNo) {
            wrapper.like(SysOrgPersonBDBEntity::getJobNo, jobNo.trim());
        }
        // 按用户ID去重，避免角色/部门一对多导致重复
        wrapper.groupBy(SysUserDBEntity::getUserId)
                .orderByDesc(SysUserDBEntity::getCreateDate);

        return sysUserMapperService.selectJoinPage(page, SysUserDTO.class, wrapper);
    }


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


    public boolean insertAll(List<SysUser> list) {
        List<SysUserDBEntity> entities = convertToDB(list);
        return super.saveBatch(entities);
    }


    public List<SysUser> findByUserIdsAndFuzzy(List<Long> userIdList, String userName, String fullName) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .selectAll(SysUserDBEntity.class)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonBDBEntity.class, SysOrgPersonBDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysOrgPersonBDBEntity::getPersonId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );
        if (userIdList != null && userIdList.size() > 0) {
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


    public List<SysOnlineUserVO> findByUserNamesAndFuzzy(List<String> userNameList, SysOnlineUserParamDTO search) {
        MPJLambdaWrapper<SysUserDBEntity> wrapper = new MPJLambdaWrapper<SysUserDBEntity>()
                .select(SysUserDBEntity::getUserId)
                .select(SysUserDBEntity::getUserName)
                .select(SysOrgPersonTlDBEntity::getFullName)
                .leftJoin(SysOrgPersonTlDBEntity.class,
                        fn -> fn.eq(SysOrgPersonTlDBEntity::getPersonId, SysUserDBEntity::getUserRelationId)
                                .eq(SysOrgPersonTlDBEntity::getLanguage, "zh-CN")
                );
        if (userNameList != null && userNameList.size() > 0) {
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
            if ("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName())
                        .or()
                        .like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo()));
            } else {
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

    public IPage<SysUser> findByPersonParamPage(SysOrgPersonFindByPageParam param) {
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
            if ("or".equals(param.getMontage())) {
                wrapper.and(w -> w.like(SysOrgPersonTlDBEntity::getFullName, param.getFullName())
                        .or()
                        .like(SysOrgPersonBDBEntity::getJobNo, param.getJobNo()));
            } else {
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
        Page<SysUser> page = sysUserMapperService.selectJoinPage(
                MyBatisUtil.makePage(param.getPage(), param.getPageSize()), SysUser.class, wrapper);
        return page;
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
        if (data != null && StringUtils.isNotBlank(entity.getFullName())) {
            data.setUserDescription(entity.getFullName());
        }
        return data;
    }

    private List<SysUser> convertFromJoinDB(List<SysUserInfoDBJoinDTO> entityList) {
        List<SysUser> dataList = new ArrayList<>();
        entityList.forEach(entity -> {
            SysUser data = BeanUtils.toBean(entity, SysUser.class);
            if (data != null && StringUtils.isNotBlank(entity.getFullName())) {
                data.setUserDescription(entity.getFullName());
            }
            dataList.add(data);
        });
        return dataList;
    }
}

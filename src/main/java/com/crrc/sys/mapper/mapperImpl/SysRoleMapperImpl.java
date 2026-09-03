package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.MyBatisUtil;
import com.crrc.sys.common.SortContextHolder;
import com.crrc.sys.entity.profile.SysProfileBDBEntity;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.entity.role.*;
import com.crrc.sys.entity.user.SysUserRoleDBEntity;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.mapper.SysRoleMapper;
import com.crrc.sys.mapper.mapperService.SysRoleBMapperService;
import com.crrc.sys.mapper.mapperService.SysRoleTlMapperService;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统角色多语言表-mapper实现类，通过MyBatisPlus方案重写SQL
 *
 * @author 李江涛
 * @date 2025-11-20
 */
@Repository
public class SysRoleMapperImpl extends ServiceImpl<SysRoleTlMapperService, SysRoleTlDBEntity> {

    @Resource
    private SysRoleTlMapperService sysRoleTlMapperService;

    @Resource
    private SysRoleBMapperService sysRoleBMapperService;

    @Resource
    private SysRoleMapper sysRoleMapper;


    public List<SysRole> findByFuzzy(SysRoleFindByPageParam param) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = JoinWrappers.lambda(SysRoleBDBEntity.class)
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class, SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .eq(SysRoleTlDBEntity::getLanguage, param.getLanguage());

        if (param.getMenuId() != null) {
            wrapper.innerJoin(
                    SysRoleMenuDBEntity.class,
                    fn -> {
                        fn.setAlias("rm")
                                .select(SysRoleMenuDBEntity::getRoleId)
                                .eq(SysRoleMenuDBEntity::getMenuId, param.getMenuId())
                                .groupBy(SysRoleMenuDBEntity::getMenuId);
                    },
                    SysRoleMenuDBEntity::getRoleId, SysRoleBDBEntity::getRoleId
            );
        }

        if (param.getEnableFlag() != null && !param.getEnableFlag().isEmpty()) {
            wrapper.eq(SysRoleBDBEntity::getEnableFlag, param.getEnableFlag());
        }

        if (param.getRoleCode() != null && !param.getRoleCode().isEmpty()) {
            wrapper.like(SysRoleBDBEntity::getRoleCode, param.getRoleCode());
        }

        if (param.getRoleName() != null && !param.getRoleName().isEmpty()) {
            wrapper.like(SysRoleTlDBEntity::getRoleName, param.getRoleName());
        }

        if (param.getRoleDescription() != null && !param.getRoleDescription().isEmpty()) {
            wrapper.like(SysRoleTlDBEntity::getRoleDescription, param.getRoleDescription());
        }

        if (param.getProjectId() != null) {
            wrapper.eq(SysRoleBDBEntity::getProjectId, param.getProjectId());
        }

        if (param.getDefaultFlag() != null && !param.getDefaultFlag().isEmpty()) {
            wrapper.eq(SysRoleBDBEntity::getDefaultFlag, param.getDefaultFlag());
        }

        wrapper.orderByAsc(SysRoleTlDBEntity::getRoleName);

        return sysRoleBMapperService.selectJoinList(SysRole.class, wrapper);
    }

    public IPage<SysRole> findByFuzzyPage(SysRoleFindByPageParam param, Set<Long> allowedProjectIds) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = JoinWrappers.lambda(SysRoleBDBEntity.class)
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class, SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .eq(SysRoleTlDBEntity::getLanguage, param.getLanguage());

        if (param.getMenuId() != null) {
            wrapper.innerJoin(
                    SysRoleMenuDBEntity.class,
                    fn -> {
                        fn.setAlias("rm")
                                .select(SysRoleMenuDBEntity::getRoleId)
                                .eq(SysRoleMenuDBEntity::getMenuId, param.getMenuId())
                                .groupBy(SysRoleMenuDBEntity::getMenuId);
                    },
                    SysRoleMenuDBEntity::getRoleId, SysRoleBDBEntity::getRoleId
            );
        }

        if (param.getEnableFlag() != null && !param.getEnableFlag().isEmpty()) {
            wrapper.eq(SysRoleBDBEntity::getEnableFlag, param.getEnableFlag());
        }

        if (param.getRoleCode() != null && !param.getRoleCode().isEmpty()) {
            wrapper.like(SysRoleBDBEntity::getRoleCode, param.getRoleCode());
        }

        if (param.getRoleName() != null && !param.getRoleName().isEmpty()) {
            wrapper.like(SysRoleTlDBEntity::getRoleName, param.getRoleName());
        }

        if (param.getRoleDescription() != null && !param.getRoleDescription().isEmpty()) {
            wrapper.like(SysRoleTlDBEntity::getRoleDescription, param.getRoleDescription());
        }

        if (param.getProjectId() != null) {
            wrapper.eq(SysRoleBDBEntity::getProjectId, param.getProjectId());
        }

        if(allowedProjectIds != null && !allowedProjectIds.isEmpty()) {
            wrapper.in(SysRoleBDBEntity::getProjectId, allowedProjectIds);
        }

        if (param.getDefaultFlag() != null && !param.getDefaultFlag().isEmpty()) {
            wrapper.eq(SysRoleBDBEntity::getDefaultFlag, param.getDefaultFlag());
        }

        String sortField = SortContextHolder.getSortField();
        boolean isAsc = SortContextHolder.isAsc();
        if(StringUtils.isNotBlank(sortField)) {
            wrapper.orderBy(true, isAsc, sortField);
        } else {
            wrapper.orderByAsc(SysRoleTlDBEntity::getRoleName);
        }

        return sysRoleBMapperService.selectJoinPage(MyBatisUtil.makePage(param.getPage(), param.getPageSize()), SysRole.class, wrapper);
    }


    public int addTl(List<SysRoleTl> roleTlList) {
        List<SysRoleTlDBEntity> entities = convertToDB(roleTlList);
        return super.saveBatch(entities) ? entities.size() : 0;
    }


    public int addB(SysRole role) {
        SysRoleBDBEntity entity = convertToDB(role);
        return sysRoleBMapperService.insert(entity);
    }


    public int updateTl(SysRole role) {
        LambdaUpdateWrapper<SysRoleTlDBEntity> wrapper = new LambdaUpdateWrapper<SysRoleTlDBEntity>()
                .eq(SysRoleTlDBEntity::getRoleId, role.getRoleId())
                .eq(SysRoleTlDBEntity::getLanguage, role.getLanguage())
                .set(SysRoleTlDBEntity::getRoleName, role.getRoleName())
                .set(SysRoleTlDBEntity::getRoleDescription, role.getRoleDescription())
                .set(SysRoleTlDBEntity::getLastUpdateBy, role.getLastUpdateBy())
                .set(SysRoleTlDBEntity::getLastUpdateDate, role.getLastUpdateDate())
                .set(SysRoleTlDBEntity::getLastUpdateLogin, role.getLastUpdateLogin());

        return sysRoleTlMapperService.update(wrapper);
    }


    public int updateB(SysRole role) {
        LambdaUpdateWrapper<SysRoleBDBEntity> wrapper = new LambdaUpdateWrapper<SysRoleBDBEntity>()
                .eq(SysRoleBDBEntity::getRoleId, role.getRoleId())
                .set(SysRoleBDBEntity::getRoleCode, role.getRoleCode())
                .set(SysRoleBDBEntity::getMenuId, role.getMenuId())
                .set(SysRoleBDBEntity::getEnableFlag, role.getEnableFlag())
                .set(SysRoleBDBEntity::getProjectId, role.getProjectId())
                .set(SysRoleBDBEntity::getLastUpdateBy, role.getLastUpdateBy())
                .set(SysRoleBDBEntity::getLastUpdateDate, role.getLastUpdateDate())
                .set(SysRoleBDBEntity::getLastUpdateLogin, role.getLastUpdateLogin())
                .set(SysRoleBDBEntity::getDefaultFlag, role.getDefaultFlag());

        return sysRoleBMapperService.update(wrapper);
    }


    public int deleteTl(List<Long> ids) {
        LambdaQueryWrapper<SysRoleTlDBEntity> wrapper = new LambdaQueryWrapper<SysRoleTlDBEntity>()
                .in(SysRoleTlDBEntity::getRoleId, ids);

        return sysRoleTlMapperService.delete(wrapper);
    }


    public int deleteB(List<Long> ids) {
        LambdaQueryWrapper<SysRoleBDBEntity> wrapper = new LambdaQueryWrapper<SysRoleBDBEntity>()
                .in(SysRoleBDBEntity::getRoleId, ids);

        return sysRoleBMapperService.delete(wrapper);
    }


    public SysRole findById(long roleId, String language) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = new MPJLambdaWrapper<SysRoleBDBEntity>()
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class, SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .eq(SysRoleBDBEntity::getRoleId, roleId)
                .eq(SysRoleTlDBEntity::getLanguage, language);

        return sysRoleBMapperService.selectJoinOne(SysRole.class, wrapper);
    }


    public SysRole findByCode(String roleCode, String language) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = new MPJLambdaWrapper<SysRoleBDBEntity>()
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class, SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .eq(SysRoleBDBEntity::getRoleCode, roleCode)
                .eq(SysRoleTlDBEntity::getLanguage, language);

        return sysRoleBMapperService.selectJoinOne(SysRole.class, wrapper);
    }


    public List<SysRole> findByUserId(long userId, String language) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = new MPJLambdaWrapper<SysRoleBDBEntity>()
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class,
                        fn -> fn.eq(SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                                .eq(SysRoleTlDBEntity::getLanguage, language)
                )
                .innerJoin(SysUserRoleDBEntity.class, SysUserRoleDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .eq(SysUserRoleDBEntity::getUserId, userId);

        return sysRoleBMapperService.selectJoinList(SysRole.class, wrapper);
    }


    public SysRole findByName(String roleName, String language) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = new MPJLambdaWrapper<SysRoleBDBEntity>()
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class, SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .eq(SysRoleTlDBEntity::getRoleName, roleName)
                .eq(SysRoleTlDBEntity::getLanguage, language);

        return sysRoleBMapperService.selectJoinOne(SysRole.class, wrapper);
    }


    public List<SysRole> findByCodes(List<String> roleCodeList, String language) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = new MPJLambdaWrapper<SysRoleBDBEntity>()
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class, SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .in(SysRoleBDBEntity::getRoleCode, roleCodeList)
                .eq(SysRoleTlDBEntity::getLanguage, language);

        return sysRoleBMapperService.selectJoinList(SysRole.class, wrapper);
    }


    public List<Long> findRoleIdList() {
        LambdaQueryWrapper<SysRoleBDBEntity> wrapper = new LambdaQueryWrapper<SysRoleBDBEntity>()
                .select(SysRoleBDBEntity::getRoleId)
                .orderByAsc(SysRoleBDBEntity::getRoleId);

        return sysRoleBMapperService.selectList(wrapper)
                .stream()
                .map(SysRoleBDBEntity::getRoleId)
                .collect(Collectors.toList());
    }

    private List<SysRoleTlDBEntity> convertToDB(List<SysRoleTl> dataList) {
        return BeanUtils.toBean(dataList, SysRoleTlDBEntity.class);
    }

    private SysRoleBDBEntity convertToDB(SysRole data) {
        return BeanUtils.toBean(data, SysRoleBDBEntity.class);
    }

    public Long findDefaultRoleIdInProject(Long projectId) {
        LambdaQueryWrapper<SysRoleBDBEntity> wrapper = new LambdaQueryWrapper<SysRoleBDBEntity>()
                .select(SysRoleBDBEntity::getRoleId)
                .eq(SysRoleBDBEntity::getProjectId, projectId)
                .eq(SysRoleBDBEntity::getDefaultFlag, "Y");
        List<SysRoleBDBEntity> entities = sysRoleBMapperService.selectList(wrapper);
        return entities != null && entities.size() > 0 ? entities.get(0).getRoleId() : null;
    }

    public List<Long> findRoleIdsInProject(Long projectId) {
        LambdaQueryWrapper<SysRoleBDBEntity> wrapper = new LambdaQueryWrapper<SysRoleBDBEntity>()
                .select(SysRoleBDBEntity::getRoleId)
                .eq(SysRoleBDBEntity::getProjectId, projectId);
        List<SysRoleBDBEntity> entities = sysRoleBMapperService.selectList(wrapper);
        return entities.stream().map(SysRoleBDBEntity::getRoleId).collect(Collectors.toList());
    }

    /**
     * 根据角色ID集合查询角色信息(含多语言名称)
     *
     * @param roleIds  角色ID集合
     * @param language 语言
     * @return 角色集合
     */
    public List<SysRole> findByIds(List<Long> roleIds, String language) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = new MPJLambdaWrapper<SysRoleBDBEntity>()
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class, SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .in(SysRoleBDBEntity::getRoleId, roleIds)
                .eq(SysRoleTlDBEntity::getLanguage, language);
        return sysRoleBMapperService.selectJoinList(SysRole.class, wrapper);
    }

    public SysRole findDefaultRoleByProject(Long projectId, String projectName) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = new MPJLambdaWrapper<SysRoleBDBEntity>()
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription, SysRoleTlDBEntity::getLanguage)
                .innerJoin(SysRoleTlDBEntity.class, SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .leftJoin(ProjectEntity.class, ProjectEntity::getId, SysRoleBDBEntity::getProjectId)
                .eq(SysRoleTlDBEntity::getLanguage, LanguageEnum.ZH_CN.getValue())
                .eq(SysRoleBDBEntity::getDefaultFlag, "Y");
        if(projectId != null) {
            wrapper.eq(SysRoleBDBEntity::getProjectId, projectId);
        }
        if(StringUtils.isNotBlank(projectName)) {
            wrapper.eq(ProjectEntity::getName, projectName);
        }
        List<SysRole> sysRoles = sysRoleBMapperService.selectJoinList(SysRole.class, wrapper);
        if(sysRoles != null && sysRoles.size() > 0){
            return sysRoles.get(0);
        }
        return null;
    }

    public List<Long> listRoleIdsByProjectAndUser(Long projectId,Long userId){
        return sysRoleMapper.listRoleIdsByProjectAndUser(projectId,userId);
    }
}

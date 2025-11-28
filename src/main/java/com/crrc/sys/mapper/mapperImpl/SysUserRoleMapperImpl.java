package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.role.SysRoleBDBEntity;
import com.crrc.sys.entity.role.SysRoleTlDBEntity;
import com.crrc.sys.entity.user.SysUserDBEntity;
import com.crrc.sys.entity.user.SysUserRole;
import com.crrc.sys.entity.user.SysUserRoleDBEntity;
import com.crrc.sys.mapper.mapperService.SysRoleBMapperService;
import com.crrc.sys.mapper.mapperService.SysUserRoleMapperService;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import com.crrc.sys.model.user.role.SysUserRolePO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联表-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-20
 */
@Repository
public class SysUserRoleMapperImpl extends ServiceImpl<SysUserRoleMapperService, SysUserRoleDBEntity> {

    @Resource
    private SysUserRoleMapperService sysUserRoleMapperService;

    @Resource
    private SysRoleBMapperService sysRoleBMapperService;


    public List<SysRole> findByUserId(long userId, String language) {
        MPJLambdaWrapper<SysRoleBDBEntity> wrapper = new MPJLambdaWrapper<SysRoleBDBEntity>()
                .selectAll(SysRoleBDBEntity.class)
                .select(SysRoleTlDBEntity::getRoleName, SysRoleTlDBEntity::getRoleDescription)
                .innerJoin(SysRoleTlDBEntity.class,
                        fn -> fn.eq(SysRoleTlDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                                .eq(SysRoleTlDBEntity::getLanguage, language)
                )
                .innerJoin(SysUserRoleDBEntity.class, SysUserRoleDBEntity::getRoleId, SysRoleBDBEntity::getRoleId)
                .eq(SysUserRoleDBEntity::getUserId, userId);
        return sysRoleBMapperService.selectJoinList(SysRole.class, wrapper);
    }


    public int insert(List<SysUserRole> userRoleList) {
        List<SysUserRoleDBEntity> entities = convertToDB(userRoleList);
        return super.saveBatch(entities) ? entities.size() : 0;
    }


    public int deleteByUserId(long userId) {
        LambdaUpdateWrapper<SysUserRoleDBEntity> wrapper = new LambdaUpdateWrapper<SysUserRoleDBEntity>()
                .eq(SysUserRoleDBEntity::getUserId, userId);
        return sysUserRoleMapperService.delete(wrapper);
    }


    public List<Long> findRoleIdByUserId(long userId) {
        MPJLambdaWrapper<SysUserRoleDBEntity> wrapper = new MPJLambdaWrapper<SysUserRoleDBEntity>()
                .select(SysRoleBDBEntity::getRoleId)
                .innerJoin(SysRoleBDBEntity.class, SysRoleBDBEntity::getRoleId, SysUserRoleDBEntity::getRoleId)
                .eq(SysUserRoleDBEntity::getUserId, userId);

        return sysUserRoleMapperService.selectJoinList(Long.class, wrapper);
    }


    public List<Long> findUserIdByRoleId(long roleId) {
        LambdaQueryWrapper<SysUserRoleDBEntity> wrapper = new LambdaQueryWrapper<SysUserRoleDBEntity>()
                .select(SysUserRoleDBEntity::getUserId)
                .eq(SysUserRoleDBEntity::getRoleId, roleId);

        return sysUserRoleMapperService.selectList(wrapper)
                .stream().map(SysUserRoleDBEntity::getUserId)
                .collect(Collectors.toList());
    }

    public List<Long> findUserByRoleCode(String roleCode) {
        LambdaQueryWrapper<SysRoleBDBEntity> roleWrapper = new LambdaQueryWrapper<SysRoleBDBEntity>()
                .eq(SysRoleBDBEntity::getRoleCode,roleCode);
        SysRoleBDBEntity role = sysRoleBMapperService.selectOne(roleWrapper);
        if (null == role) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SysUserRoleDBEntity> wrapper = new LambdaQueryWrapper<SysUserRoleDBEntity>()
                .select(SysUserRoleDBEntity::getUserId)
                .eq(SysUserRoleDBEntity::getRoleId, role.getRoleId());

        return sysUserRoleMapperService.selectList(wrapper)
                .stream().map(SysUserRoleDBEntity::getUserId)
                .collect(Collectors.toList());
    }


    public List<SysUserRoleDTO> findUserRoleByUserIdOrUserName(List<Long> userIds, List<String> userNames) {
        MPJLambdaWrapper<SysUserRoleDBEntity> wrapper = new MPJLambdaWrapper<SysUserRoleDBEntity>()
                .selectAll(SysUserRoleDBEntity.class)
                .select(SysRoleBDBEntity::getRoleCode)
                .select(SysUserDBEntity::getUserName)
                .leftJoin(SysRoleBDBEntity.class, SysRoleBDBEntity::getRoleId, SysUserRoleDBEntity::getRoleId)
                .leftJoin(SysUserDBEntity.class, SysUserDBEntity::getUserId, SysUserRoleDBEntity::getUserId);

        if (userIds != null && !userIds.isEmpty()) {
            wrapper.in(SysUserDBEntity::getUserId, userIds);
        }
        if (userNames != null && !userNames.isEmpty()) {
            wrapper.in(SysUserDBEntity::getUserName, userNames);
        }

        return sysUserRoleMapperService.selectJoinList(SysUserRoleDTO.class, wrapper);
    }


    public void deleteByRoleIds(List<Long> roleIdList) {
        LambdaUpdateWrapper<SysUserRoleDBEntity> wrapper = new LambdaUpdateWrapper<SysUserRoleDBEntity>()
                .in(SysUserRoleDBEntity::getRoleId, roleIdList);
        sysUserRoleMapperService.delete(wrapper);
    }


    public List<SysUserRolePO> findByUserIds(List<Long> userIdList) {
        LambdaQueryWrapper<SysUserRoleDBEntity> wrapper = new LambdaQueryWrapper<SysUserRoleDBEntity>()
                .in(SysUserRoleDBEntity::getUserId, userIdList);

        List<SysUserRoleDBEntity> entities = sysUserRoleMapperService.selectList(wrapper);
        return convertFromDB(entities);
    }

    private List<SysUserRoleDBEntity> convertToDB(List<SysUserRole> dataList) {
        List<SysUserRoleDBEntity> entityList = BeanUtils.toBean(dataList, SysUserRoleDBEntity.class);
        return entityList;
    }

    private List<SysUserRolePO> convertFromDB(List<SysUserRoleDBEntity> entityList) {
        List<SysUserRolePO> dataList = BeanUtils.toBean(entityList, SysUserRolePO.class);
        return dataList;
    }
}
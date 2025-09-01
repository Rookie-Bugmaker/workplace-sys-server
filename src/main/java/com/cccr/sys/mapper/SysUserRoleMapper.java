package com.cccr.sys.mapper;

import com.cccr.sys.entity.role.SysRole;
import com.cccr.sys.entity.user.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色表-Mapper
 *
 * @author 谭永强
 * @date 2022-09-09
 */
public interface SysUserRoleMapper {

    /**
     * 根据用户ID查询拥有的角色集合
     *
     * @param userId   用户ID
     * @param language 语言
     * @return 角色集合
     */
    List<SysRole> findByUserId(@Param("userId") long userId, @Param("language") String language);

    /**
     * 用户角色添加
     *
     * @param userRoleList 用户角色集合
     * @return 结果
     */
    int insert(@Param("userRoleList") List<SysUserRole> userRoleList);

    /**
     * 根据用户ID删除用户角色中间表数据
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteByUserId(@Param("userId") long userId);

    /**
     * 根据用户ID查询拥有的角色ID集合
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    List<Long> findRoleIdByUserId(@Param("userId") long userId);
}

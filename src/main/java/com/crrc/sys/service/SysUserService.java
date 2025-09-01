package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserFindByPageParam;

import java.util.List;

/**
 * 用户-接口
 *
 * @author 谭永强
 * @date 2022-09-02
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    SysUser findByUserName(String userName);

    /**
     * 用户信息分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    PaginationGrid<SysUser> findByPage(SysUserFindByPageParam param);

    /**
     * 用户添加
     *
     * @param user 用户信息
     * @return 结果
     */
    int add(SysUser user);

    /**
     * 用户修改
     *
     * @param user 用户信息
     * @return 结果
     */
    int update(SysUser user);

    /**
     * 用户删除
     *
     * @param userIds 用户ID
     * @return 结果
     */
    int delete(List<Long> userIds);

    /**
     * 根据用户ID查询用户
     *
     * @param userId   用户ID
     * @param language 语言
     * @return 用户信息
     */
    SysUser findById(long userId, String language);

    /**
     * 密码错误次数保存
     *
     * @param errorUser 错误次数记录
     * @return 结果
     */
    int updateLoginError(SysUser errorUser);

    /**
     * 根据用户ID集合查询用户信息
     *
     * @param userIds 用户ID集合
     * @return 结果
     */
    List<SysUser> findByUserIds(List<Long> userIds);

    /**
     * 密码修改
     *
     * @param user 用户
     * @return 结果
     */
    int updatePassword(SysUser user);

    /**
     * 根据用户关联ID(员工ID/供应商ID/客户ID)查询用户信息
     *
     * @param relationIdList 用户关联ID(员工ID/供应商ID/客户ID)
     * @return 用户信息
     */
    List<SysUser> findByRelationIds(List<Long> relationIdList);

    /**
     * 根据用户名集合查询用户信息
     *
     * @param userNameList 用户名集合
     * @return 用户信息
     */
    List<SysUser> findByUserNameList(List<String> userNameList);

    /**
     * 根据用户名查询用户信息(包含角色信息)
     *
     * @param userName 用户名
     * @param language 语言
     * @return 用户信息
     */
    SysUser findRoleByUserName(String userName, String language);

    /**
     * 用户添加(批量)
     *
     * @param list 用户信息
     * @return 结果
     */
    boolean addList(List<SysUser> list);
}

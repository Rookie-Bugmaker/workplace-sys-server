package com.crrc.sys.mapper;

import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-02
 */
public interface SysUserMapper {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    SysUser findByUserName(@Param("userName") String userName);

    /**
     * 用户信息模糊查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    List<SysUser> findByFuzzy(SysUserFindByPageParam param);

    /**
     * 用户添加
     *
     * @param user 用户信息
     * @return 结果
     */
    int insert(SysUser user);

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
    int delete(@Param("userIds") List<Long> userIds);

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser findById(@Param("userId") long userId);

    /**
     * 修改用户最后登录信息ID
     *
     * @param loginId 登录日志ID
     * @param userId  用户ID
     * @return 结果
     */
    int updateLastLoginInfoId(@Param("loginId") long loginId, @Param("userId") long userId);

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
    List<SysUser> findByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 密码修改
     *
     * @param userId               用户ID
     * @param password             密码
     * @param passwordUpdateDate   修改时间
     * @return 结果
     */
    int updatePassword(@Param("userId") long userId, @Param("password") String password, @Param("passwordUpdateDate") Date passwordUpdateDate);

    /**
     * 根据用户关联ID(员工ID/供应商ID/客户ID)查询用户信息
     *
     * @param relationIdList 用户关联ID(员工ID/供应商ID/客户ID)
     * @return 用户信息
     */
    List<SysUser> findByRelationIds(@Param("relationIdList") List<Long> relationIdList);

    /**
     * 根据用户名集合查询用户信息
     *
     * @param userNameList 用户名集合
     * @return 用户信息
     */
    List<SysUser> findByUserNameList(@Param("userNameList") List<String> userNameList);

    /**
     * 根据角色code查询用户信息
     * @param roleCode
     * @return
     */
    List<SysUser> findUsersByRoleCode(@Param("roleCodes") List<String> roleCode);

    List<SysUser> findUsersByOrgCode(@Param("orgCode") String orgCode, @Param("companyId") Long companyId);

    /**
     *  根据用户全名查询用户信息列表
     * @param fullName
     * @param companyId
     * @return
     */
    List<SysUserDTO> findUsersByfullName(@Param("fullName") String fullName, @Param("companyId") Long companyId);


    /**
     * 批量保存
     *
     * @param list 用户集合
     * @return 结果
     */
    boolean insertAll(@Param("list") List<SysUser> list);

    /**
     * 根据用户ID批量查询并模糊查询
     *
     * @param userIdList 用户ID集合
     * @param userName   用户名(模糊搜索)
     * @param fullName   姓名(模糊搜索)
     * @return 结果
     */
    List<SysUser> findByUserIdsAndFuzzy(@Param("userIdList") List<Long> userIdList, @Param("userName") String userName, @Param("fullName") String fullName);
}
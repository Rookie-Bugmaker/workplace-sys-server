package com.crrc.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.model.org.person.SysOrgPersonParamOfUserPageDTO;
import com.crrc.sys.model.user.SysOnlineUserParamDTO;
import com.crrc.sys.model.user.SysOnlineUserVO;
import com.crrc.sys.model.user.UserGetRespVO;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import org.apache.ibatis.annotations.Param;

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
     * 根据用户id查询角色
     *
     * @param userIdList
     * @return
     */
    List<SysUserRoleDTO> findUserRoleByUserIdOrUserName(@Param("userIdList") List<Long> userIdList, @Param("userNameList") List<String> userNameList);

    /**
     * 根据角色code查询用户信息
     *
     * @param roleCodes
     * @return
     */
    List<SysUser> findUsersByRoleCode(List<String> roleCodes);

    List<SysUserDTO> findUserByRoleCodeAndCompanyId(String roleCode, Long companyId);

    List<SysUserDTO> findUserByRoleCode(String roleCode, Long companyId, Long deptId);

    /**
     * 根据用户全名查询用户信息列表
     *
     * @param fullName
     * @param companyId
     * @return
     */
    List<SysUserDTO> findUsersByfullName(String fullName, Long companyId);

    public List<SysUser> findUsersByOrgCode(String orgCode, Long companyId);


    /**
     * 用户信息分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    PaginationGrid<SysUser> findByPage(SysUserFindByPageParam param);

    List<SysUser> findList(SysUserFindByPageParam param);

    /**
     * 用户信息分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    IPage<SysUser> findByFuzzyAndDeptPage(SysUserFindByPageParam param, List<Long> deptIdList);

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

    List<SysUserDTO> findByUserIdListWithFullName(List<Long> userIds);

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
     * 根据用户名集合查询用户信息
     *
     * @param userNameList 用户名集合
     * @param search       搜索条件
     * @return 用户信息
     */
    List<SysOnlineUserVO> findByUserNamesAndFuzzy(List<String> userNameList, SysOnlineUserParamDTO search);

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

    /**
     * 查询角色ID绑定的用户列表
     *
     * @param roleId 角色ID
     * @return 结果
     */
    List<SysUser> findUserByRoleId(Long roleId);

    List<SysUser> findUserByRoleCode(String rokeKey);

    /**
     * 获取带角色的用户List
     *
     * @param ids
     * @return
     */
    List<UserGetRespVO> getUsersWithRoles(List<Long> ids);

    UserGetRespVO getUserWithRolesByUsername(String username);

    PaginationGrid<SysUser> getUserPageWithPersonInform(SysOrgPersonParamOfUserPageDTO param);
}

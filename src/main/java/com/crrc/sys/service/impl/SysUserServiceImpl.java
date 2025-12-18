package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.entity.user.SysUserRole;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.mapper.mapperImpl.*;
import com.crrc.sys.model.org.person.SysOrgPersonParamOfUserPageDTO;
import com.crrc.sys.model.user.SysOnlineUserParamDTO;
import com.crrc.sys.model.user.SysOnlineUserVO;
import com.crrc.sys.model.user.UserGetRespVO;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import com.crrc.sys.model.user.role.SysUserRolePO;
import com.crrc.sys.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户-接口实现
 *
 * @author 谭永强
 * @date 2022-09-02
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Autowired
    private SysUserMapperImpl userMapper;
    @Autowired
    private SysUserRoleMapperImpl userRoleMapper;
    @Autowired
    private SysDataAuthMapperImpl dataAuthMapper;
    @Autowired
    private SysDataAuthValueMapperImpl dataAuthValueMapper;

    @Autowired
    private SysOrgPersonMapperImpl personMapper;

    @Resource
    private AuthClient authClient;

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    @Override
    public SysUser findByUserName(String userName) {
        SysUser sysUser = userMapper.findByUserName(userName);
        if (!ObjectUtils.isEmpty(sysUser)) {
            List<Long> roleIdList = userRoleMapper.findRoleIdByUserId(sysUser.getUserId());
            sysUser.setRoleIdList(roleIdList);
        }
        return sysUser;
    }

    @Override
    public List<SysUserRoleDTO> findUserRoleByUserIdOrUserName(List<Long> userIdList, List<String> userNameList) {
        return userRoleMapper.findUserRoleByUserIdOrUserName(userIdList, userNameList);
    }


    @Override
    public List<SysUser> findUsersByRoleCode(List<String> roleCodes) {
        List<SysUser> userList = userMapper.findUsersByRoleCode(roleCodes);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userMapper.findUsersByRoleCode(roleCodes);
    }

    @Override
    public List<SysUserDTO> findUserByRoleCodeAndCompanyId(String roleCode, Long companyId) {
        List<SysUserDTO> userList = userMapper.findUserByRoleCodeAndCompanyId(roleCode, companyId);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userMapper.findUserByRoleCodeAndCompanyId(roleCode, companyId);
    }

    @Override
    public List<SysUserDTO> findUserByRoleCode(String roleCode, Long companyId, Long deptId) {
        List<SysUserDTO> userList = userMapper.findUserByRoleCode(roleCode, companyId, deptId);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userMapper.findUserByRoleCode(roleCode, companyId, deptId);
    }

    @Override
    public List<SysUserDTO> findUsersByfullName(String fullName, Long companyId) {
        List<SysUserDTO> userList = userMapper.findUsersByfullName(fullName, companyId);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userMapper.findUsersByfullName(fullName, companyId);
    }

    public List<SysUser> findUsersByOrgCode(String orgCode, Long companyId) {
        List<SysUser> userList = userMapper.findUsersByOrgCode(orgCode, companyId);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userMapper.findUsersByOrgCode(orgCode, companyId);
    }


    /**
     * 用户信息分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @Override
    public PaginationGrid<SysUser> findByPage(SysUserFindByPageParam param) {
        if (!ObjectUtils.isEmpty(param.getUserName()) && !ObjectUtils.isEmpty(param.getUserDescription()) && ObjectUtils.isEmpty(param.getMontage())) {
            param.setMontage("and");
        }
//        PageHelper.startPage(param.getPage(), param.getPageSize());
//        List<SysUser> rows = userMapper.findByFuzzy(param);
//        PageInfo<SysUser> pageInfo = new PageInfo<>(rows);
        IPage<SysUser> midPage = userMapper.findByFuzzyPage(param);
        PaginationGrid<SysUser> page = new PaginationGrid<>(
                Math.toIntExact(midPage.getCurrent()), Math.toIntExact(midPage.getSize()));
        page.setTotal(midPage.getTotal());
        page.setRows(midPage.getRecords());
        return page;
    }

    /**
     * 用户信息列表查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @Override
    public List<SysUser> findList(SysUserFindByPageParam param) {
        if (!ObjectUtils.isEmpty(param.getUserName()) && !ObjectUtils.isEmpty(param.getUserDescription()) && ObjectUtils.isEmpty(param.getMontage())) {
            param.setMontage("and");
        }
        return userMapper.findByFuzzy(param);
    }

    @Override
    public IPage<SysUser> findByFuzzyAndDeptPage(SysUserFindByPageParam param, List<Long> deptIdList) {
        if (!ObjectUtils.isEmpty(param.getUserName()) && !ObjectUtils.isEmpty(param.getUserDescription()) && ObjectUtils.isEmpty(param.getMontage())) {
            param.setMontage("and");
        }
        // PageHelper.startPage(param.getPage(), param.getPageSize());
//        List<SysUser> rows = userMapper.findByFuzzyAndDeptPage(param, deptIdList);
//        PageInfo<SysUser> pageInfo = new PageInfo<>(rows);
//        return PaginationGrid.success(pageInfo);
        return userMapper.findByFuzzyAndDeptPage(param, deptIdList);
    }

    /**
     * 用户添加
     *
     * @param user 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int add(SysUser user) {
        //1.添加用户角色
        final List<SysRole> roleList = user.getRoleList();
        if (!CollectionUtils.isEmpty(roleList)) {
            List<SysUserRole> userRoleList = roleList.stream().map(o -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setId(idKeyGenerator.getId());
                userRole.setUserId(user.getUserId());
                userRole.setRoleId(o.getRoleId());
                userRole.setCreateBy(user.getCreateBy());
                userRole.setCreateDate(user.getCreateDate());
                return userRole;
            }).collect(Collectors.toList());
            userRoleMapper.insert(userRoleList);
        }
        List<SysDataAuth> dataAuthList = user.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            //3.添加用户数据权限
            dataAuthMapper.insertList(dataAuthList);
            //4.添加用户数据权限值
            dataAuthValueMapper.insertList(dataAuthList.stream().map(SysDataAuth::getDataAuthValueList).flatMap(Collection::stream).collect(Collectors.toList()));
        }
        //6.添加用户
        return userMapper.insert(user);
    }

    /**
     * 用户修改
     *
     * @param user 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SysUser user) {
        //1.修改用户登录失败的错误信息
        userMapper.updateLoginError(user);
        //2.删除用户角色中间表数据
        userRoleMapper.deleteByUserId(user.getUserId());
        List<SysUserRole> userRoleList = user.getRoleList().stream().map(o -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setId(idKeyGenerator.getId());
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(o.getRoleId());
            userRole.setCreateBy(o.getCreateBy());
            userRole.setCreateDate(o.getCreateDate());
            return userRole;
        }).collect(Collectors.toList());
        //5.保存用户角色中间表数据
        userRoleMapper.insert(userRoleList);
        //6.删除用户数据权限值
        dataAuthValueMapper.delete(user.getUserId());
        //7.删除用户数据权限
        dataAuthMapper.delete(user.getUserId());
        List<SysDataAuth> dataAuthList = user.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            //8.添加用户数据权限
            dataAuthMapper.insertList(dataAuthList);
            //9.添加用户数据权限值
            dataAuthValueMapper.insertList(dataAuthList.stream().map(SysDataAuth::getDataAuthValueList).flatMap(Collection::stream).collect(Collectors.toList()));
        }
        //12.修改用户信息
        int result = userMapper.update(user);
        if (result <= 0) {
            throw new BizException("用户信息修改失败");
        }
        return result;
    }

    /**
     * 用户删除
     *
     * @param userIds 用户ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(List<Long> userIds) {
        for (Long userId : userIds) {
            //删除用户角色中间表数据
            userRoleMapper.deleteByUserId(userId);
        }
        //删除用户数据权限值
        dataAuthValueMapper.deleteByObjectIds(userIds);
        //删除用户数据权限
        dataAuthMapper.deleteByObjectIds(userIds);
        //删除用户
        return userMapper.delete(userIds);
    }

    /**
     * 根据用户ID查询用户
     *
     * @param userId   用户ID
     * @param language 语言
     * @return 用户信息
     */
    @Override
    public SysUser findById(long userId, String language) {
        //根据用户ID查询用户
        SysUser user = userMapper.findById(userId);
        if (ObjectUtils.isEmpty(user)) {
            throw new BizException("用户不存在!");
        }
        //根据用户ID查询拥有的角色ID集合
        List<SysRole> roleList = userRoleMapper.findByUserId(userId, language);
        user.setRoleList(roleList);
        //根据用户ID查询组织列表
        //根据用户ID查询该用户数据权限
        List<SysDataAuth> dataAuthList = dataAuthMapper.findByObjectId(userId);
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            List<Long> authIdList = dataAuthList.stream().map(SysDataAuth::getAuthId).collect(Collectors.toList());
            List<SysDataAuthValue> dataAuthValueList = dataAuthValueMapper.findByAuthIds(authIdList);
            dataAuthList.forEach(o -> {
                o.setDataAuthValueList(dataAuthValueList.stream().filter(i -> i.getAuthId().equals(o.getAuthId())).collect(Collectors.toList()));
            });
            user.setDataAuthList(dataAuthList);
        }
        return user;
    }

    /**
     * 密码错误次数保存
     *
     * @param errorUser 错误次数记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateLoginError(SysUser errorUser) {
        return userMapper.updateLoginError(errorUser);
    }

    /**
     * 根据用户ID集合查询用户信息
     *
     * @param userIds 用户ID集合
     * @return 结果
     */
    @Override
    public List<SysUser> findByUserIds(List<Long> userIds) {
        return userMapper.findByUserIds(userIds);
    }

    @Override
    public List<SysUserDTO> findByUserIdListWithFullName(List<Long> userIds) {
        return userMapper.findByUserIdListWithFullName(userIds);
    }

    /**
     * 密码修改
     *
     * @param user 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePassword(SysUser user) {
        //将下一次修改密码字段设置为N
        user.setNextLoginUpdatePassword("N");
        //修改密码
        int result = userMapper.updatePassword(user);
        if (result <= 0) {
            throw new BizException("修改失败");
        }
        authClient.updateNextLoginUpdatePassword(user.getNextLoginUpdatePassword());
        return result;
    }

    /**
     * 根据用户关联ID(员工ID/供应商ID/客户ID)查询用户信息
     *
     * @param relationIdList 用户关联ID(员工ID/供应商ID/客户ID)
     * @return 用户信息
     */
    @Override
    public List<SysUser> findByRelationIds(List<Long> relationIdList) {
        return userMapper.findByRelationIds(relationIdList);
    }

    /**
     * 根据用户名集合查询用户信息
     *
     * @param userNameList 用户名集合
     * @return 用户信息
     */
    @Override
    public List<SysUser> findByUserNameList(List<String> userNameList) {
        return userMapper.findByUserNameList(userNameList);
    }

    /**
     * 根据用户名集合查询用户信息
     *
     * @param userNameList 用户名集合
     * @param search       搜索条件
     * @return 用户信息
     */
    @Override
    public List<SysOnlineUserVO> findByUserNamesAndFuzzy(List<String> userNameList, SysOnlineUserParamDTO search) {
        return userMapper.findByUserNamesAndFuzzy(userNameList, search);
    }

    /**
     * 根据用户名查询用户信息(包含角色信息)
     *
     * @param userName 用户名
     * @param language 语言
     * @return 用户信息
     */
    @Override
    public SysUser findRoleByUserName(String userName, String language) {
        SysUser sysUser = userMapper.findByUserName(userName);
        if (!ObjectUtils.isEmpty(sysUser)) {
            List<SysRole> roleList = userRoleMapper.findByUserId(sysUser.getUserId(), language);
            sysUser.setRoleList(roleList);
        }
        return sysUser;
    }

    /**
     * 用户添加(批量)
     *
     * @param list 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addList(List<SysUser> list) {
        ArrayList<SysUserRole> userRoleList = new ArrayList<>();
        list.forEach(user -> {
            if (!CollectionUtils.isEmpty(user.getUserRoleList())) {
                userRoleList.addAll(user.getUserRoleList());
            }
        });
        if (!CollectionUtils.isEmpty(userRoleList)) {
            // 保存用户分配角色
            userRoleMapper.insert(userRoleList);
        }
        // 保存用户信息
        return userMapper.insertAll(list);
    }

    /**
     * 查询角色ID绑定的用户列表
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public List<SysUser> findUserByRoleId(Long roleId) {
        List<Long> userIdList = userRoleMapper.findUserIdByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIdList)) {
            return null;
        }
        return userMapper.findByUserIds(userIdList);
    }

    @Override
    public List<SysUser> findUserByRoleCode(String roleCode) {
        List<Long> userIdList = userRoleMapper.findUserByRoleCode(roleCode);
        if (CollectionUtils.isEmpty(userIdList)) {
            return null;
        }
        return userMapper.findByUserIds(userIdList);
    }

    @Override
    public List<UserGetRespVO> getUsersWithRoles(List<Long> userIds) {
        // 查询用户列表
        List<SysUser> users = userMapper.findByUserIds(userIds);

        // 将角色列表设置到用户对象中
        List<UserGetRespVO> dtoList = new ArrayList<>();
        for (SysUser user : users) {
            UserGetRespVO dto = new UserGetRespVO();
            dto.setId(user.getUserId());
            dto.setUserName(user.getUserName());
            dto.setStatus("Y".equals(user.getDisableState()) ? "1" : "0");
            dto.setUserType(user.getUserType());
            if (!ObjectUtils.isEmpty(user.getPasswordUpdateDate())) {
                dto.setPasswordUpdateTime(LocalDateTime.ofInstant(user.getPasswordUpdateDate().toInstant(), ZoneId.systemDefault()));
            }
            // 关联角色
            List<UserGetRespVO.Role> roles = new ArrayList<>();
            userRoleMapper.findByUserId(user.getUserId(), "zh-CN").forEach(sysRole -> {
                UserGetRespVO.Role role = new UserGetRespVO.Role();
                role.setId(String.valueOf(sysRole.getRoleId()));
                role.setName(sysRole.getRoleName());
                role.setRoleKey(sysRole.getRoleCode());
                roles.add(role);
            });
            dto.setRoles(roles);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public UserGetRespVO getUserWithRolesByUsername(String username) {
        // 查询用户列表
        SysUser user = userMapper.findByUserName(username);
        // 将角色列表设置到用户对象中
        UserGetRespVO dto = new UserGetRespVO();
        dto.setId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setStatus("Y".equals(user.getDisableState()) ? "1" : "0");
        dto.setUserType(user.getUserType());
        if (!ObjectUtils.isEmpty(user.getPasswordUpdateDate())) {
            dto.setPasswordUpdateTime(LocalDateTime.ofInstant(user.getPasswordUpdateDate().toInstant(), ZoneId.systemDefault()));
        }
        // 关联角色
        List<UserGetRespVO.Role> roles = new ArrayList<>();
        userRoleMapper.findByUserId(user.getUserId(), "zh-CN").forEach(sysRole -> {
            UserGetRespVO.Role role = new UserGetRespVO.Role();
            role.setId(String.valueOf(sysRole.getRoleId()));
            role.setName(sysRole.getRoleName());
            role.setRoleKey(sysRole.getRoleCode());
            roles.add(role);
        });
        dto.setRoles(roles);
        return dto;
    }

    @Override
    public PaginationGrid<SysUser> getUserPageWithPersonInform(SysOrgPersonParamOfUserPageDTO param) {
        // 查询人员
        SysOrgPersonFindByPageParam personParam = new SysOrgPersonFindByPageParam();
        personParam.setLanguage("zh-CN");
        if (!Objects.isNull(param.getPageNo()) && !Objects.isNull(param.getPageSize())) {
            personParam.setPage(param.getPageNo());
            personParam.setPageSize(param.getPageSize());
//            PageHelper.startPage(personParam.getPage(), personParam.getPageSize());
        }
        if (!Objects.isNull(param.getPersonTypeList()) && !param.getPersonTypeList().isEmpty()) {
            personParam.setPersonTypeList(param.getPersonTypeList());
        }
        if (StringUtils.isNotBlank(param.getJobNo())) {
            personParam.setJobNo(param.getJobNo());
        }
        if (StringUtils.isNotBlank(param.getFullName())) {
            personParam.setFullName(param.getFullName());
        }
        if (!ObjectUtils.isEmpty(personParam.getJobNo()) && !ObjectUtils.isEmpty(personParam.getFullName()) && ObjectUtils.isEmpty(personParam.getMontage())) {
            personParam.setMontage("and");
        }
        // 通过人员id关联查询用户
//        PageHelper.startPage(param.getPageNo(), param.getPageSize());
//        PageInfo<SysUser> pageInfo = new PageInfo<>(rows);
        IPage<SysUser> midPage = userMapper.findByPersonParamPage(personParam);
        PaginationGrid<SysUser> page = new PaginationGrid<>(
                Math.toIntExact(midPage.getCurrent()), Math.toIntExact(midPage.getSize()));
        page.setTotal(midPage.getTotal());
        page.setRows(midPage.getRecords());
        return page;
    }
}

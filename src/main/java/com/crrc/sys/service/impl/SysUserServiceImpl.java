package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthDBEntity;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.auth.SysDataAuthValueDBEntity;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.entity.profile.SysProfileBDBEntity;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserFindByPageParam;
import com.crrc.sys.entity.user.SysUserRole;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.entity.user.dto.SysUserRequestDTO;
import com.crrc.sys.enums.DataAuthTypeEnum;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.mapper.mapperImpl.*;
import com.crrc.sys.model.org.person.SysOrgPersonParamOfUserPageDTO;
import com.crrc.sys.model.user.AuthInformDTO;
import com.crrc.sys.model.user.SysOnlineUserParamDTO;
import com.crrc.sys.model.user.SysOnlineUserVO;
import com.crrc.sys.model.user.UserGetRespVO;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import com.crrc.sys.model.user.role.SysUserRolePO;
import com.crrc.sys.service.SysDataAuthService;
import com.crrc.sys.service.SysProfileService;
import com.crrc.sys.service.SysUserService;
import com.crrc.sys.service.CompanyService;
import com.crrc.sys.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户-接口实现
 *
 * @author 谭永强
 * @date 2022-09-02
 */
@Service
@Slf4j
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
    private SysProfileMapperImpl profileMapper;
    @Autowired
    private SysRoleMapperImpl roleMapper;

    @Autowired
    private SysOrgPersonMapperImpl personMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DeptService deptService;

    @Resource
    private SysDataAuthService sysDataAuthService;

    @Resource
    private SysProfileService profileService;

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
    public PaginationGrid<SysUserDTO> findUsersByMultiCondition(SysUserRequestDTO param) {
        List<Long> companyIds = param.getCompanyIds();
        List<String> companyOrgCodes = param.getCompanyOrgCodes();
        List<Long> deptIds = param.getDeptIds();
        List<String> deptOrgCodes = param.getDeptOrgCodes();
        List<String> roleCodes = param.getRoleCodes();
        String userName = param.getUserName();
        String userDescription = param.getUserDescription();
        String jobNo = param.getJobNo();
        String montage = param.getMontage();

        // 分页参数：默认 page=1, pageSize=50
        int page = param.getPage() != null && param.getPage() > 0 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() > 0 ? param.getPageSize() : 50;

        // 归一化公司：ID直接用，orgCode批量转ID（取Company.id，对应sys_dept_person.company_id）
        List<Long> companyIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(companyIds)) {
            companyIdList.addAll(companyIds);
        }
        if (!CollectionUtils.isEmpty(companyOrgCodes)) {
            List<Company> companyList = companyService.list(
                    new LambdaQueryWrapper<Company>().in(Company::getOrgCode, companyOrgCodes));
            if (CollectionUtils.isEmpty(companyList)) {
                log.warn("findUsersByMultiCondition: companyOrgCodes 未命中任何公司, 短路返回空: {}", companyOrgCodes);
                return new PaginationGrid<>(page, pageSize);
            }
            companyList.forEach(c -> companyIdList.add(c.getId()));
        }

        // 归一化部门：ID直接用，orgCode批量转ID（取Dept.id，对应sys_dept_person.dept_id）
        List<Long> deptIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(deptIds)) {
            deptIdList.addAll(deptIds);
        }
        if (!CollectionUtils.isEmpty(deptOrgCodes)) {
            List<Dept> deptList = deptService.getDeptByOrgCodeList(deptOrgCodes);
            if (CollectionUtils.isEmpty(deptList)) {
                log.warn("findUsersByMultiCondition: deptOrgCodes 未命中任何部门, 短路返回空: {}", deptOrgCodes);
                return new PaginationGrid<>(page, pageSize);
            }
            deptList.forEach(d -> deptIdList.add(d.getId()));
        }

        Page<SysUserDTO> pageObj = new Page<>(page, pageSize);
        IPage<SysUserDTO> result = userMapper.findUsersByMultiCondition(
                pageObj, companyIdList, deptIdList, roleCodes, userName, userDescription, jobNo, montage);
        return PaginationGrid.success(result);
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
        if (CollectionUtils.isEmpty(list)) {
            return true;
        }

        // 提交时重新查询已有用户，保留原账号资料，并使用真实用户ID补充角色
        List<String> userNames = list.stream().map(SysUser::getUserName).distinct().collect(Collectors.toList());
        Map<String, SysUser> userByName = new HashMap<>();
        final int batchSize = 900;
        for (int start = 0; start < userNames.size(); start += batchSize) {
            List<SysUser> existsUsers = userMapper.findByUserNameList(
                    userNames.subList(start, Math.min(start + batchSize, userNames.size())));
            existsUsers.forEach(user -> userByName.put(user.getUserName(), user));
        }

        // 直接读取用户角色关系，按用户ID和角色ID去重
        Map<Long, Set<Long>> roleIdsByUser = new HashMap<>();
        List<Long> existsUserIds = userByName.values().stream().map(SysUser::getUserId).collect(Collectors.toList());
        for (int start = 0; start < existsUserIds.size(); start += batchSize) {
            List<SysUserRolePO> existsRoles = userRoleMapper.findByUserIds(
                    existsUserIds.subList(start, Math.min(start + batchSize, existsUserIds.size())));
            existsRoles.forEach(role -> roleIdsByUser.computeIfAbsent(role.getUserId(), key -> new HashSet<>())
                    .add(role.getRoleId()));
        }

        List<SysUser> newUsers = new ArrayList<>();
        List<SysUserRole> newRoles = new ArrayList<>();
        for (SysUser user : list) {
            SysUser targetUser = userByName.get(user.getUserName());
            if (targetUser == null) {
                // 文件内同名用户只新增首条，后续行继续合并角色
                targetUser = user;
                userByName.put(user.getUserName(), targetUser);
                newUsers.add(targetUser);
            }
            Set<Long> roleIds = roleIdsByUser.computeIfAbsent(targetUser.getUserId(), key -> new HashSet<>());
            if (!CollectionUtils.isEmpty(user.getUserRoleList())) {
                for (SysUserRole role : user.getUserRoleList()) {
                    if (roleIds.add(role.getRoleId())) {
                        role.setUserId(targetUser.getUserId());
                        newRoles.add(role);
                    }
                }
            }
        }

        // 仅写入缺少的用户和角色，全部重复时直接成功；写入失败在事务内抛出异常
        if (!CollectionUtils.isEmpty(newUsers) && !userMapper.insertAll(newUsers)) {
            throw new BizException("入库失败！");
        }
        if (!CollectionUtils.isEmpty(newRoles) && userRoleMapper.insert(newRoles) != newRoles.size()) {
            throw new BizException("入库失败！");
        }
        return true;
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

    @Override
    public List<SysUser> getUserWithAuthenticationInform(List<String> roleCodeList, AuthInformDTO authInform) {
        List<SysUser> validUserList = new ArrayList<>();
        // 1.根据角色编码查询对应角色的数据权限，如果对应角色有存在于dataAuthCodeToValueMap的数据权限，则该角色下人员需要获取
        List<String> validRoleCodeList = new ArrayList<>();
        List<String> invalidRoleCodeList = new ArrayList<>();
        if(roleCodeList.isEmpty() || authInform == null || authInform.getData().isEmpty()){
            return validUserList;
        }
        // 查找对应角色信息
        List<SysRole> roleList = roleMapper.findByCodes(roleCodeList, LanguageEnum.ZH_CN.getValue());
        roleList.forEach(role -> {
            // 过滤数据权限与给定的权限符合的角色
            Map<String, List<String>> dataAuthCodeToValueMapByRole =
                    sysDataAuthService.findDataAuthsByObjectId(role.getRoleId(), DataAuthTypeEnum.ROLE.getValue());
            Boolean existedAllAuthValue = judgeAuthValueReturnExisted(dataAuthCodeToValueMapByRole, authInform);
            if (existedAllAuthValue) {
                validRoleCodeList.add(role.getRoleCode());
            } else {
                invalidRoleCodeList.add(role.getRoleCode());
            }
        });
        if (!validRoleCodeList.isEmpty()) {
            validUserList.addAll(this.findUsersByRoleCode(validRoleCodeList));
        }
        // 2.如果对应角色在dataAuthCodeToValueMap下没有角色拥有的数据权限，则在当前角色（缺省角色）下查询人员，判断人员是否有对应数据权限。有则返回
        if (!CollectionUtils.isEmpty(invalidRoleCodeList)) {
            List<SysUser> userList = this.findUsersByRoleCode(invalidRoleCodeList);
            List<SysUser> finalValidUserList = validUserList;
            userList.forEach(user -> {
                // 过滤数据权限与给定的权限符合的人员
                Map<String, List<String>> dataAuthCodeToValueMapByUser =
                        sysDataAuthService.findDataAuthsByObjectId(user.getUserId(), DataAuthTypeEnum.USER.getValue());
                Boolean existedAllAuthValue = judgeAuthValueReturnExisted(dataAuthCodeToValueMapByUser, authInform);
                if (existedAllAuthValue) {
                    finalValidUserList.add(user);
                }
            });
        }
        // 3.合并去重1、2下获取的人员进行返回
        if (!validUserList.isEmpty()) {
            validUserList = validUserList.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    SysUser::getUserId,  // 以userId作为去重key
                                    user -> user,        // 保留用户对象
                                    (existing, replacement) -> existing  // 重复时保留已存在的记录
                            ),
                            map -> new ArrayList<>(map.values())  // 转换回List
                    ));
        }
        return validUserList;
    }

    @Override
    public List<SysUser> findUsersByDataAuth(AuthInformDTO authInform) {
        List<SysUser> validUserList = new ArrayList<>();
        Set<String> profileCodes = authInform.getData().keySet();
        // 根据编码从配置进行查询
        LambdaQueryWrapper<SysProfileBDBEntity> profileWrapper = new LambdaQueryWrapper<>();
        profileWrapper.select(SysProfileBDBEntity::getProfileCode, SysProfileBDBEntity::getProfileId)
                .in(SysProfileBDBEntity::getProfileCode, profileCodes);
        List<SysProfileBDBEntity> profileList = profileMapper.list(profileWrapper);
        Map<Long, String> profileIdToDataAuthCodeMap = profileList.stream()
                .collect(Collectors.toMap(SysProfileBDBEntity::getProfileId, SysProfileBDBEntity::getProfileCode,
                        (existing, replacement) -> existing));
        // 查询拥有当前配置（权限编码）的用户ID
        LambdaQueryWrapper<SysDataAuthDBEntity> dataAuthWrapper = new LambdaQueryWrapper<>();
        dataAuthWrapper.select(SysDataAuthDBEntity::getAuthId, SysDataAuthDBEntity::getProfileId, SysDataAuthDBEntity::getObjectId)
                .in(SysDataAuthDBEntity::getProfileId, profileIdToDataAuthCodeMap.keySet())
                .eq(SysDataAuthDBEntity::getAuthType, DataAuthTypeEnum.USER.getValue());
        List<SysDataAuthDBEntity> dataAuthAllList = dataAuthMapper.list(dataAuthWrapper);
        // 根据用户ID进行分类
        Map<Long, List<SysDataAuthDBEntity>> groupedByObjectId = dataAuthAllList.stream()
                .collect(Collectors.groupingBy(
                        SysDataAuthDBEntity::getObjectId  // 假设objectId是Long类型
                ));
        List<SysUser> finalValidUserList = validUserList;
        groupedByObjectId.entrySet().forEach(entry1 -> {
            Long userId = entry1.getKey();
            List<SysDataAuthDBEntity> dataAuthList = entry1.getValue();
            Map<Long, Long> authIdToProfileIdMap = dataAuthList.stream()
                    .collect(Collectors.toMap(SysDataAuthDBEntity::getAuthId, SysDataAuthDBEntity::getProfileId,
                            (existing, replacement) -> existing));
            // 查询得到的用户是否有给定的权限对应的所有权限值
            LambdaQueryWrapper<SysDataAuthValueDBEntity> dataAuthValueWrapper = new LambdaQueryWrapper<>();
            dataAuthValueWrapper.select(SysDataAuthValueDBEntity::getAuthId, SysDataAuthValueDBEntity::getAuthValue)
                    .in(SysDataAuthValueDBEntity::getAuthId, authIdToProfileIdMap.keySet());
            List<SysDataAuthValueDBEntity> dataAuthValueList = dataAuthValueMapper.list(dataAuthValueWrapper);
            Map<Long, List<String>> dataAuthIdToValueMap = dataAuthValueList.stream()
                    .collect(Collectors.groupingBy(
                            SysDataAuthValueDBEntity::getAuthId,
                            Collectors.mapping(
                                    SysDataAuthValueDBEntity::getAuthValue,
                                    Collectors.toList()
                            )
                    ));
            Map<String, List<String>> dataAuthCodeToValueMapInDB = authIdToProfileIdMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            // Key映射：通过authId找到profileId，再通过profileId找到dataAuthCode
                            entry2 -> profileIdToDataAuthCodeMap.get(entry2.getValue()),
                            // Value映射：直接从dataAuthIdToValueMap获取对应的value列表
                            entry2 -> dataAuthIdToValueMap.get(entry2.getKey()),
                            // 合并策略：如果遇到重复key（相同的dataAuthCode），保留已存在的value
                            (existing, replacement) -> existing,
                            // 指定返回的Map实现类
                            HashMap::new
                    ));
            boolean existed = judgeAuthValueReturnExisted(dataAuthCodeToValueMapInDB, authInform);
            if (existed) {
                finalValidUserList.add(userMapper.findById(userId));
            }
        });
        if (!validUserList.isEmpty()) {
            validUserList = validUserList.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    SysUser::getUserId,  // 以userId作为去重key
                                    user -> user,        // 保留用户对象
                                    (existing, replacement) -> existing  // 重复时保留已存在的记录
                            ),
                            map -> new ArrayList<>(map.values())  // 转换回List
                    ));
        }
        return validUserList;
    }

    /**
     * 用户批量新增（带角色和数据权限）
     *
     * @param users 用户信息列表
     * @return 成功数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addBatch(List<SysUser> users) {
        if (CollectionUtils.isEmpty(users)) {
            return 0;
        }

        // 1. 批量插入用户
        boolean result = userMapper.insertAll(users);
        if (!result) {
            throw new BizException("批量新增用户失败");
        }

        // 2. 收集所有角色绑定信息
        List<SysUserRole> allUserRoles = new ArrayList<>();
        for (SysUser user : users) {
            List<SysRole> roleList = user.getRoleList();
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
                allUserRoles.addAll(userRoleList);
            }
        }

        // 3. 批量插入用户角色
        if (!CollectionUtils.isEmpty(allUserRoles)) {
            userRoleMapper.insert(allUserRoles);
        }

        // 4. 收集所有数据权限信息
        List<SysDataAuth> allDataAuths = new ArrayList<>();
        List<SysDataAuthValue> allDataAuthValues = new ArrayList<>();
        for (SysUser user : users) {
            List<SysDataAuth> dataAuthList = user.getDataAuthList();
            if (!CollectionUtils.isEmpty(dataAuthList)) {
                allDataAuths.addAll(dataAuthList);
                for (SysDataAuth dataAuth : dataAuthList) {
                    List<SysDataAuthValue> valueList = dataAuth.getDataAuthValueList();
                    if (!CollectionUtils.isEmpty(valueList)) {
                        allDataAuthValues.addAll(valueList);
                    }
                }
            }
        }

        // 5. 批量插入数据权限
        if (!CollectionUtils.isEmpty(allDataAuths)) {
            dataAuthMapper.insertList(allDataAuths);
        }

        // 6. 批量插入数据权限值
        if (!CollectionUtils.isEmpty(allDataAuthValues)) {
            dataAuthValueMapper.insertList(allDataAuthValues);
        }

        return users.size();
    }

    /**
     * 检查originMap中的所有entry是否都能在targetMap中找到完全匹配的对应项
     *
     * @param targetMap 目标Map，查找的基准
     * @param authInform 原始鉴权信息，用于遍历查找
     * @return 如果originMap中的所有entry都能在targetMap中找到key相同且value列表完全包含的对应项，则返回true，否则返回false
     */
    public boolean judgeAuthValueReturnExisted(Map<String, List<String>> targetMap,
                                               AuthInformDTO authInform) {
        // 获取用户权限数据和逻辑判断方式
        Map<String, List<String>> authData = authInform.getData();
        boolean isOr = authInform.getIsOr() == null || Boolean.TRUE.equals(authInform.getIsOr());

        // 遍历用户提供的所有权限编码
        for (Map.Entry<String, List<String>> entry : authData.entrySet()) {
            String authCode = entry.getKey();
            List<String> authValues = entry.getValue();

            // 检查目标映射中是否包含当前权限编码
            if (!targetMap.containsKey(authCode)) {
                return false;
            }

            List<String> targetValues = targetMap.get(authCode);

            if (isOr) {
                // OR逻辑：只要有一个权限值匹配就继续检查下一个权限编码
                boolean anyMatch = false;
                for (String userValue : authValues) {
                    if (targetValues.contains(userValue)) {
                        anyMatch = true;
                        break;
                    }
                }
                if (!anyMatch) {
                    return false;
                }
            } else {
                // AND逻辑：所有权限值都必须匹配
                for (String userValue : authValues) {
                    if (!targetValues.contains(userValue)) {
                        return false;
                    }
                }
            }
        }

        // 所有检查都通过
        return true;
    }
}

package com.crrc.sys.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.*;
import com.crrc.sys.common.response.ResponseResult;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.excel.ExcelParams;
import com.crrc.sys.entity.excel.ExcelResultData;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.*;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.entity.user.dto.SysUserRequestDTO;
import com.crrc.sys.entity.user.dto.WorkflowUserPageDTO;
import com.crrc.sys.enums.*;
import com.crrc.sys.excel.ExcelExport;
import com.crrc.sys.excel.ExcelImport;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.model.role.SysRoleCompanyDeptDTO;
import com.crrc.sys.model.user.SysUserCompatibilityDTO;
import com.crrc.sys.model.user.UserGetRespVO;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import com.crrc.sys.service.*;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 用户管理-控制器
 *
 * @author 谭永强
 * @date 2022-09-02
 */
@RestController
@RequestMapping("user")
public class SysUserController {

    private static final String REDIS_PREFIX = "import_user_data";
    /**
     * 密码强度
     * 1:弱、2：中、3：强、4：非常强
     */
    private static final int PWD_STRONG = 3;
    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysUserService userService;
    @Resource
    private SysProfileService profileService;
    @Resource
    private SysRoleService roleService;
    @Resource
    private SysOrgPersonService personService;
    @Resource
    private ExcelExport excelExport;
    @Resource
    private ExcelImport excelImport;
    @Resource
    private ValueOperations<String, Object> valueOperations;
    @Resource
    private SysDeptPersonService deptPersonService;
    @Resource
    private SysRoleController roleController;
    @Resource
    private CompanyService companyService;
    @Resource
    private DeptService deptService;

    @Resource
    private SysOrgPersonService sysOrgPersonService;
    @Resource
    private SysUserService sysUserService;

    /**
     * 测试接口(公寓测试配置文件通过api接口调用)
     *
     * @param param 参数
     * @return 结果
     */
    @GetMapping("test/findUserPage")
    public Map<String, Object> findUserPage(SysUserFindByPageParam param) {
        PaginationGrid<SysUser> paginationGrid = userService.findByPage(param);
        Map<String, Object> map = new HashMap<>();
        map.put("total", paginationGrid.getTotal());
        map.put("rows", paginationGrid.getRows());
        return map;
    }

    /**
     * 用户信息分页查询(copy)
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage/cp")
    public PaginationGrid<SysUser> findByPageCopy(SysUserFindByPageParam param) {
        return userService.findByPage(param);
    }


    /**
     * 用户信息分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @RequestMapping("findByPage")
    public PaginationGrid<SysUser> findByPage(SysUserFindByPageParam param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(param.getPage()) || param.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(param.getPageSize()) || param.getPageSize() < 0) {
            throw new ParamValidateException("pageSize不能为空且必须>=0");
        }
        return userService.findByPage(param);
    }

    /**
     * 用户新增
     *
     * @param user 用户信息
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(@RequestBody SysUser user) {
        verification(OperationTypeEnum.ADD.getValue(), user);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        PasswordEncoder bcpe = new BCryptPasswordEncoder();
        //密文
        String enPassword = bcpe.encode(user.getPassword());
        user.setPassword(enPassword);
        //设置角色信息
        List<SysRole> roleList = user.getRoleIds().stream().map(o -> {
            SysRole role = new SysRole();
            role.setRoleId(o);
            return role;
        }).collect(Collectors.toList());
        user.setRoleList(roleList);
        //设置主键
        user.setUserId(idKeyGenerator.getId());
        //设置创建人
        user.setCreateBy(currentUser.getUserId());
        //设置创建时间
        user.setCreateDate(Calendar.getInstance().getTime());
        //用户数据权限
        List<SysDataAuth> dataAuthList = user.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            dataAuthList.forEach(o -> {
                //主键
                o.setAuthId(idKeyGenerator.getId());
                //权限类型:用户
                o.setAuthType(DataAuthTypeEnum.USER.getValue());
                //用户ID
                o.setObjectId(user.getUserId());
                //设置创建人
                o.setCreateBy(currentUser.getUserId());
                //设置创建时间
                o.setCreateDate(Calendar.getInstance().getTime());
                o.getDataAuthValueList().forEach(i -> {
                    i.setAuthId(o.getAuthId());
                    i.setCreateBy(o.getCreateBy());
                    i.setCreateDate(o.getCreateDate());
                });
            });
        }
        //用户新增
        int result = userService.add(user);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 用户修改
     *
     * @param user 用户信息
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(@RequestBody SysUser user) {
        verification(OperationTypeEnum.UPDATE.getValue(), user);
        if (!ObjectUtils.isEmpty(user.getPassword())) {
            PasswordEncoder bcrypt = new BCryptPasswordEncoder();
            //密文
            String enPassword = bcrypt.encode(user.getPassword());
            user.setPassword(enPassword);
            user.setPasswordUpdateDate(Calendar.getInstance().getTime());
        }
        //当禁用状态==N，取消该用户所有错误信息
        if (EnableFlagEnum.N.getValue().equals(user.getDisableState())) {
            user.setLoginErrorCount(0);
            user.setDisableDeadlineDate(null);
        }
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //设置角色信息
        List<SysRole> roleList = user.getRoleIds().stream().map(o -> {
            SysRole role = new SysRole();
            role.setRoleId(o);
            role.setCreateBy(user.getUserId());
            role.setCreateDate(Calendar.getInstance().getTime());
            return role;
        }).collect(Collectors.toList());
        user.setRoleList(roleList);
        //修改人
        user.setLastUpdateBy(currentUser.getUserId());
        //修改时间
        user.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改的登录信息ID
        user.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        //用户数据权限
        List<SysDataAuth> dataAuthList = user.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            dataAuthList.forEach(o -> {
                //主键
                o.setAuthId(idKeyGenerator.getId());
                //权限类型:用户
                o.setAuthType(DataAuthTypeEnum.USER.getValue());
                //用户ID
                o.setObjectId(user.getUserId());
                //设置创建人
                o.setCreateBy(currentUser.getUserId());
                //设置创建时间
                o.setCreateDate(Calendar.getInstance().getTime());
                o.getDataAuthValueList().forEach(i -> {
                    i.setAuthId(o.getAuthId());
                    i.setCreateBy(o.getCreateBy());
                    i.setCreateDate(o.getCreateDate());
                });
            });
        }
        int result = userService.update(user);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
        //清空用户登录信息
        //1.如果用户禁用状态==Y，则请求改用户的token缓存信息
        //2.如果修改了用户密码，则也需要清空
        if (EnableFlagEnum.Y.getValue().equals(user.getDisableState()) || !ObjectUtils.isEmpty(user.getPassword())) {
            //清空该用户登录信息
            authClient.clear(user.getUserName());
        }
    }

    /**
     * 用户删除
     *
     * @param userIds 用户ID，多个以逗号","拼接
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String userIds) {
        if (ObjectUtils.isEmpty(userIds)) {
            throw new ParamValidateException("userIds不能为空!");
        }
        int result = userService.delete(Arrays.stream(userIds.split(",")).map(Long::parseLong).collect(Collectors.toList()));
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @PostMapping("findById")
    public SysUser findById(Long userId, String language) {
        if (ObjectUtils.isEmpty(userId)) {
            throw new ParamValidateException("userId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        SysUser sysUser = userService.findById(userId, language);
        if (ObjectUtils.isEmpty(sysUser)) {
            throw new BizException("用户不存在!");
        }
        sysUser.setPassword(null);
        //用户数据权限
        List<SysDataAuth> dataAuthList = sysUser.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            List<Long> profileIdList = dataAuthList.stream().map(SysDataAuth::getProfileId).collect(Collectors.toList());
            //根据配置文件ID查询配置文件信息
            List<SysProfile> profileList = profileService.findByIds(profileIdList, language);
            dataAuthList.forEach(o -> {
                SysProfile sysProfile = profileList.stream().filter(i -> i.getProfileId().equals(o.getProfileId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(sysProfile)) {
                    o.setAuthCode(sysProfile.getProfileCode());
                }
            });
        }
        return sysUser;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    @PostMapping("findByUserName")
    public SysUser findByUserName(String userName) {
        if (ObjectUtils.isEmpty(userName)) {
            throw new ParamValidateException("userName不能为空!");
        }
        return userService.findByUserName(userName);
    }

    /**
     * 根据角色codes和部门信息查询用户信息
     * @param sysUserRequestDTO
     * @return
     */
    @PostMapping("findUsersByRoleCodesAndDeptCode")
    public ResponseResult<WorkflowUserPageDTO> findUsersByRoleCode(@RequestBody SysUserRequestDTO sysUserRequestDTO) {
        WorkflowUserPageDTO workflowUserPageDTO = new WorkflowUserPageDTO();
        List<String> roleCodes = sysUserRequestDTO.getRoleCodes();
        String orgCode = sysUserRequestDTO.getOrgCode();
        if (ObjectUtils.isEmpty(roleCodes)) {
            return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "角色编码不能为空!");
        }
        if (ObjectUtils.isEmpty(orgCode)) {
            return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "部门编码不能为空!");
        }
        ResultData<SysUser> currentUserResultData = authClient.currentUser();
        if (ObjectUtils.isEmpty(currentUserResultData.getResult().getCurrentCompanyId())) {
            return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "当前租户id为空!");
        }

        List<SysUser> usersByRoleCode = userService.findUsersByRoleCode(roleCodes);
        List<SysUserDTO> userDTOs = usersByRoleCode.stream()
                .map(user -> {
                    SysUserDTO dto = new SysUserDTO();
                    BeanUtils.copyProperties(user, dto); // 注意参数顺序：源在前，目标在后
                    return dto;
                })
                .collect(Collectors.toList());
        // 通过角色集合获取的用户去重
        Set<Long> seenIds = new HashSet<>();
        List<SysUserDTO> distinctUserListByRoles = userDTOs.stream()
                .filter(dto -> seenIds.add(dto.getUserId())) // 利用Set的add返回值判断是否已存在
                .collect(Collectors.toList());

        if (ObjectUtils.isEmpty(distinctUserListByRoles)) {
            return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "通过角色编码查询的用户为空!");
        }

        //查询当前租户下的当前当前部门用户
        List<SysUser> usersByOrgCode = userService.findUsersByOrgCode(orgCode, currentUserResultData.getResult().getCurrentCompanyId());
//        List<SysUser> usersByOrgCode = userService.findUsersByOrgCode(orgCode, 467L);
        if (ObjectUtils.isEmpty(usersByOrgCode)) {
            return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "通过部门编码和当前用户租户id查询的用户为空!");
        }
        // 转化DTO
        List<SysUserDTO> usersByOrgCodeDTOs = usersByOrgCode.stream()
                .map(user -> {
                    SysUserDTO dto = new SysUserDTO();
                    BeanUtils.copyProperties(user, dto); // 注意参数顺序：源在前，目标在后
                    return dto;
                })
                .collect(Collectors.toList());

        // 求distinctUserListByRoles和usersByOrgCodeDTOs集合交集
        Set<Long> userIds = usersByOrgCodeDTOs.stream()
                .map(SysUserDTO::getUserId)
                .collect(Collectors.toSet());

        List<SysUserDTO> finalUserList = distinctUserListByRoles.stream()
                .filter(user -> userIds.contains(user.getUserId()))
                .collect(Collectors.toList());

        if (ObjectUtils.isEmpty(finalUserList)) {
            return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "findUsersByRoleCode查询的用户为空!");
        }
        //返回封装fullname
        List<SysUserDTO> finalUserListWithFullName = new ArrayList<>();
        for(SysUserDTO sysUserDTO : finalUserList){
            SysOrgPerson sysOrgPerson = sysOrgPersonService.findById(sysUserDTO.getUserRelationId(), "zh-CN");
            sysUserDTO.setFullName(sysOrgPerson.getFullName());
            finalUserListWithFullName.add(sysUserDTO);
        }

        workflowUserPageDTO.setRows(finalUserListWithFullName);
        workflowUserPageDTO.setTotal((long) finalUserListWithFullName.size());
        return ResponseResult.success(workflowUserPageDTO);
    }

    /**
     * 根据用户ids查询角色编码
     * @param sysUserRequestDTO
     * @return
     */
    @PostMapping("findUserRolesByUserIdOrUserName")
    public List<SysUserRoleDTO> findUserRoleByUserIdOrUserName(@RequestBody SysUserRequestDTO sysUserRequestDTO){
        List<Long> userIdList = sysUserRequestDTO.getUserIdList();
        List<String> userNameList = sysUserRequestDTO.getUserNameList();
        if (ObjectUtils.isEmpty(userIdList) && ObjectUtils.isEmpty(userNameList)) {
            throw new ParamValidateException("用户id和用户名不能同时为空!");
        }
        return sysUserService.findUserRoleByUserIdOrUserName(userIdList,userNameList);
    }

    /**
     * 通过角色编码获取用户 roleCodes
     * @param sysUserRequestDTO
     * @return
     */
    @PostMapping("findUserByRoleCodes")
    public List<SysUser> findUserByRoleCodes(@RequestBody SysUserRequestDTO sysUserRequestDTO) {
        List<String> roleCodes = sysUserRequestDTO.getRoleCodes();
        if (ObjectUtils.isEmpty(roleCodes)) {
            throw new ParamValidateException("角色编码不能为空!");
        }
        return userService.findUsersByRoleCode(roleCodes);
    }

    /**
     * 通过姓名查询用户
     * @param sysUserRequestDTO
     * @return
     */
    @PostMapping("findUserByFullName")
    public List<SysUserDTO> findUserByFullName(@RequestBody SysUserRequestDTO sysUserRequestDTO) {
        String fullName = sysUserRequestDTO.getFullName();
        if (ObjectUtils.isEmpty(fullName)) {
            throw new ParamValidateException("姓名不能为空!");
        }
        ResultData<SysUser> currentUserResultData = authClient.currentUser();
        if (ObjectUtils.isEmpty(currentUserResultData.getResult().getCurrentCompanyId())) {
            throw new ParamValidateException("租户id不能为空!");
        }
        return userService.findUsersByfullName(fullName, currentUserResultData.getResult().getCurrentCompanyId());
    }

    /**
     * 根据角色编码和租户id获取用户信息
     * @param sysUserRequestDTO
     * @return
     */
    @PostMapping("findUserByRoleCodeAndCompanyId")
    public List<SysUserDTO> findUserByRoleCodeAndCompanyId(@RequestBody SysUserRequestDTO sysUserRequestDTO) {
        Long companyId = sysUserRequestDTO.getCompanyId();
        String roleCode = sysUserRequestDTO.getRoleCode();
        if (ObjectUtils.isEmpty(companyId)) {
            throw new ParamValidateException("companyId不能为空!");
        }
        if (ObjectUtils.isEmpty(roleCode)) {
            throw new ParamValidateException("roleCode不能为空!");
        }
        return userService.findUserByRoleCodeAndCompanyId(roleCode, companyId);
    }




    /**
     * 密码修改
     *
     * @param rawPwd     原始密码
     * @param newPwd     新密码
     * @param confirmPwd 确认密码
     */
    @AutoIdempotent
    @PostMapping("updatePassword")
    public void updatePassword(String rawPwd, String newPwd, String confirmPwd) {
        if (ObjectUtils.isEmpty(rawPwd)) {
            throw new ParamValidateException("原密码不能为空!");
        }
        if (ObjectUtils.isEmpty(newPwd)) {
            throw new ParamValidateException("新密码不能为空!");
        }
        if (ObjectUtils.isEmpty(confirmPwd)) {
            throw new ParamValidateException("确认密码不能为空!");
        }
        if (!newPwd.equals(confirmPwd)) {
            throw new ParamValidateException("两次密码不匹配!");
        }
        ResultData<SysUser> currentUserResultData = authClient.currentUser();
        if (!currentUserResultData.getSuccess()) {
            throw new BizException(currentUserResultData.getCode(), currentUserResultData.getMessage());
        }
        SysUser sysUser = currentUserResultData.getResult();
        //密码强度等级
        int strong = checkStrong(newPwd, sysUser.getUserName(), sysUser.getUserDescription());
        if (strong < PWD_STRONG) {
            throw new ParamValidateException("密码强度太低!");
        }
        SysUser user = userService.findById(sysUser.getUserId(), LanguageEnum.EN_US.getValue());
        PasswordEncoder bcpe = new BCryptPasswordEncoder();
        if (!bcpe.matches(rawPwd, user.getPassword())) {
            throw new ParamValidateException("原密码错误!");
        }
        String enPassword = bcpe.encode(newPwd);
        sysUser.setPassword(enPassword);
        sysUser.setPasswordUpdateDate(Calendar.getInstance().getTime());
        //更新密码过期时间
        //根据配置编码查询配置文件信息
        SysProfile sysProfile = profileService.findByCode("PASSWORD_LIFESPAN_DAY");
        //值来源类型： sql:SQL、fixed:固定值
        if (!ProfileSourceTypeEnum.FIXED.getValue().equals(sysProfile.getProfileSourceType())) {
            throw new BizException("该接口只查询[值来源类型=fixed]的配置文件!");
        }
        //密码默认有效期
        String passwordLifespanDay = sysProfile.getProfileSource();
        if (ObjectUtils.isEmpty(passwordLifespanDay)) {
            throw new BizException("未设置[用户密码有效期]");
        }
        Calendar calender = Calendar.getInstance();
        //加N天
        calender.add(Calendar.DATE, Integer.parseInt(passwordLifespanDay));
        int result = userService.updatePassword(sysUser);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
        //清空该用户登录信息
        authClient.clear(user.getUserName());
    }

    /**
     * 密码强度校验
     * 字符类型：数字、小写字母、大写字母、特殊字符
     * 0：非常弱(长度<8位)
     * 1：弱(长度>=8位，并且只存在一种字符)
     * 2：中(长度>=8位，并且由2种字符组成)
     * 3：强(长度>=8位，并且由3种字符组成)
     * 4：非常强(长度>=8位，并且由4种字符组成)
     *
     * @param password 密码
     * @return 密码强度
     */
    @PostMapping("passwordStrongCheck")
    public int passwordStrongCheck(String password) {
        if (ObjectUtils.isEmpty(password)) {
            throw new ParamValidateException("密码不能为空!");
        }
        ResultData<SysUser> currentUserResultData = authClient.currentUser();
        if (!currentUserResultData.getSuccess()) {
            throw new BizException(currentUserResultData.getCode(), currentUserResultData.getMessage());
        }
        SysUser sysUser = currentUserResultData.getResult();
        return checkStrong(password, sysUser.getUserName(), sysUser.getUserDescription());
    }

    /**
     * 根据用户ID集合查询用户信息
     *
     * @param userIds 用户ID，多个以逗号","拼接
     * @return 结果
     */
    @PostMapping("findByUserIds")
    public List<SysUser> findByUserIds(String userIds) {
        if (ObjectUtils.isEmpty(userIds)) {
            throw new ParamValidateException("userIds不能为空!");
        }
        return userService.findByUserIds(Arrays.stream(userIds.split(",")).map(Long::parseLong).collect(Collectors.toList()));
    }

    /**
     * 根据用户ID集合查询用户信息
     *
     * @param userIds 用户ID集合
     * @return 结果
     */
    @PostMapping("findByUserIdList")
    public List<SysUser> findByUserIdList(@RequestBody List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new ParamValidateException("userIds不能为空!");
        }
        return userService.findByUserIds(userIds);
    }


    @PostMapping("findByUserIdListWithFullName")
    public List<SysUserDTO> findByUserIdListWithFullName(@RequestBody SysUserRequestDTO sysUserRequestDTO) {
        List<Long> userIdList = sysUserRequestDTO.getUserIdList();
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new ParamValidateException("userIds不能为空!");
        }
        return userService.findByUserIdListWithFullName(userIdList);
    }

    /**
     * 密码错误次数保存
     *
     * @param errorUser 错误次数记录
     * @return 结果
     */
    @PostMapping("updateLoginError")
    public int updateLoginError(@RequestBody SysUser errorUser) {
        return userService.updateLoginError(errorUser);
    }

    /**
     * 切换角色
     *
     * @param roleId 角色ID
     */
    @AutoIdempotent
    @PostMapping("switchRole")
    public void switchRole(HttpServletRequest request, Long roleId) {
        if (ObjectUtils.isEmpty(roleId)) {
            throw new ParamValidateException("角色ID不能为空!");
        }
        switchRoleOrCompanyOrDept(request, roleId, null, null);
    }

    /**
     * 切换公司
     *
     * @param companyId 公司ID
     */
    @AutoIdempotent
    @PostMapping("switchCompany")
    public void switchCompany(HttpServletRequest request, Long companyId) {
        if (ObjectUtils.isEmpty(companyId)) {
            throw new ParamValidateException(" 公司ID不能为空!");
        }
        switchRoleOrCompanyOrDept(request, null, companyId, null);
    }

    /**
     * 切换部门
     *
     * @param deptId 部门ID
     */
    @AutoIdempotent
    @PostMapping("switchDept")
    public void switchDept(HttpServletRequest request, Long deptId) {
        if (ObjectUtils.isEmpty(deptId)) {
            throw new ParamValidateException(" 部门ID不能为空!");
        }
        switchRoleOrCompanyOrDept(request, null, null, deptId);
    }

    /**
     * 切换角色||公司||部门
     *
     * @param request   请求
     * @param roleId    角色ID
     * @param companyId 公司ID
     * @param deptId    部门ID
     */
    private void switchRoleOrCompanyOrDept(HttpServletRequest request, Long roleId, Long companyId, Long deptId) {
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //项目编码
        String projectCode = request.getHeader(Constant.X_PROJECT_CODE);
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //获取当前用户在当前项目下的角色列表
        List<SysRole> projectRoleList = roleController.findRoleListByCurrentUser(request);
        if (CollectionUtils.isEmpty(projectRoleList)) {
            throw new BizException("当前用户未分配[" + projectCode + "]项目的角色!");
        }
        //① 当前项目下的角色ID集合
        List<Long> roleIdList = projectRoleList.stream().map(SysRole::getRoleId).collect(Collectors.toList());
        //当前角色ID
        Long currentRoleId = currentUser.getCurrentRoleId();
        //当前公司ID
        Long currentCompanyId = currentUser.getCurrentCompanyId();
        //当前部门ID
        Long currentDeptId = currentUser.getCurrentDeptId();
        if (!ObjectUtils.isEmpty(roleId)) {
            //说明当前操作为角色切换
            if (!roleIdList.contains(roleId)) {
                //切换的角色ID不在①中
                throw new ParamValidateException("当前用户不存在此切换的角色!");
            }
            if (currentRoleId.equals(roleId)) {
                //切换的角色与当前角色一致
                throw new BizException("当前角色与切换角色一致，无需切换!");
            }
            //切换的角色与当前角色不一致，设置新角色为切换的角色
            currentUser.setCurrentRoleId(roleId);
        }
        //根据角色ID查询绑定的公司ID与部门ID集合
        List<SysRoleCompanyDeptDTO> companyDeptDtoList = roleController.findRoleCompanyDeptByRoleId(currentUser.getCurrentRoleId());
        if (CollectionUtils.isEmpty(companyDeptDtoList)) {
            throw new BizException("切换失败，角色ID[" + currentRoleId + "]未绑定公司");
        }
        //② 当前角色绑定的公司ID集合
        List<Long> companyIdList = companyDeptDtoList.stream().map(SysRoleCompanyDeptDTO::getCompanyId).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(companyId)) {
            //说明当前操作为公司切换
            if (!companyIdList.contains(companyId)) {
                //切换的公司ID不在②中
                throw new ParamValidateException("当前角色不存在此切换的公司!");
            }
            if (currentCompanyId.equals(companyId)) {
                //切换的公司与当前公司一致
                throw new BizException("当前公司与切换公司一致，无需切换!");
            }
            //切换的公司与当前公司不一致，设置新公司为切换的公司
            currentUser.setCurrentCompanyId(companyId);
        }
        if (!ObjectUtils.isEmpty(roleId) && !companyIdList.contains(currentUser.getCurrentCompanyId())) {
            //角色ID != null && 切换的公司ID不在②中，则随机取一个公司
            currentUser.setCurrentCompanyId(companyIdList.get(0));
        }
        //③ 当前公司绑定的部门ID集合
        List<Long> deptIdList = companyDeptDtoList.stream()
                .filter(o -> o.getCompanyId().equals(currentUser.getCurrentCompanyId()))
                .findFirst()
                .map(SysRoleCompanyDeptDTO::getDeptIdList)
                .orElse(null);
        if (!ObjectUtils.isEmpty(deptId)) {
            if (CollectionUtils.isEmpty(deptIdList)) {
                //没有可以切换的部门
                throw new BizException("当前公司没有可切换的部门，请刷新页面!");
            }
            //说明当前操作为部门切换
            if (!deptIdList.contains(deptId)) {
                //切换的部门ID不在③中
                throw new ParamValidateException("当前公司不存在此切换的部门!");
            }
            if (currentDeptId.equals(deptId)) {
                //切换的部门与当前部门一致
                throw new BizException("当前部门与切换部门一致，无需切换!");
            }
            //切换的部门与当前部门不一致，设置新部门为切换的部门
            currentUser.setCurrentDeptId(deptId);
        }
        if (CollectionUtils.isEmpty(deptIdList)) {
            //如果没有部门集合，则直接将当前部门置空
            currentUser.setCurrentDeptId(null);
        } else {
            //有部门集合，先判断当前部门ID是否在集合中，如果不在则随机取一个
            if (!deptIdList.contains(currentUser.getCurrentDeptId())) {
                currentUser.setCurrentDeptId(deptIdList.get(0));
            }
        }
        //更新用户缓存
        ResultData<Boolean> resultData = authClient.updateCurrentUser(projectCode, currentUser.getCurrentRoleId(), currentUser.getCurrentCompanyId(), currentUser.getCurrentDeptId());
        if (!resultData.getSuccess()) {
            throw new BizException(resultData.getCode(), resultData.getMessage());
        }
        if (!resultData.getResult()) {
            throw new BizException("切换失败");
        }
    }

    /**
     * 根据用户关联ID(员工ID/供应商ID/客户ID)查询用户信息
     *
     * @param relationIdList 用户关联ID(员工ID/供应商ID/客户ID)
     * @return 用户信息
     */
    @PostMapping("findByRelationIds")
    public List<SysUser> findByRelationIds(@RequestBody List<Long> relationIdList) {
        if (ObjectUtils.isEmpty(relationIdList)) {
            throw new ParamValidateException("personIdList不能为空!");
        }
        return userService.findByRelationIds(relationIdList);
    }

    /**
     * 根据用户名集合查询用户信息
     *
     * @param userNameList 用户名集合
     * @return 用户信息
     */
    @PostMapping("findByUserNameList")
    public List<SysUser> findByUserNameList(@RequestBody List<String> userNameList) {
        return userService.findByUserNameList(userNameList);
    }

    /**
     * 根据用户名查询用户信息(包含角色信息)
     *
     * @param userName 用户名
     * @param language 语言
     * @return 用户信息
     */
    @PostMapping("findRoleByUserName")
    public SysUser findRoleByUserName(String userName, String language) {
        return userService.findRoleByUserName(userName, language);
    }


    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param user      用户信息
     */
    private void verification(String operation, SysUser user) {
        if (ObjectUtils.isEmpty(user)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(user.getUserName())) {
            throw new ParamValidateException("用户名不能为空!");
        }
        if (OperationTypeEnum.ADD.getValue().equals(operation)) {
            SysUser sysUserResultData = userService.findByUserName(user.getUserName());
            if (!ObjectUtils.isEmpty(sysUserResultData)) {
                throw new ParamValidateException("用户名已存在!");
            }
            if (ObjectUtils.isEmpty(user.getPassword())) {
                throw new ParamValidateException("密码不能为空!");
            }
            if (user.getPassword().length() < 8) {
                throw new ParamValidateException("密码长度不能低于8位!");
            }
            //密码强度等级
            int strong = checkStrong(user.getPassword(), user.getUserName(), user.getUserDescription());
            if (strong < PWD_STRONG) {
                throw new ParamValidateException("密码强度太低!");
            }
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(user.getUserId())) {
                throw new ParamValidateException("用户ID不能为空!");
            }
            SysUser sysUser = userService.findByUserName(user.getUserName());
            if (!ObjectUtils.isEmpty(sysUser) && !sysUser.getUserId().equals(user.getUserId())) {
                throw new ParamValidateException("用户名已存在!");
            }
            if (!ObjectUtils.isEmpty(user.getPassword())) {
                if (checkStrong(user.getPassword(), user.getUserName(), user.getUserDescription()) < PWD_STRONG) {
                    throw new ParamValidateException("密码强度太低!");
                }
            }
        }
        if (ObjectUtils.isEmpty(user.getUserType())) {
            throw new ParamValidateException("用户类型不能为空!");
        }
        if (ObjectUtils.isEmpty(user.getDisableState())) {
            throw new ParamValidateException("状态不能为空!");
        }
        if (!EnableFlagEnum.contains(user.getDisableState())) {
            throw new ParamValidateException("状态错误!");
        }
        if (CollectionUtils.isEmpty(user.getRoleIds())) {
            throw new ParamValidateException("请选择角色!");
        }
        List<SysDataAuth> dataAuthList = user.getDataAuthList();
        if (!ObjectUtils.isEmpty(dataAuthList)) {
            if (!CollectionUtils.isEmpty(dataAuthList)) {
                for (SysDataAuth dataAuth : dataAuthList) {
                    List<SysDataAuthValue> authValueList = dataAuth.getDataAuthValueList();
                    if (CollectionUtils.isEmpty(authValueList)) {
                        throw new ParamValidateException("用户权限值不能为空!");
                    }
                }
            }
        }
        //校验一个人员只能绑定一个用户
        if (UserTypeEnum.EMPLOYEE.getValue().equals(user.getUserType()) && !ObjectUtils.isEmpty(user.getUserRelationId())) {
            //根据人员ID查询用户
            List<SysUser> userList = userService.findByRelationIds(Collections.singletonList(user.getUserRelationId()));
            if (!CollectionUtils.isEmpty(userList)) {
                if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                    throw new BizException("该人员已绑定用户");
                }
                if (OperationTypeEnum.UPDATE.getValue().equals(operation) && !userList.get(0).getUserId().equals(user.getUserId())) {
                    throw new BizException("该人员已绑定用户");
                }
            }
        }
    }

    /**
     * 辰致科技用户密码策略：
     * 1.密码组成字符包括以下四类:数字、大写字母、小写字母、特殊字符，必须包含三种字符及以上；
     * 2.密码长度为8至16位，密码不能包含账号。
     * 3.连续3位及以上数字不能连号(例如123、654)
     * 4.连续3位及以上字母不能连续(例如abc、cba)
     * 5.密码不能包含连续3个及以上相同字符(例如aaa、rrr)
     * 6.密码不能包含用户名、姓名大小写全拼
     *
     * @param pwd      密码
     * @param userName 用户名
     * @return 1:弱、2：中、3：强、4：非常强
     */
    private int checkStrong(String pwd, String userName, String fullName) {
        int flag = 0;
        if (ObjectUtils.isEmpty(pwd)) {
            return flag;
        }
        if (pwd.length() < 8) {
            throw new ParamValidateException("密码长度不能小于8位!");
        }
        if (pwd.length() > 16) {
            throw new ParamValidateException("密码长度不能大于16位!");
        }
        if (pwd.contains(userName)) {
            throw new ParamValidateException("密码不能包含账号!");
        }
        if (!ObjectUtils.isEmpty(fullName) && pwd.toLowerCase().contains(ChineseToPinyin.convertToHanYuPinyin(fullName).toLowerCase())) {
            throw new ParamValidateException("密码不能包含用户姓名大小写全拼!");
        }
        if (MyUtils.isContinuousDigits(pwd)) {
            throw new ParamValidateException("连续3位及以上(数字/字母)不能连号!");
        }
        if (pwd.matches(".*[0-9].*")) {
            //数字
            flag++;
        }
        if (pwd.matches(".*[a-z].*")) {
            //小写字母
            flag++;
        }
        if (pwd.matches(".*[A-Z].*")) {
            //大写字母
            flag++;
        }
        if (pwd.matches(".*[!@*#$\\-_()+=&￥.].*")) {
            //特殊字符
            flag++;
        }
        return flag;
    }

    /**
     * 下载导入模板
     */
    @GetMapping("download/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        UserExcelEntity excelEntity = new UserExcelEntity();
        excelEntity.setEndDate(new Date());
        excelEntity.setStartDate(new Date());
        excelEntity.setUserName("【此行删除】输入用户名");
        excelEntity.setDisableState("请输入（启用/禁用）");
        excelEntity.setRoleAuth("输入角色编码（多个用英文逗号" + "," + "隔开）");
        excelEntity.setTerminals("输入终端名称（多个用英文逗号" + "," + "隔开）");
        excelEntity.setUserType("输入用户类型");
        excelEntity.setUserDescription("输入用户描述");
        excelEntity.setUserRelation("输入工号");
        excelEntity.setPassword("输入用户密码");
        // 忽略字段列表
        HashSet<String> excludeColumnFiledNames = new HashSet<>();
        excludeColumnFiledNames.add("validateStatus");
        excludeColumnFiledNames.add("validateMsg");
        ExcelParams excelParams = new ExcelParams();
        excelParams.setExcludeColumnFiledNames(excludeColumnFiledNames);
        excelParams.setClazz(Collections.singletonList(UserExcelEntity.class));
        excelParams.setExportType(ExcelEnums.ExportEnum.NORMAL);
        excelParams.setExcelFileTitle("用户上传模板");
        excelParams.setSheetNames(Collections.singletonList("用户上传模板"));
        excelExport.exportExcel(Collections.singletonList(excelEntity), excelParams, response);
    }

    /**
     * 导入用户数据
     *
     * @param file 文件
     * @return 结果
     */
    @AutoIdempotent(seconds = 30)
    @PostMapping("/import/user")
    public ResultData<ExcelResultData<List<UserExcelEntity>>> importStation(MultipartFile file) {
        UserExcelListener listener = new UserExcelListener();
        ExcelParams excelParams = new ExcelParams();
        excelParams.setClazz(Collections.singletonList(UserExcelEntity.class));
        excelParams.setAnalysisEventListenerList(Collections.singletonList(listener));
        excelImport.importExcel(excelParams, file);
        List<UserExcelEntity> list = listener.list();
        if (CollectionUtils.isEmpty(list)) {
            throw new BizException("导入文件不能为空");
        }
        // 验证数据并构建
        List<SysUser> sysUsers = validateBuild(list);
        HashMap<List<SysUser>, List<UserExcelEntity>> map = new HashMap<>();
        map.put(sysUsers, list);
        //存入redis
        String redisKey = REDIS_PREFIX + idKeyGenerator.getId();
        String jsonStr = JSON.toJSONString(map);
        valueOperations.set(redisKey, jsonStr, 3600, TimeUnit.SECONDS);
        ExcelResultData<List<UserExcelEntity>> build = ExcelResultData.build(redisKey, list);
        // 验证是否有失败的数据
        List<String> validateStatus = list.stream().map(UserExcelEntity::getValidateStatus).filter(status -> StatusEnum.N.name().equals(status)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(validateStatus)) {
            return ResultData.success(build);
        }
        return ResultData.fail(ReturnCode.EXCEL_ERROR.getCode(), ReturnCode.EXCEL_ERROR.getMessage(), build);
    }

    /**
     * 校验并转换数据
     *
     * @param list 待校验数据
     * @return 转换后数据列表
     */
    private List<SysUser> validateBuild(List<UserExcelEntity> list) {
        StringBuilder validateMsg = new StringBuilder();
        List<String> userNames = list.stream().map(UserExcelEntity::getUserName).filter(StringUtils::hasText).collect(Collectors.toList());
        // 获取重复用户名
        List<String> repeatNames = userNames.stream().collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        // 获取分配角色
        List<String> roleCodes = list.stream().map(UserExcelEntity::getRoleAuth).filter(StringUtils::hasText).collect(Collectors.toList());
        List<SysRole> roleList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleCodes)) {
            Set<String> roleCodeList = new HashSet<>();
            roleCodes.forEach(roleCode -> {
                List<String> split = Arrays.asList(roleCode.split(","));
                roleCodeList.addAll(split);
            });
            // 获取角色信息
            List<SysRole> roleResult = roleService.findByCodes(new ArrayList<>(roleCodeList), LanguageEnum.ZH_CN.getValue());
            roleList.addAll(roleResult);
        }
        List<String> roleCodeList = roleList.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
        // 存放关联用户
        List<String> personNos = list.stream().map(UserExcelEntity::getUserRelation).filter(StringUtils::hasText).distinct().collect(Collectors.toList());
        List<SysOrgPerson> relationUsers = new ArrayList<>();
        // 存放数据库已存在用户
        List<SysUser> existsUsers = new ArrayList<>();
        // 每次查询900条
        int constantQuantity = 900;
        // 去重减少查询次数
        Set<String> userNameSet = new HashSet<>(userNames);
        // 设置共享request
        RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true);
        // 计算查询次数
        int userNameCount = (userNameSet.size() / constantQuantity);
        if (userNameSet.size() % constantQuantity != 0) {
            userNameCount += 1;
        }
        int personNoCount = (personNos.size() / constantQuantity);
        if (personNos.size() % constantQuantity != 0) {
            personNoCount += 1;
        }
        // 获取最大的查询次数
        int maxCount = Stream.of(personNoCount, userNameCount).max(Integer::compare).get();
        String[] mapKeys = {"user", "person"};
        int finalUserNameCount = userNameCount;
        int finalPersonNoCount = personNoCount;
        //自定义创建线程池对象
        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(maxCount);
        List<CompletableFuture<Boolean>> futures = IntStream.rangeClosed(1, maxCount)
                //构建查询参数
                .mapToObj(i -> {
                    Map<String, List<String>> map = new HashMap<>();
                    if (finalUserNameCount <= i) {
                        int startIndex = (i - 1) * constantQuantity;
                        int endIndex = i * constantQuantity;
                        if (finalUserNameCount == i) {
                            endIndex = userNameSet.size();
                        }
                        map.put(mapKeys[0], new ArrayList<>(userNameSet).subList(startIndex, endIndex));
                    }
                    if (finalPersonNoCount <= i) {
                        int startIndex = (i - 1) * constantQuantity;
                        int endIndex = i * constantQuantity;
                        if (finalPersonNoCount == i || CollUtil.isEmpty(personNos)) {
                            endIndex = personNos.size();
                        }
                        map.put(mapKeys[1], new ArrayList<>(personNos).subList(startIndex, endIndex));
                    }
                    return map;
                })
                //构建异步线程
                .map(map -> CompletableFuture.supplyAsync(() -> {
                    // 查询用户
                    if (map.containsKey(mapKeys[0])) {
                        List<SysUser> byUserNameList = userService.findByUserNameList(map.get(mapKeys[0]));
                        synchronized (existsUsers) {
                            existsUsers.addAll(byUserNameList);
                        }
                    }
                    // 查询人员
                    if (map.containsKey(mapKeys[1]) && CollUtil.isNotEmpty(map.get(mapKeys[1]))) {
                        List<SysOrgPerson> byJobNos = personService.findByJobNos(map.get(mapKeys[1]));
                        synchronized (relationUsers) {
                            relationUsers.addAll(byJobNos);
                        }
                    }
                    return true;
                }, myThreadPool).exceptionally(e -> {
                    e.getCause().printStackTrace();
                    return false;
                }))
                .collect(Collectors.toList());
        //等待线程全部处理完毕
        List<Boolean> resultList = CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[]{}))
                .thenApply(f -> futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .join();
        myThreadPool.shutdown();
        //判断是否存在异常
        Boolean threadResult = resultList.stream().filter(o -> !o).findFirst().orElse(true);
        if (Boolean.FALSE.equals(threadResult)) {
            //抛出异常
            throw new BizException(ReturnCode.RC500.getCode(), "查询失败,请重试!");
        }
        // 数据库已存在的用户名
        List<String> existsUserNames = existsUsers.stream().map(SysUser::getUserName).collect(Collectors.toList());
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (Boolean.FALSE.equals(userResultData.getSuccess())) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        PasswordEncoder bcpe = new BCryptPasswordEncoder();
        Date nowDate = new Date();
        List<SysUser> result = new ArrayList<>();
        list.forEach(excelEntity -> {
            SysUser user = new SysUser();
            BeanUtils.copyProperties(excelEntity, user);
            // 密文
            String enPassword = bcpe.encode(user.getPassword());
            user.setPassword(enPassword);
            // 设置创建人、创建时间
            user.setCreateBy(currentUser.getUserId());
            user.setCreateDate(nowDate);
            // 设置Id
            user.setUserId(idKeyGenerator.getId());
            // 验证用户名
            if (ObjectUtils.isEmpty(excelEntity.getUserName())) {
                validateMsg.append("【用户名为空】");
            } else {
                if (repeatNames.contains(excelEntity.getUserName())) {
                    validateMsg.append("【用户名输入重复】");
                }
                if (existsUserNames.contains(excelEntity.getUserName())) {
                    validateMsg.append("【用户名已存在】");
                }
            }
            // 验证密码
            if (ObjectUtils.isEmpty(excelEntity.getPassword())) {
                validateMsg.append("【密码为空】");
            }
            if (checkStrong(excelEntity.getPassword(), currentUser.getUserName(), null) < PWD_STRONG) {
                validateMsg.append("【密码强度太低】");
            }
            // 验证用户类型
            if (ObjectUtils.isEmpty(excelEntity.getUserType())) {
                validateMsg.append("【用户类型为空】");
            } else {
                UserTypeEnum userTypeEnum = Arrays.stream(UserTypeEnum.values()).filter(x -> x.getText().equals(excelEntity.getUserType())).findFirst().orElse(null);
                if (ObjectUtils.isEmpty(userTypeEnum)) {
                    validateMsg.append("【用户类型验证失败】");
                } else {
                    user.setUserType(userTypeEnum.getValue());
                }
            }
            // 验证关联用户
            if (StringUtils.hasText(excelEntity.getUserRelation())) {
                SysOrgPerson person = relationUsers.stream().filter(x -> x.getJobNo().equals(excelEntity.getUserRelation())).findFirst().orElse(null);
                if (ObjectUtils.isEmpty(person)) {
                    validateMsg.append("【关联用户验证失败】");
                } else {
                    user.setUserRelationId(person.getPersonId());
                }
            }
            // 验证生效日期
            if (ObjectUtils.isEmpty(excelEntity.getStartDate())) {
                validateMsg.append("【生效日期为空】");
            }
            // 验证状态
            if (ObjectUtils.isEmpty(excelEntity.getDisableState())) {
                validateMsg.append("【用户状态为空】");
            } else {
                UserStatusEnum userStatusEnum = Arrays.stream(UserStatusEnum.values()).filter(x -> x.getText().equals(excelEntity.getDisableState())).findFirst().orElse(null);
                if (ObjectUtils.isEmpty(userStatusEnum)) {
                    validateMsg.append("【用户状态验证失败】");
                } else {
                    user.setDisableState(userStatusEnum.getValue());
                }
            }
            // 验证用户描述
            if (ObjectUtils.isEmpty(excelEntity.getUserDescription())) {
                validateMsg.append("【用户描述为空】");
            } else if (excelEntity.getUserDescription().length() > 50) {
                validateMsg.append("【用户描述过长】");
            }
            // 验证角色授权
            if (ObjectUtils.isEmpty(excelEntity.getRoleAuth())) {
                validateMsg.append("【角色授权为空】");
            } else {
                List<String> currentRoleCodes = Arrays.asList(excelEntity.getRoleAuth().split(","));
                if (!roleCodeList.containsAll(currentRoleCodes)) {
                    validateMsg.append("【角色验证失败】");
                } else {
                    ArrayList<SysUserRole> currentRoleList = new ArrayList<>();
                    currentRoleCodes.forEach(roleCode -> {
                        SysRole sysRole = roleList.stream().filter(x -> x.getRoleCode().equals(roleCode)).findFirst().orElse(new SysRole());
                        SysUserRole userRole = new SysUserRole();
                        userRole.setId(idKeyGenerator.getId());
                        userRole.setUserId(user.getUserId());
                        userRole.setRoleId(sysRole.getRoleId());
                        userRole.setCreateBy(currentUser.getUserId());
                        userRole.setCreateDate(nowDate);
                        currentRoleList.add(userRole);
                    });
                    user.setUserRoleList(currentRoleList);
                }
            }
            excelEntity.setValidateStatus(StatusEnum.Y.name());
            if (validateMsg.length() > 0) {
                excelEntity.setValidateStatus(StatusEnum.N.name());
                excelEntity.setValidateMsg(validateMsg.toString());
                validateMsg.setLength(0);
            }
            result.add(user);
        });
        return result;
    }

    /**
     * 下载导入验证结果
     *
     * @param response 响应
     */
    @GetMapping("/download/data")
    public void downloadExportData(HttpServletResponse response, @RequestParam String redisKey) {
        String s = (String) valueOperations.get(redisKey);
        Map map = JSON.parseObject(s);
        assert map != null;
        List<UserExcelEntity> list = new ArrayList<>();
        Collection<List<JSON>> values1 = map.values();
        if (!CollectionUtils.isEmpty(values1)) {
            values1.forEach(values -> {
                values.forEach(value -> {
                    UserExcelEntity excelEntity = JSON.parseObject(value.toString(), UserExcelEntity.class);
                    list.add(excelEntity);
                });
            });

        }
        // excel 导出参数
        ExcelParams excelParams = new ExcelParams();
        excelParams.setClazz(Collections.singletonList(UserExcelEntity.class));
        excelParams.setExportType(ExcelEnums.ExportEnum.NORMAL);
        excelParams.setExcelFileTitle("用户导入验证结果下载");
        excelParams.setSheetNames(Collections.singletonList("用户导入验证结果下载"));
        try {
            excelExport.exportExcel(list, excelParams, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证数据入库
     *
     * @param redisKey redis缓存主键
     */
    @AutoIdempotent(seconds = 30)
    @GetMapping("/import/data")
    public void importData(@RequestParam String redisKey) {
        String s = (String) valueOperations.get(redisKey);
        Map map = JSON.parseObject(s);
        assert map != null;
        List<SysUser> list = new ArrayList<>();
        Collection<List<JSON>> values1 = map.keySet();
        if (!CollectionUtils.isEmpty(values1)) {
            values1.forEach(values -> values.forEach(value -> {
                SysUser sysUser = JSON.parseObject(value.toString(), SysUser.class);
                list.add(sysUser);
            }));

        }
        boolean result = userService.addList(list);
        if (!result) {
            throw new BizException("入库失败！");
        }
    }

    /**
     * 根据部门ID查询用户列表(兼容旧平台)
     *
     * @param deptid 部门ID
     */
    @GetMapping("get_dept_users")
    public List<SysUserCompatibilityDTO> getUserByDeptId(@RequestParam Long deptid) {
        if (ObjectUtils.isEmpty(deptid)) {
            throw new ParamValidateException("部门ID不能为空!");
        }
        //根据部门ID查询人员列表
        LambdaQueryWrapper<SysDeptPersonPO> dpQueryWrapper = new LambdaQueryWrapper<>();
        dpQueryWrapper.eq(SysDeptPersonPO::getDeptId, deptid);
        List<SysDeptPersonPO> deptPersonList = deptPersonService.list(dpQueryWrapper);
        if (CollectionUtils.isEmpty(deptPersonList)) {
            return null;
        }
        //人员ID集合
        List<Long> personIdList = deptPersonList.stream().map(SysDeptPersonPO::getPersonId).collect(Collectors.toList());
        //根据人员ID查询用户信息
        List<SysUser> userList = userService.findByRelationIds(personIdList);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        //查询人员信息
        List<SysOrgPerson> personList = personService.findByIds(personIdList);
        List<SysUserCompatibilityDTO> dtoList = new ArrayList<>();
        //属性设置
        for (SysUser user : userList) {
            SysUserCompatibilityDTO dto = new SysUserCompatibilityDTO();
            dto.setId(user.getUserId());
            dto.setUserName(user.getUserName());
            dto.setStatus("Y".equals(user.getDisableState()) ? 1 : 0);
            if (user.getPasswordUpdateDate() != null) {
                dto.setPasswordUpdateTime(LocalDateTime.ofInstant(user.getPasswordUpdateDate().toInstant(), ZoneId.systemDefault()));
            }
            //部门人员信息
            SysDeptPersonPO deptPerson = deptPersonList.stream().filter(o -> o.getPersonId().equals(user.getUserRelationId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(deptPerson)) {
                dto.setLeader(ObjectUtils.isEmpty(deptPerson.getParentPersonId()));
            }
            //关联人员信息
            SysOrgPerson person = personList.stream().filter(o -> o.getPersonId().equals(user.getUserRelationId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(person)) {
                dto.setNickName(person.getFullName());
                dto.setPhonenumber(person.getPhone());
                dto.setSex("M".equals(person.getGender()) ? 0 : 1);
                dto.setEmail(person.getEmail());
            }
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 根据用户ID查询用户信息(批量)
     *
     * @param ids 用户ID集合
     * @return 结果
     */
    @GetMapping("get_user_lists")
    public List<SysUserCompatibilityDTO> getUserList(@RequestParam List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new ParamValidateException("参数必填");
        }
        List<SysUser> userList = userService.findByUserIds(ids);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return setUserProperties(userList);
    }

    /**
     * 根据用户名查询用户信息(批量)
     *
     * @param usernames 用户名列表
     * @return 结果
     */
    @GetMapping("get_user_lists_by_user_name")
    public List<SysUserCompatibilityDTO> getUserListByUserName(@RequestParam List<String> usernames) {
        if (CollectionUtils.isEmpty(usernames)) {
            throw new ParamValidateException("参数必填");
        }
        List<SysUser> userList = userService.findByUserNameList(usernames);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return setUserProperties(userList);
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return 结果
     */
    @GetMapping("get_user")
    public UserGetRespVO getUser(@RequestParam Long id) {
        SysUser user = userService.findById(id, "zh-CN");
        if (Objects.isNull(user)) {
            return null;
        }
        List<SysUser> userList = Collections.singletonList(user);
        return setUserAndDMRProperties(userList).get(0);
    }

    /**
     * 分页查询用户信息
     *
     * @param pageNo   当前页码
     * @param pageSize 查询数据条数
     * @return 结果
     */
    @GetMapping("get_user_page")
    public Page<UserGetRespVO> getUserPage(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        SysUserFindByPageParam param = new SysUserFindByPageParam();
        param.setPage(pageNo);
        param.setPageSize(pageSize);
        PaginationGrid<SysUser> paginationGrid = userService.findByPage(param);
        List<SysUser> rows = paginationGrid.getRows();
        Page<UserGetRespVO> page = Page.of(paginationGrid.getPage(), paginationGrid.getPageSize());
        if (CollectionUtils.isEmpty(rows)) {
            return page;
        }
        page.setTotal(paginationGrid.getTotal());
        page.setRecords(setUserAndDMRProperties(rows));
        return page;
    }

    /**
     * 获取带角色的用户List
     * @param ids
     * @return
     */
    @PostMapping("get_users_with_role")
    public List<UserGetRespVO> getUsersWithRoles(@RequestBody List<Long> ids) {
        return userService.getUsersWithRoles(ids);
    }

    /**
     * 通过用户名称获取带角色的用户
     * @param username
     * @return
     */
    @PostMapping("/get_user_with_role_by_username")
    public UserGetRespVO getUsersWithRoles(@RequestBody String username) {
        return userService.getUserWithRolesByUsername(username);
    }

    /**
     * 设置用户的其他属性：是否部门领导、用户姓名、手机号码、性别、邮箱
     *
     * @param userList 用户集合
     * @return 结果
     */
    private List<SysUserCompatibilityDTO> setUserProperties(List<SysUser> userList) {
        //用户关联的人员ID
        List<Long> personIdList = userList.stream().filter(o -> UserTypeEnum.EMPLOYEE.getValue().equals(o.getUserType())).map(SysUser::getUserRelationId).collect(Collectors.toList());
        //根据人员ID查询人员信息
        List<SysOrgPerson> personList = personService.findByIds(personIdList);
        //根据人员ID查询部门人员
        List<SysDeptPersonPO> deptPersonList = deptPersonService.findByPersonId(personIdList);
        List<SysUserCompatibilityDTO> dtoList = new ArrayList<>();
        //属性设置
        for (SysUser user : userList) {
            SysUserCompatibilityDTO dto = new SysUserCompatibilityDTO();
            dto.setId(user.getUserId());
            dto.setUserName(user.getUserName());
            dto.setStatus("Y".equals(user.getDisableState()) ? 1 : 0);
            if (!ObjectUtils.isEmpty(user.getPasswordUpdateDate())) {
                dto.setPasswordUpdateTime(LocalDateTime.ofInstant(user.getPasswordUpdateDate().toInstant(), ZoneId.systemDefault()));
            }
            //部门人员信息
            SysDeptPersonPO deptPerson = deptPersonList.stream().filter(o -> o.getPersonId().equals(user.getUserRelationId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(deptPerson)) {
                dto.setLeader(ObjectUtils.isEmpty(deptPerson.getParentPersonId()));
            }
            //关联人员信息
            if (!CollectionUtils.isEmpty(personList)) {
                SysOrgPerson person = personList.stream().filter(o -> o.getPersonId().equals(user.getUserRelationId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(person)) {
                    dto.setNickName(person.getFullName());
                    dto.setPhonenumber(person.getPhone());
                    dto.setSex("M".equals(person.getGender()) ? 0 : 1);
                    dto.setEmail(person.getEmail());
                }
            }
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 设置用户的其他属性：是否部门领导、用户姓名、手机号码、性别、邮箱,公司部门与角色
     *
     * @param userList 用户集合
     * @return 结果
     */
    private List<UserGetRespVO> setUserAndDMRProperties(List<SysUser> userList) {
        //用户关联的人员ID
//        List<Long> personIdList = userList.stream().filter(o -> UserTypeEnum.EMPLOYEE.getValue().equals(o.getUserType())).map(SysUser::getUserRelationId).collect(Collectors.toList());
        List<Long> personIdList = userList.stream().map(SysUser::getUserRelationId).collect(Collectors.toList());
        //根据人员ID查询人员信息
        List<SysOrgPerson> personList = personService.findByIds(personIdList);
        //根据人员ID查询部门人员
        List<SysDeptPersonPO> deptPersonList = deptPersonService.findByPersonId(personIdList);
        List<UserGetRespVO> dtoList = new ArrayList<>();
        //属性设置
        for (SysUser user : userList) {
            UserGetRespVO dto = new UserGetRespVO();
            dto.setId(user.getUserId());
            dto.setUserName(user.getUserName());
            dto.setStatus("Y".equals(user.getDisableState()) ? "1" : "0");
            dto.setUserType(user.getUserType());
            if (!ObjectUtils.isEmpty(user.getPasswordUpdateDate())) {
                dto.setPasswordUpdateTime(LocalDateTime.ofInstant(user.getPasswordUpdateDate().toInstant(), ZoneId.systemDefault()));
            }
            //部门人员信息
            dto.setLeader(false);
            if (deptPersonList != null) {
                SysDeptPersonPO deptPerson = deptPersonList.stream().filter(o -> o.getPersonId().equals(user.getUserRelationId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(deptPerson)) {
                    dto.setLeader(ObjectUtils.isEmpty(deptPerson.getParentPersonId()));
                }
            }
            //关联人员信息
            if (!CollectionUtils.isEmpty(personList)) {
                SysOrgPerson person = personList.stream().filter(o -> o.getPersonId().equals(user.getUserRelationId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(person)) {
                    dto.setNickName(person.getFullName());
                    dto.setPhonenumber(person.getPhone());
                    dto.setSex("M".equals(person.getGender()) ? "0" : "1");
                    dto.setEmail(person.getEmail());
                }
            }
            // 关联角色
            List<UserGetRespVO.Role> roles = new ArrayList<>();
            roleService.findByUserId(user.getUserId(), "zh-CN").forEach(sysRole -> {
                UserGetRespVO.Role role = new UserGetRespVO.Role();
                role.setId(String.valueOf(sysRole.getRoleId()));
                role.setName(sysRole.getRoleName());
                role.setRoleKey(sysRole.getRoleCode());
                roles.add(role);
            });

            List<Long> deptIdList = deptPersonList.stream().map(SysDeptPersonPO::getDeptId).distinct().collect(Collectors.toList());
            List<Dept> deptList = deptService.findByDeptIds(deptIdList);
            // 关联部门
            List<UserGetRespVO.Dept> depts = new ArrayList<>();
            deptList.forEach(sysDept -> {
                // 过滤临时部门
                if (!("-".concat(String.valueOf(sysDept.getCompanyId()))).equals(sysDept.getOrgCode())) {
                    UserGetRespVO.Dept dept = new UserGetRespVO.Dept();
                    dept.setId(sysDept.getId());
                    dept.setCompanyId(sysDept.getCompanyId());
                    dept.setOrgCode(sysDept.getOrgCode());
                    dept.setDepartmentName(sysDept.getDepartmentName());
                    depts.add(dept);
                }
            });
            List<Long> companyIdList = deptList.stream().map(Dept::getCompanyId).distinct().collect(Collectors.toList());
            // 关联部门
            List<UserGetRespVO.Company> companys = new ArrayList<>();
            companyService.listByIds(companyIdList).forEach(sysCompany -> {
                UserGetRespVO.Company company = new UserGetRespVO.Company();
                company.setId(sysCompany.getId());
                company.setCompanyId(String.valueOf(sysCompany.getCompanyId()));
                company.setOrgCode(sysCompany.getOrgCode());
                company.setCompanyName(sysCompany.getCompanyName());
                companys.add(company);
            });
            dto.setRoles(roles);
            dto.setDepts(depts);
            dto.setCompanys(companys);
            dtoList.add(dto);
        }
        return dtoList;
    }
}

package com.crrc.sys.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.annotation.AuditLog;
import com.crrc.enums.LogType;
import com.crrc.enums.OperateType;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.*;
import com.crrc.sys.common.response.ResponseResult;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.excel.ExcelParams;
import com.crrc.sys.entity.excel.ExcelResultData;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.*;
import com.crrc.sys.entity.user.dto.SysUserDTO;
import com.crrc.sys.entity.user.dto.SysUserRequestDTO;
import com.crrc.sys.entity.user.dto.BatchAddResult;
import com.crrc.sys.entity.user.dto.WorkflowUserPageDTO;
import com.crrc.sys.enums.*;
import com.crrc.sys.excel.ExcelExport;
import com.crrc.sys.excel.ExcelImport;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.model.org.person.SysOrgPersonParamOfUserPageDTO;
import com.crrc.sys.model.role.SysRoleCompanyDeptDTO;
import com.crrc.sys.model.user.*;
import com.crrc.sys.model.user.role.SysUserRoleDTO;
import com.crrc.sys.service.*;
import com.crrc.sys.service.impl.SysUserValidServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysUserService userService;
    @Resource
    private SysUserValidService userValidService;
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
    @AuditLog(value = "用户信息分页查询NEW-审计日志测试", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_QUERY)
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
        userValidService.verification(OperationTypeEnum.ADD.getValue(), user);
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
     * 用户批量新增
     *
     * @param users 用户信息列表
     * @return 批量新增结果
     */
    @PostMapping("batchAdd")
    @Operation(summary = "用户批量新增")
    @Transactional
    public ResponseResult<BatchAddResult> batchAdd(@RequestBody List<SysUser> users) {
        // 1. 参数校验
        if (CollectionUtils.isEmpty(users)) {
            return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "用户列表不能为空!");
        }

        // 2. 获取当前用户
        SysUser currentUser = null;
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (userResultData != null && userResultData.getSuccess()) {
            currentUser = userResultData.getResult();
        } else {
            currentUser = new SysUser();
            currentUser.setUserId(-1L);
        }

        // 3. 密码编码器
        PasswordEncoder bcpe = new BCryptPasswordEncoder();
        Date nowDate = Calendar.getInstance().getTime();

        // 4. 预处理每个用户
        for (int i = 0; i < users.size(); i++) {
            SysUser user = users.get(i);
            try {
                // 4.1 基础校验
                userValidService.verification(OperationTypeEnum.ADD.getValue(), user);

                // 4.2 密码加密
                user.setPassword(bcpe.encode(user.getPassword()));

                // 4.3 设置角色信息
                List<SysRole> roleList = user.getRoleIds().stream().map(o -> {
                    SysRole role = new SysRole();
                    role.setRoleId(o);
                    return role;
                }).collect(Collectors.toList());
                user.setRoleList(roleList);

                // 4.4 设置主键
                user.setUserId(idKeyGenerator.getId());

                // 4.5 设置创建人
                user.setCreateBy(currentUser.getUserId());

                // 4.6 设置创建时间
                user.setCreateDate(nowDate);

                // 4.7 处理用户数据权限
                List<SysDataAuth> dataAuthList = user.getDataAuthList();
                if (!CollectionUtils.isEmpty(dataAuthList)) {
                    SysUser finalCurrentUser = currentUser;
                    dataAuthList.forEach(o -> {
                        // 主键
                        o.setAuthId(idKeyGenerator.getId());
                        // 权限类型：用户
                        o.setAuthType(DataAuthTypeEnum.USER.getValue());
                        // 用户 ID
                        o.setObjectId(user.getUserId());
                        // 设置创建人
                        o.setCreateBy(finalCurrentUser.getUserId());
                        // 设置创建时间
                        o.setCreateDate(nowDate);
                        o.getDataAuthValueList().forEach(data -> {
                            data.setAuthId(o.getAuthId());
                            data.setCreateBy(o.getCreateBy());
                            data.setCreateDate(o.getCreateDate());
                        });
                    });
                }
            } catch (ParamValidateException e) {
                String username = user.getUserName() != null ? user.getUserName() : "未知";
                String error = e.getErrorMsg() != null ? e.getErrorMsg() : "未知错误";
                return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "批量新增失败(用户" + username + "):" + error);
            } catch (BizException e) {
                String username = user.getUserName() != null ? user.getUserName() : "未知";
                String error = e.getErrorMsg() != null ? e.getErrorMsg() : "未知错误";
                return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "批量新增失败(用户" + username + "):" + error);
            } catch (Exception e) {
                String username = user.getUserName() != null ? user.getUserName() : "未知";
                String error = e.getMessage() != null ? e.getMessage() : "未知错误";
                return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), "批量新增失败(用户" + username + "):" + error);
            }
        }

        // 5. 批量入库（事务保护，全部成功或全部失败）
        int successCount = userService.addBatch(users);

        // 6. 返回成功结果
        BatchAddResult result = new BatchAddResult(users.size(), successCount, Collections.emptyList());
        return ResponseResult.success(result);
    }

    /**
     * 用户修改
     *
     * @param user 用户信息
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(@RequestBody SysUser user) {
        userValidService.verification(OperationTypeEnum.UPDATE.getValue(), user);
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
        //1.如果用户禁用状态==Y，则清空该用户的token缓存信息
        //2.如果修改了用户密码，则也需要清空
        if (EnableFlagEnum.Y.getValue().equals(user.getDisableState()) || !ObjectUtils.isEmpty(user.getPassword())) {
            //清空该用户登录信息
            authClient.clear(user.getUserName());
        }
    }

    /**
     * 更新供应商用户密码（根据用户 ID 或工号）
     *
     * @param userId   用户 ID（可选，与 jobNo 二选一）
     * @param jobNo    工号（可选，与 userId 二选一）
     * @param password 新密码
     */
    @AutoIdempotent
    @Transactional
    @PostMapping("updateSupplierPassword")
    public Boolean updateSupplierPassword(@RequestParam(required = false) Long userId,
                                          @RequestParam(required = false) String jobNo,
                                          @RequestParam(required = false) String password) {
        // 1. 参数校验：userId 和 jobNo 必须提供一个且只能提供一个
        if (ObjectUtils.isEmpty(userId) && ObjectUtils.isEmpty(jobNo)) {
            throw new ParamValidateException("用户 ID 和工号不能同时为空，请提供一个！");
        }
        if (!ObjectUtils.isEmpty(userId) && !ObjectUtils.isEmpty(jobNo)) {
            throw new ParamValidateException("用户 ID 和工号不能同时提供，请只提供一个！");
        }
        if (ObjectUtils.isEmpty(password)) {
            throw new ParamValidateException("新密码不能为空！");
        }

        // 2. 密码强度校验
        ResultData<SysUser> currentUserResultData = authClient.currentUser();
        if (!currentUserResultData.getSuccess()) {
            throw new BizException(currentUserResultData.getCode(), currentUserResultData.getMessage());
        }
        SysUser operator = currentUserResultData.getResult();
        int strong = userValidService.checkStrong(password, operator.getUserName(), operator.getUserDescription());
        if (strong < SysUserValidServiceImpl.PWD_STRONG) {
            throw new ParamValidateException("密码强度太低！");
        }

        // 3. 根据工号或用户 ID 确定目标用户
        SysUser targetUser;
        if (!ObjectUtils.isEmpty(jobNo)) {
            // 3.1 根据工号查询人员信息
            SysOrgPerson person = sysOrgPersonService.findByJobNo(jobNo);
            if (ObjectUtils.isEmpty(person)) {
                throw new BizException("工号不存在！");
            }
            // 3.2 根据人员 ID 查询用户（必须唯一）
            List<SysUser> users = userService.findByRelationIds(Collections.singletonList(person.getPersonId()));
            if (CollectionUtils.isEmpty(users)) {
                throw new BizException("该工号未绑定用户！");
            }
            if (users.size() > 1) {
                throw new BizException("该工号绑定了多个用户，无法唯一确认！");
            }
            targetUser = users.get(0);
        } else {
            // 3.3 根据用户 ID 查询用户
            targetUser = userService.findById(userId, LanguageEnum.ZH_CN.getValue());
            if (ObjectUtils.isEmpty(targetUser)) {
                throw new BizException("用户不存在！");
            }
        }

        // 4. 校验用户类型必须是供应商
        if (!UserTypeEnum.SUPPLIER.getValue().equals(targetUser.getUserType())) {
            throw new BizException("只能更新供应商类型的用户密码，当前用户类型为：" + targetUser.getUserType());
        }

        // 5. 密码加密
        PasswordEncoder bcpe = new BCryptPasswordEncoder();
        String enPassword = bcpe.encode(password);
        targetUser.setPassword(enPassword);
        targetUser.setPasswordUpdateDate(Calendar.getInstance().getTime());

        // 6. 更新密码
        int result = userService.updatePassword(targetUser);
        if (result <= 0) {
            throw new BizException("密码更新失败！");
        }
        // 7. 清空该用户登录信息
        authClient.clear(targetUser.getUserName());
        return true;
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
        List<Long> userIdList = Arrays.stream(userIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<SysUser> userList = userService.findByUserIds(userIdList);
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new BizException("用户不存在");
        }
        userIdList = userList.stream().map(SysUser::getUserId).collect(Collectors.toList());
        int result = userService.delete(userIdList);
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
        for (SysUser user : userList) {
            authClient.clear(user.getUserName());
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
     *
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
        for (SysUserDTO sysUserDTO : finalUserList) {
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
     *
     * @param sysUserRequestDTO
     * @return
     */
    @PostMapping("findUserRolesByUserIdOrUserName")
    public List<SysUserRoleDTO> findUserRoleByUserIdOrUserName(@RequestBody SysUserRequestDTO sysUserRequestDTO) {
        List<Long> userIdList = sysUserRequestDTO.getUserIdList();
        List<String> userNameList = sysUserRequestDTO.getUserNameList();
        if (ObjectUtils.isEmpty(userIdList) && ObjectUtils.isEmpty(userNameList)) {
            throw new ParamValidateException("用户id和用户名不能同时为空!");
        }
        return sysUserService.findUserRoleByUserIdOrUserName(userIdList, userNameList);
    }

    /**
     * 通过角色编码获取用户 roleCodes
     *
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
     *
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
     * 根据用户部门id和companyId和角色编码信息 查询用户
     *
     * @param sysUserRequestDTO
     * @return
     */
    @PostMapping("findUserByRoleCode")
    public List<SysUserDTO> findUserByRoleCode(@RequestBody SysUserRequestDTO sysUserRequestDTO) {
        Long companyId = sysUserRequestDTO.getCompanyId();
        String roleCode = sysUserRequestDTO.getRoleCode();
        Long deptId = sysUserRequestDTO.getDeptId();
        if (ObjectUtils.isEmpty(companyId)) {
            throw new ParamValidateException("companyId不能为空!");
        }
        if (ObjectUtils.isEmpty(roleCode)) {
            throw new ParamValidateException("roleCode不能为空!");
        }
        if (ObjectUtils.isEmpty(deptId)) {
            throw new ParamValidateException("deptId不能为空!");
        }
        return userService.findUserByRoleCode(roleCode, companyId, deptId);
    }

    /**
     * 根据公司/部门/角色（均可多个）查询用户，支持用户名/用户描述/工号模糊查询（服务端分页）
     * 公司/部门/角色至少一个必填
     * @param sysUserRequestDTO 查询条件
     * @return 分页用户信息
     */
    @PostMapping("findUsersByMultiCondition")
    public PaginationGrid<SysUserDTO> findUsersByMultiCondition(@RequestBody SysUserRequestDTO sysUserRequestDTO) {
        boolean companyEmpty = CollectionUtils.isEmpty(sysUserRequestDTO.getCompanyIds())
                && CollectionUtils.isEmpty(sysUserRequestDTO.getCompanyOrgCodes());
        boolean deptEmpty = CollectionUtils.isEmpty(sysUserRequestDTO.getDeptIds())
                && CollectionUtils.isEmpty(sysUserRequestDTO.getDeptOrgCodes());
        boolean roleEmpty = CollectionUtils.isEmpty(sysUserRequestDTO.getRoleCodes());
        if (companyEmpty && deptEmpty && roleEmpty) {
            throw new ParamValidateException("公司/部门/角色编码至少填写一个!");
        }
        return userService.findUsersByMultiCondition(sysUserRequestDTO);
    }


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
        int strong = userValidService.checkStrong(newPwd, sysUser.getUserName(), sysUser.getUserDescription());
        if (strong < SysUserValidServiceImpl.PWD_STRONG) {
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
        if (ObjectUtils.isEmpty(sysProfile)) {
            throw new BizException("配置文件[PASSWORD_LIFESPAN_DAY]不存在");
        }
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
        sysUser.setPasswordExpireDate(calender.getTime());
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
        return userValidService.checkStrong(password, sysUser.getUserName(), sysUser.getUserDescription());
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

    @GetMapping("/export")
    @AuditLog(value = "导出用户信息", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出用户信息")
    public void exportSysPerson(SysUserFindByPageParam exportReqVO, HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("用户信息-" + new SimpleDateFormat("yyyyMMddhhmmss")
                        .format(System.currentTimeMillis()), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        exportReqVO = (exportReqVO == null) ? new SysUserFindByPageParam() : exportReqVO;
        List<SysUser> userList = userService.findList(exportReqVO);
        List<UserDeliveryExcelVO> userExcelVOS = transUserToExcelVOS(userList);
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), UserDeliveryExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("用户信息")
                .doWrite(userExcelVOS);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("download/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        UserExcelEntity excelEntity = new UserExcelEntity();
//        excelEntity.setEndDate(new Date());
//        excelEntity.setStartDate(new Date());
        excelEntity.setUserName("【此行删除】输入用户名");
        excelEntity.setDisableState("请输入（启用/禁用）");
        excelEntity.setRoleAuth("输入角色编码（多个用英文逗号" + "," + "隔开）");
//        excelEntity.setTerminals("输入终端名称（多个用英文逗号" + "," + "隔开）");
        excelEntity.setUserType("输入用户类型(员工/供应商/客户)");
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
        List<SysUser> sysUsers = userValidService.validateBuild(list);
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
            return new ArrayList<>();
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
            return new ArrayList<>();
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
     *
     * @param ids
     * @return
     */
    @PostMapping("get_users_with_role")
    public List<UserGetRespVO> getUsersWithRoles(@RequestBody List<Long> ids) {
        return userService.getUsersWithRoles(ids);
    }

    /**
     * 通过用户名称获取带角色的用户
     *
     * @param username
     * @return
     */
    @PostMapping("/get_user_with_role_by_username")
    public UserGetRespVO getUsersWithRoles(@RequestBody String username) {
        return userService.getUserWithRolesByUsername(username);
    }

    /**
     * 通过人员信息获取其下创建的用户（分页）
     *
     * @param param
     * @return
     */
    @PostMapping("/get_user_page_with_person_inform")
    public Page<UserGetRespVO> getUserPageWithPersonInform(@RequestBody SysOrgPersonParamOfUserPageDTO param) {
        param = param == null ? new SysOrgPersonParamOfUserPageDTO() : param;
        PaginationGrid<SysUser> paginationGrid = userService.getUserPageWithPersonInform(param);
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
     * 通过用户信息获取其下创建的用户（分页）
     *
     * @param param
     * @return
     */
    @PostMapping("/get_user_page_with_user_inform")
    public Page<UserGetRespVO> getUserPageWithUserInform(@RequestBody SysUserParamDTO param) {
        param = param == null ? new SysUserParamDTO() : param;
        SysUserFindByPageParam findByPageParam = new SysUserFindByPageParam();
        findByPageParam.setPage(param.getPageNo());
        findByPageParam.setPageSize(param.getPageSize());
        if (StringUtils.hasText(param.getUserName())) {
            findByPageParam.setUserName(param.getUserName());
        }
        if (StringUtils.hasText(param.getNickName())) {
            findByPageParam.setUserDescription(param.getNickName());
        }
//        PaginationGrid<SysUser> paginationGrid = userService.findByFuzzyAndDeptPage(findByPageParam, param.getDeptIdList());
//        List<SysUser> rows = paginationGrid.getRows();
//        Page<UserGetRespVO> page = Page.of(paginationGrid.getPage(), paginationGrid.getPageSize());
//        if (CollectionUtils.isEmpty(rows)) {
//            return page;
//        }
//        page.setTotal(paginationGrid.getTotal());
//        page.setRecords(setUserAndDMRProperties(rows));
        IPage<SysUser> minPage = userService.findByFuzzyAndDeptPage(findByPageParam, param.getDeptIdList());
        List<SysUser> rows = minPage.getRecords();
        Page<UserGetRespVO> resultPage = Page.of(minPage.getCurrent(), minPage.getSize());
        if (CollectionUtils.isEmpty(rows)) {
            return resultPage;
        }
        resultPage.setTotal(minPage.getTotal());
        resultPage.setRecords(setUserAndDMRProperties(rows));
        return resultPage;
    }

    /**
     * 通过鉴权信息获取其下存在的用户
     * 角色列表不为空且数据权限映射为空，则查找拥有角色的用户
     * 角色列表为空且数据权限映射不为空，则查找拥有数据权限的用户（只查找设置在用户上的数据权限）
     * 角色列表不为空且数据权限映射不为空，则在给定角色下查找拥有数据权限的人员（设置在用户和角色上的权限都查找）
     *
     * @param param
     * @return
     */
    @PostMapping("/get_user_with_authentication_inform")
    public List<UserGetRespVO> getUserWithAuthenticationInform(@RequestBody SysUserAuthDTO param) {
        param = param == null ? new SysUserAuthDTO() : param;
        List<String> roleCodeList = param.getRoleCodeList();
        AuthInformDTO authInform = param.getAuthInform();
        Boolean isEmptyRole = roleCodeList == null || roleCodeList.isEmpty();
        Boolean isEmptyAuth = authInform == null || authInform.getData().isEmpty();
        if (!isEmptyRole && isEmptyAuth) {
            // 角色列表不为空且数据权限映射为空，则查找拥有角色的用户
            return setUserAndDMRProperties(userService.findUsersByRoleCode(roleCodeList));
        } else if (isEmptyRole && !isEmptyAuth) {
            // 角色列表为空且数据权限映射不为空，则查找拥有数据权限的用户（只查找设置在用户上的数据权限）
            return setUserAndDMRProperties(userService.findUsersByDataAuth(authInform));
        } else if (!isEmptyRole && !isEmptyAuth) {
            // 角色列表不为空且数据权限映射不为空，则在给定角色下查找拥有数据权限的人员（设置在用户和角色上的权限都查找）
            return setUserAndDMRProperties(userService.getUserWithAuthenticationInform(roleCodeList, authInform));
        }
        return Collections.emptyList();
    }

    /**
     * 设置用户的其他属性：是否部门领导、用户姓名、手机号码、性别、邮箱
     *
     * @param userList 用户集合
     * @return 结果
     */
    private List<SysUserCompatibilityDTO> setUserProperties(List<SysUser> userList) {
        //用户关联的人员ID
//        List<Long> personIdList = userList.stream().filter(o -> UserTypeEnum.EMPLOYEE.getValue().equals(o.getUserType())).map(SysUser::getUserRelationId).collect(Collectors.toList());
        List<Long> personIdList = userList.stream().map(SysUser::getUserRelationId).collect(Collectors.toList());
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
            if (!companyIdList.isEmpty()) {
                companyService.listByIds(companyIdList).forEach(sysCompany -> {
                    UserGetRespVO.Company company = new UserGetRespVO.Company();
                    company.setId(sysCompany.getId());
                    company.setCompanyId(String.valueOf(sysCompany.getCompanyId()));
                    company.setOrgCode(sysCompany.getOrgCode());
                    company.setCompanyName(sysCompany.getCompanyName());
                    companys.add(company);
                });
            }

            dto.setRoles(roles);
            dto.setDepts(depts);
            dto.setCompanys(companys);
            dtoList.add(dto);
        }
        return dtoList;
    }

    private List<UserDeliveryExcelVO> transUserToExcelVOS(List<SysUser> userList) {
        // 使用并行流处理
        return userList.parallelStream().map(user -> {
            UserDeliveryExcelVO excelVO = new UserDeliveryExcelVO();
            excelVO.setUserName(user.getUserName());
            excelVO.setUserDescription(user.getUserDescription());
            if (UserTypeEnum.EMPLOYEE.getValue().equals(user.getUserType())) {
                excelVO.setUserType(UserTypeEnum.EMPLOYEE.getText());
            } else if (UserTypeEnum.SUPPLIER.getValue().equals(user.getUserType())) {
                excelVO.setUserType(UserTypeEnum.SUPPLIER.getText());
            } else if (UserTypeEnum.CUSTOMER.getValue().equals(user.getUserType())) {
                excelVO.setUserType(UserTypeEnum.CUSTOMER.getText());
            } else {
                excelVO.setUserType("未知");
            }
            if (UserStatusEnum.N.getValue().equals(user.getDisableState())) {
                excelVO.setDisableState(UserStatusEnum.N.getText());
            } else if (UserStatusEnum.Y.getValue().equals(user.getDisableState())) {
                excelVO.setDisableState(UserStatusEnum.Y.getText());
            } else {
                excelVO.setDisableState("未知");
            }
            if (user.getUserRelationId() != null) {
                String jobNo = personService.findById(user.getUserRelationId(), LanguageEnum.ZH_CN.getValue()).getJobNo();
                excelVO.setUserRelation(jobNo);
            }
            String roleCodeList = roleService.findByUserId(user.getUserId(), LanguageEnum.ZH_CN.getValue())
                    .stream().map(SysRole::getRoleCode).collect(Collectors.joining(","));
            excelVO.setRoleAuth(roleCodeList);
            return excelVO;
        }).collect(Collectors.toList());
    }
}

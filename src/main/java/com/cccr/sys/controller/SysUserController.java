package com.cccr.sys.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.cccr.sys.client.AuthClient;
import com.cccr.sys.common.*;
import com.cccr.sys.entity.user.*;
import com.cccr.sys.enums.*;
import com.cccr.sys.excel.ExcelExport;
import com.cccr.sys.excel.ExcelImport;
import com.cccr.sys.annotation.AutoIdempotent;
import com.cccr.sys.service.*;
import com.cccr.sys.config.exception.BizException;
import com.cccr.sys.config.exception.ParamValidateException;
import com.cccr.sys.entity.auth.SysDataAuth;
import com.cccr.sys.entity.auth.SysDataAuthValue;
import com.cccr.sys.entity.excel.ExcelParams;
import com.cccr.sys.entity.excel.ExcelResultData;
import com.cccr.sys.entity.org.SysOrg;
import com.cccr.sys.entity.org.person.SysOrgPerson;
import com.cccr.sys.entity.profile.SysProfile;
import com.cccr.sys.entity.role.SysRole;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private SysOrgService orgService;
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

    public static void main(String[] args) {
        //PasswordEncoder bcpe = new BCryptPasswordEncoder();
        //System.out.println(bcpe.encode("111111"));
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
        //根据用户ID查询组织列表
        List<SysOrg> userOrgList = orgService.findByRoleId(userId, language);
        if (!CollectionUtils.isEmpty(userOrgList)) {
            List<Long> userOrgIdList = userOrgList.stream().map(SysOrg::getOrgId).collect(Collectors.toList());
            sysUser.setOrgIdList(userOrgIdList);
        }
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
        SysUser sysUser = userService.findByUserName(userName);
        if (ObjectUtils.isEmpty(sysUser)) {
            return sysUser;
        }
        //组织查询
        List<SysOrg> orgList = orgService.findByRoleId(sysUser.getUserId(), LanguageEnum.ZH_CN.getValue());
        if (!CollectionUtils.isEmpty(orgList)) {
            List<Long> orgIdList = orgList.stream().map(SysOrg::getOrgId).collect(Collectors.toList());
            sysUser.setOrgIdList(orgIdList);
        }
        return sysUser;
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
    public void switchRole(Long roleId, String language) {
        if (ObjectUtils.isEmpty(roleId)) {
            throw new ParamValidateException("角色ID不能为空!");
        }
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //当前角色ID
        Long currentRoleId = currentUser.getCurrentRoleId();
        //当前组织ID
        Long currentOrgId = currentUser.getCurrentOrgId();
        if (currentRoleId.equals(roleId)) {
            throw new ParamValidateException("当前角色与切换角色一致，无需切换!");
        }
        //根据角色ID查询角色组织列表
        List<SysOrg> roleOrgList = orgService.findByRoleId(roleId, language);
        if (CollectionUtils.isEmpty(roleOrgList)) {
            throw new BizException("当前角色下无组织!");
        }
        //角色组织ID集合
        List<Long> roleOrgIdList = roleOrgList.stream().map(SysOrg::getOrgId).collect(Collectors.toList());
        //根据用户ID查询用户组织列表
        List<SysOrg> userOrgList = orgService.findByRoleId(currentUser.getUserId(), language);
        //如果用户未绑定组织，那就直接取角色组织；如果用户有绑定组织，则与角色组织取交集。
        if (!CollectionUtils.isEmpty(userOrgList)) {
            List<Long> userOrgIdList = userOrgList.stream().map(SysOrg::getOrgId).collect(Collectors.toList());
            roleOrgIdList = roleOrgIdList.stream().filter(userOrgIdList::contains).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(roleOrgIdList)) {
            throw new BizException("当前用户与所选角色无组织交集!");
        }
        if (!roleOrgIdList.contains(currentOrgId)) {
            //判断组织ID集合中不存在当前组织ID，则随机取一个组织ID
            currentOrgId = roleOrgIdList.get(0);
        }
        ResultData<Boolean> resultData = authClient.updateCurrentUser(roleId, currentOrgId);
        if (!resultData.getSuccess()) {
            throw new BizException(resultData.getCode(), resultData.getMessage());
        }
        if (!resultData.getResult()) {
            throw new BizException("切换失败");
        }
    }

    /**
     * 切换组织
     *
     * @param orgId 组织ID
     */
    @AutoIdempotent
    @PostMapping("switchOrg")
    public void switchOrg(Long orgId) {
        if (ObjectUtils.isEmpty(orgId)) {
            throw new ParamValidateException("组织ID不能为空!");
        }
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        if (currentUser.getCurrentOrgId().equals(orgId)) {
            throw new ParamValidateException("当前组织与切换组织一致，无需切换!");
        }
        ResultData<Boolean> resultData = authClient.updateCurrentUser(currentUser.getCurrentRoleId(), orgId);
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
}

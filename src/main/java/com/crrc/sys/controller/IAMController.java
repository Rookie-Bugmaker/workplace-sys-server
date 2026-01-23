package com.crrc.sys.controller;



import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.config.iam.IamProperties;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.SysUserRole;
import com.crrc.sys.enums.*;
import com.crrc.sys.enums.iam.IDMPushTypeEnum;
import com.crrc.sys.mapper.mapperImpl.SysUserRoleMapperImpl;
import com.crrc.sys.model.iam.IDMAccount;
import com.crrc.sys.model.iam.IDMResult;
import com.crrc.sys.service.ProjectService;
import com.crrc.sys.service.SysOrgPersonService;
import com.crrc.sys.service.SysRoleService;
import com.crrc.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * IAM-控制器
 *
 * @author ljt
 * @date 2026-01-21
 */
@RestController
@RequestMapping("iam")
@Slf4j
public class IAMController {
    @Resource
    private SysUserService userService;

    @Resource
    private SysOrgPersonService personService;

    @Resource
    private SysRoleService roleService;

    @Resource
    private ProjectService projectService;

    @Autowired
    private SysUserRoleMapperImpl userRoleMapper;

    @Resource
    private IdKeyGenerator idKeyGenerator;

    @Autowired
    private IamProperties iamProperties;


    /**
     * iam主推-用户新增
     *
     * @param idmAccount 账户信息
     */
    @PostMapping("/push/userAdd")
    @Transactional
    public IDMResult add(@RequestBody IDMAccount idmAccount, HttpServletRequest request) {
        // 信息校验
//        IDMResult tokenResult = verifyToken(request);
//        if("-1".equals(tokenResult.getCode())) {
//            return tokenResult;
//        }
        IDMResult accountResult = verifyAccount(idmAccount, IDMPushTypeEnum.ADD);
        if("-1".equals(accountResult.getCode())) {
            return accountResult;
        }

        // 查找推送账号对应的人员是否存在
        String jobNo = idmAccount.getApp_account__account_no();
        String projectCode = idmAccount.getResource_system();
        if(iamProperties.getProjectMap().containsKey(projectCode)){
            projectCode = iamProperties.getProjectMap().get(projectCode);
        }
        SysOrgPerson person = personService.findByJobNo(jobNo);
        if(person == null) {
            log.error("iam主推错误：用户新增异常（不存在账号为{}的员工，无法创建用户）", jobNo);
            return IDMResult.fail("用户新增异常（不存在账号为" + jobNo + "的员工，无法创建用户）");
        }

        // 检查用户是否存在
        List<SysUser> userList = userService.findByRelationIds(Arrays.asList(person.getPersonId()));
        if(userList == null || userList.size() == 0) {
            // 不存在则创建用户
            SysUser user = new SysUser();
            IDMResult userResult = transIDMAccountToUser(user, idmAccount, person);
            if("-1".equals(userResult.getCode())) {
                return userResult;
            }
            userService.add(user);
        } else {
            // 存在则获取
            SysUser user = userList.get(0);
            // 查看是否在对应项目存在角色
            ProjectEntity project = projectService.findByCode(projectCode);
            if(project == null) {
                log.error("iam主推错误：存在用户增加角色异常（项目编码为{}的项目不存在）", projectCode);
                return IDMResult.fail("存在用户增加角色异常（项目编码为" + projectCode + "的项目不存在）");
            }
            Long projectId = project.getId();
            List<Long> roleIdList = userRoleMapper.findRoleIdByUserIdInProject(user.getUserId(), projectId);
            // 当前项目下没有角色时
            if(roleIdList == null || roleIdList.size() == 0) {
                SysUserRole userRole = new SysUserRole();
                IDMResult roleResult = addUserRoleRelationInProject(userRole, idmAccount, user, projectId);
                if("-1".equals(roleResult.getCode())) {
                    return roleResult;
                }
                userRoleMapper.insert(Arrays.asList(userRole));
            }
        }
        return IDMResult.success("账号推送成功");
    }

    private IDMResult transIDMAccountToUser(SysUser user, IDMAccount idmAccount, SysOrgPerson person) {
        // 用户必要信息：用户ID、用户名、用户密码、下次登录强制更新密码时间为N、用户类型为EMPLOYEE、关联人员、用户状态为N、关联默认角色
        String jobNo = idmAccount.getApp_account__account_no();
        String realName = idmAccount.getApp_account__account_name();
        Integer status = idmAccount.getApp_account__status();
        String projectCode = idmAccount.getResource_system();
        if(iamProperties.getProjectMap().containsKey(projectCode)){
            projectCode = iamProperties.getProjectMap().get(projectCode);
        }
        PasswordEncoder bcrypt = new BCryptPasswordEncoder();
        String enPassword = bcrypt.encode("Cccar@iam5901rj");

        user.setUserId(idKeyGenerator.getId());
        user.setUserName(jobNo);
        user.setUserDescription(realName);
        user.setPassword(enPassword);
        user.setNextLoginUpdatePassword("N");
        user.setUserType(UserTypeEnum.EMPLOYEE.getValue());
        user.setUserRelationId(person.getPersonId());
        user.setDisableState(status == 0 ? UserStatusEnum.Y.getValue(): UserStatusEnum.N.getValue());
        user.setCreateBy(-1L);
        user.setCreateDate(Calendar.getInstance().getTime());
        //关联角色
        ProjectEntity project = projectService.findByCode(projectCode);
        if(project == null) {
            log.error("iam主推错误：用户新增异常（项目编码为{}的项目不存在）", projectCode);
            return IDMResult.fail("用户新增异常（项目编码为" + projectCode + "的项目不存在）");
        }

        Long roleId = roleService.findDefaultRoleIdInProject(project.getId());
        if(roleId == null) {
            log.error("iam主推错误：用户新增异常（项目编码为{}的项目不存在默认角色）", projectCode);
            return IDMResult.fail("用户新增异常（项目编码为" + projectCode + "的项目不存在默认角色）");
        }
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        user.setRoleList(Arrays.asList(role));
        return IDMResult.success("");
    }

    private IDMResult addUserRoleRelationInProject(SysUserRole userRole, IDMAccount idmAccount, SysUser user, Long projectId) {
        String projectCode = idmAccount.getResource_system();
        if(iamProperties.getProjectMap().containsKey(projectCode)){
            projectCode = iamProperties.getProjectMap().get(projectCode);
        }
        Long roleId = roleService.findDefaultRoleIdInProject(projectId);
        // 不存在需要关联对应项目的默认角色
        if(roleId == null) {
            log.error("iam主推错误：存在用户增加角色异常（项目编码为{}的项目不存在默认角色）", projectCode);
            return IDMResult.fail("存在用户增加角色异常（项目编码为" + projectCode + "的项目不存在默认角色）");
        }

        userRole.setId(idKeyGenerator.getId());
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(roleId);
        userRole.setCreateBy(user.getCreateBy());
        userRole.setCreateDate(user.getCreateDate());
        return IDMResult.success("");
    }

    private IDMResult verifyAccount(IDMAccount idmAccount, IDMPushTypeEnum rightType) {
        if(idmAccount == null) {
            log.error("iam主推错误：账号信息异常（请求体为空）");
            return IDMResult.fail("账号信息异常（请求体为空）");
        }
        String jobNo = idmAccount.getApp_account__account_no();
        if(StringUtils.isBlank(jobNo)) {
            log.error("iam主推错误：账号信息异常（账号account_no为空）");
            return IDMResult.fail("账号信息异常（账号account_no为空）");
        }
        String realName = idmAccount.getApp_account__account_name();
        if(StringUtils.isBlank(realName)) {
            log.error("iam主推错误：账号信息异常（号名称account_name为空）");
            return IDMResult.fail("账号信息异常（账号名称account_name为空）");
        }
        Integer status = idmAccount.getApp_account__status();
        if(status == null) {
            log.error("iam主推错误：账号信息异常（账号状态status为空）");
            return IDMResult.fail("账号信息异常（账号状态status为空）");
        }
        String projectCode = idmAccount.getResource_system();
        if(StringUtils.isBlank(projectCode)) {
            log.error("iam主推错误：账号信息异常（应用编码resource_system为空）");
            return IDMResult.fail("账号信息异常（应用编码resource_system为空）");
        }
        Integer pushType = idmAccount.getRequest_log__action_flag();
        if(pushType == null){
            log.error("iam主推错误：下推状态异常（下推状态为空）");
            return IDMResult.fail("下推状态异常（下推状态为空）");
        } else if (rightType.getValue() != pushType) {
            log.error("iam主推错误：下推状态异常（需求下推状态为{}-{}，实际状态为{}-)",
                    rightType.getValue(), rightType.getText(), pushType);
            return IDMResult.fail("下推状态异常（需求下推状态为" + rightType.getValue()
                    + "-" + rightType.getText()  + "，实际状态为" + pushType + ")");
        }
        if(pushType == null){
            log.error("iam主推错误：下推状态异常（下推状态为空）");
            return IDMResult.fail("下推状态异常（下推状态为空）");
        } else if (rightType.getValue() != pushType) {
            log.error("iam主推错误：下推状态异常（需求下推状态为{}-{}，实际状态为{}-)",
                    rightType.getValue(), rightType.getText(), pushType);
            return IDMResult.fail("下推状态异常（需求下推状态为" + rightType.getValue()
                    + "-" + rightType.getText()  + "，实际状态为" + pushType + ")");
        }
        return IDMResult.success("");
    }

//    private IDMResult verifyToken(HttpServletRequest request) {
//        String token = request.getHeader("token");
//        if (StrUtil.isBlank(token)) {
//            log.error("iam主推错误：token认证失败（token为空）");
//            return IDMResult.fail("token认证失败（token为空）");
//        }
//        else if(token.startsWith("Bearer ")) {
//            try {
//                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(appSecret)).acceptIssuedAt(60).build();
//                jwtVerifier.verify(token.substring(7));
//            } catch (Exception e) {
//                log.error("iam主推错误：token认证失败（Jwt token校验失败）", e);
//                return IDMResult.fail("token认证失败（Jwt token校验失败）,报错信息为：" + e.getMessage());
//            }
//        } else if (token.startsWith("Basic ")) {
//            String secret = String.join(StrUtil.COLON, appKey, appSecret);
//            String createdToken = String.join(" ", "Basic", Base64.encode(secret, CharsetUtil.CHARSET_UTF_8));
//            if(!createdToken.equals(token)) {
//                log.error("iam主推错误：token认证失败（Basic token校验结果不相等）");
//                return IDMResult.fail("token认证失败（Basic token校验结果不相等）");
//            }
//        } else {
//            log.error("iam主推错误：token认证失败（token类型异常，不能识别）");
//            return IDMResult.fail("token认证失败（token类型异常，不能识别）");
//        }
//        return IDMResult.success("");
//    }
}

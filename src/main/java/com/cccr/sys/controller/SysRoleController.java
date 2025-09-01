package com.cccr.sys.controller;

import com.cccr.sys.client.AuthClient;
import com.cccr.sys.common.IdKeyGenerator;
import com.cccr.sys.common.PaginationGrid;
import com.cccr.sys.common.ResultData;
import com.cccr.sys.entity.role.*;
import com.cccr.sys.enums.DataAuthTypeEnum;
import com.cccr.sys.enums.EnableFlagEnum;
import com.cccr.sys.enums.LanguageEnum;
import com.cccr.sys.enums.OperationTypeEnum;
import com.cccr.sys.annotation.AutoIdempotent;
import com.cccr.sys.config.exception.BizException;
import com.cccr.sys.config.exception.ParamValidateException;
import com.cccr.sys.entity.auth.SysDataAuth;
import com.cccr.sys.entity.auth.SysDataAuthValue;
import com.cccr.sys.entity.org.SysOrg;
import com.cccr.sys.entity.profile.SysProfile;
import com.cccr.sys.entity.user.SysUser;
import com.cccr.sys.service.SysOrgService;
import com.cccr.sys.service.SysProfileService;
import com.cccr.sys.service.SysRoleService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理-控制器
 *
 * @author 谭永强
 * @date 2022-09-07
 */
@RestController
@RequestMapping("role")
public class SysRoleController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysRoleService roleService;
    @Resource
    private SysProfileService profileService;
    @Resource
    private SysOrgService orgService;

    /**
     * 角色信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysRole> findByPage(SysRoleFindByPageParam param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(param.getPage()) || param.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(param.getPageSize()) || param.getPageSize() < 0) {
            throw new ParamValidateException("pageSize不能为空且必须>=0");
        }
        if (ObjectUtils.isEmpty(param.getLanguage())) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(param.getLanguage())) {
            throw new ParamValidateException("language参数错误");
        }
        if (!ObjectUtils.isEmpty(param.getEnableFlag()) && !EnableFlagEnum.contains(param.getEnableFlag())) {
            throw new ParamValidateException("enableFlag参数错误");
        }
        return roleService.findByPage(param);
    }

    /**
     * 新增
     *
     * @param role 角色信息
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(@RequestBody SysRole role) {
        verification(OperationTypeEnum.ADD.getValue(), role);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        role.setRoleId(idKeyGenerator.getId());
        //创建人
        role.setCreateBy(currentUser.getUserId());
        //创建时间
        role.setCreateDate(Calendar.getInstance().getTime());
        //构建角色菜单数据(选中状态)
        List<SysRoleMenu> roleMenuList1 = role.getCheckedMenuIds().stream().map(o -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(role.getRoleId());
            roleMenu.setMenuId(o);
            roleMenu.setCreateBy(role.getCreateBy());
            roleMenu.setCreateDate(role.getCreateDate());
            return roleMenu;
        }).collect(Collectors.toList());
        //构建角色菜单数据(半选中状态)
        List<SysRoleMenu> roleMenuList2 = role.getHalfCheckedMenuIds().stream().map(o -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(role.getRoleId());
            roleMenu.setMenuId(o);
            roleMenu.setHalfChecked(true);
            roleMenu.setCreateBy(role.getCreateBy());
            roleMenu.setCreateDate(role.getCreateDate());
            return roleMenu;
        }).collect(Collectors.toList());
        roleMenuList1.addAll(roleMenuList2);
        role.setRoleMenuList(roleMenuList1);
        //构建角色组织数据
        if (!CollectionUtils.isEmpty(role.getCheckedOrgIds())) {
            List<SysRoleOrg> roleOrgList = role.getCheckedOrgIds().stream().map(o -> {
                SysRoleOrg roleOrg = new SysRoleOrg();
                roleOrg.setRoleId(role.getRoleId());
                roleOrg.setOrgId(o);
                roleOrg.setCreateBy(role.getCreateBy());
                roleOrg.setCreateDate(role.getCreateDate());
                return roleOrg;
            }).collect(Collectors.toList());
            role.setRoleOrgList(roleOrgList);
        }
        if (!CollectionUtils.isEmpty(role.getRoleColumnValueSets())) {
            role.getRoleColumnValueSets().forEach(o -> {
                o.setRoleId(role.getRoleId());
                o.setCreateBy(role.getCreateBy());
                o.setCreateDate(role.getCreateDate());
            });
        }
        //角色列权限数据
        if (!CollectionUtils.isEmpty(role.getCheckedFunctionColumns())) {
            role.getCheckedFunctionColumns().forEach(o -> {
                o.setRoleId(role.getRoleId());
                o.setCreateBy(role.getCreateBy());
                o.setCreateDate(role.getCreateDate());
            });
        }
        //角色数据权限
        List<SysDataAuth> dataAuthList = role.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            dataAuthList.forEach(o -> {
                //主键
                o.setAuthId(idKeyGenerator.getId());
                //权限类型:用户
                o.setAuthType(DataAuthTypeEnum.ROLE.getValue());
                //角色ID
                o.setObjectId(role.getRoleId());
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
        //角色新增
        int result = roleService.add(role);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 修改
     *
     * @param role     角色信息
     * @param language 语言
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(@RequestBody SysRole role, String language) {
        if (!ObjectUtils.isEmpty(role)) {
            role.setLanguage(language);
        }
        verification(OperationTypeEnum.UPDATE.getValue(), role);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //修改人
        role.setLastUpdateBy(currentUser.getUserId());
        //修改时间
        role.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改的登录信息ID
        role.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        //构建角色菜单数据(选中状态)
        List<SysRoleMenu> roleMenuList1 = role.getCheckedMenuIds().stream().map(o -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(role.getRoleId());
            roleMenu.setMenuId(o);
            roleMenu.setCreateBy(role.getLastUpdateBy());
            roleMenu.setCreateDate(role.getLastUpdateDate());
            return roleMenu;
        }).collect(Collectors.toList());
        //构建角色菜单数据(半选中状态)
        List<SysRoleMenu> roleMenuList2 = role.getHalfCheckedMenuIds().stream().map(o -> {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(role.getRoleId());
            roleMenu.setMenuId(o);
            roleMenu.setHalfChecked(true);
            roleMenu.setCreateBy(role.getLastUpdateBy());
            roleMenu.setCreateDate(role.getLastUpdateDate());
            return roleMenu;
        }).collect(Collectors.toList());
        roleMenuList1.addAll(roleMenuList2);
        role.setRoleMenuList(roleMenuList1);
        //构建角色组织数据
        if (!CollectionUtils.isEmpty(role.getCheckedOrgIds())) {
            List<SysRoleOrg> roleOrgList = role.getCheckedOrgIds().stream().map(o -> {
                SysRoleOrg roleOrg = new SysRoleOrg();
                roleOrg.setRoleId(role.getRoleId());
                roleOrg.setOrgId(o);
                roleOrg.setCreateBy(role.getLastUpdateBy());
                roleOrg.setCreateDate(role.getLastUpdateDate());
                return roleOrg;
            }).collect(Collectors.toList());
            role.setRoleOrgList(roleOrgList);
        }
        if (!CollectionUtils.isEmpty(role.getRoleColumnValueSets())) {
            role.getRoleColumnValueSets().forEach(o -> {
                o.setRoleId(role.getRoleId());
                o.setCreateBy(role.getLastUpdateBy());
                o.setCreateDate(role.getLastUpdateDate());
            });
        }
        //角色列权限数据
        if (!CollectionUtils.isEmpty(role.getCheckedFunctionColumns())) {
            role.getCheckedFunctionColumns().forEach(o -> {
                o.setRoleId(role.getRoleId());
                o.setCreateBy(role.getLastUpdateBy());
                o.setCreateDate(role.getLastUpdateDate());
            });
        }
        //角色数据权限
        List<SysDataAuth> dataAuthList = role.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            dataAuthList.forEach(o -> {
                //主键
                o.setAuthId(idKeyGenerator.getId());
                //权限类型:用户
                o.setAuthType(DataAuthTypeEnum.ROLE.getValue());
                //角色ID
                o.setObjectId(role.getRoleId());
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
        int result = roleService.update(role);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 删除
     *
     * @param roleIds 角色ID,多个以逗号","分隔
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) {
            throw new ParamValidateException("roleIds不能为空!");
        }
        List<Long> ids = Arrays.stream(roleIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        int result = roleService.delete(ids);
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 角色信息
     */
    @PostMapping("findById")
    public SysRole findById(Long roleId, String language) {
        if (ObjectUtils.isEmpty(roleId)) {
            throw new ParamValidateException("roleId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        SysRole sysRole = roleService.findById(roleId, language);
        if (ObjectUtils.isEmpty(sysRole)) {
            throw new BizException("角色不存在!");
        }
        //角色数据权限
        List<SysDataAuth> dataAuthList = sysRole.getDataAuthList();
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
        return sysRole;
    }

    /**
     * 根据角色编码查询角色信息
     *
     * @param roleCode 角色编码
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByCode")
    public SysRole findByCode(String roleCode, String language) {
        return roleService.findByCode(roleCode, language);
    }

    /**
     * 查询当前用户拥有的角色列表
     *
     * @param language 语言
     * @return 角色列表
     */
    @PostMapping("findRoleListByCurrentUser")
    public List<SysRole> findRoleListByCurrentUser(String language) {
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        return roleService.findByUserId(currentUser.getUserId(), language);
    }

    /**
     * 根据角色名称查询角色信息
     *
     * @param roleName 角色名称
     * @param language 语言
     * @return 角色信息
     */
    @PostMapping("findByName")
    public SysRole findByName(String roleName, String language) {
        return roleService.findByName(roleName, language);
    }

    /**
     * 根据编码集合查询角色信息
     *
     * @param roleCodeList 编码集合
     * @param language     语言
     * @return 结果
     */
    @PostMapping("/findByCodes")
    public List<SysRole> findByName(@RequestBody List<String> roleCodeList, @RequestParam String language) {
        return roleService.findByCodes(roleCodeList, language);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param role      角色信息
     */
    private void verification(String operation, SysRole role) {
        if (ObjectUtils.isEmpty(role)) {
            throw new ParamValidateException("参数错误");
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(role.getRoleId())) {
                throw new ParamValidateException("roleId不能为空");
            }
            if (ObjectUtils.isEmpty(role.getLanguage())) {
                throw new ParamValidateException("language不能为空");
            }
            if (!LanguageEnum.contains(role.getLanguage())) {
                throw new ParamValidateException("language参数错误");
            }
        }
        if (ObjectUtils.isEmpty(role.getRoleCode())) {
            throw new ParamValidateException("角色编码不能为空!");
        }
        if (role.getRoleCode().length() > 30) {
            throw new ParamValidateException("角色编码过长(最大30)!");
        }
        //根据角色编码查询角色信息
        SysRole resultData = roleService.findByCode(role.getRoleCode(), LanguageEnum.ZH_CN.getValue());
        if (!ObjectUtils.isEmpty(resultData)) {
            if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                throw new ParamValidateException("角色编码已存在!");
            } else {
                //根据角色ID查询角色信息
                SysRole resultIdData = roleService.findByIdMin(role.getRoleId(), role.getLanguage());
                if (!ObjectUtils.isEmpty(resultData) && !resultData.getRoleId().equals(resultIdData.getRoleId())) {
                    throw new ParamValidateException("角色编码已存在!");
                }
            }
        }
        if (ObjectUtils.isEmpty(role.getRoleName())) {
            throw new ParamValidateException("角色名称不能为空!");
        }
        if (role.getRoleName().length() > 30) {
            throw new ParamValidateException("角色名称过长(最大50)!");
        }
        //根据角色名称查询角色信息
        resultData = roleService.findByName(role.getRoleName(), role.getLanguage());
        if (!ObjectUtils.isEmpty(resultData)) {
            if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                throw new ParamValidateException("角色名称已存在!");
            } else {
                //根据角色ID查询角色信息
                SysRole resultIdData = roleService.findByIdMin(role.getRoleId(), role.getLanguage());
                if (!ObjectUtils.isEmpty(resultData) && !resultData.getRoleId().equals(resultIdData.getRoleId())) {
                    throw new ParamValidateException("角色名称已存在!");
                }
            }
        }
        if (ObjectUtils.isEmpty(role.getEnableFlag())) {
            throw new ParamValidateException("有效状态不能为空!");
        }
        if (!EnableFlagEnum.contains(role.getEnableFlag())) {
            throw new ParamValidateException("有效状态参数错误!");
        }
        if (ObjectUtils.isEmpty(role.getMenuId())) {
            throw new ParamValidateException("请选择菜单!");
        }
        if (ObjectUtils.isEmpty(role.getCheckedMenuIds())) {
            throw new ParamValidateException("请选择菜单权限!");
        }
        List<SysDataAuth> dataAuthList = role.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            for (SysDataAuth dataAuth : dataAuthList) {
                List<SysDataAuthValue> authValueList = dataAuth.getDataAuthValueList();
                if (CollectionUtils.isEmpty(authValueList)) {
                    throw new ParamValidateException("角色权限值不能为空!");
                }
            }
        }
        if (!ObjectUtils.isEmpty(role.getRoleDescription()) && role.getRoleDescription().length() > 80) {
            throw new ParamValidateException("描述过长(最大80)!");
        }
    }

    /**
     * 角色复制
     *
     * @param roleId 角色ID
     */
    @PostMapping("copy")
    public void copy(Long roleId) {
        if (ObjectUtils.isEmpty(roleId)) {
            throw new ParamValidateException("roleId不能为空!");
        }
        //新角色ID
        final long newRoleId = idKeyGenerator.getId();
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //当前用户ID
        long currentUserId = currentUser.getUserId();
        long lastLoginInfoId = currentUser.getLastLoginInfoId();
        //当前时间
        Date nowDate = Calendar.getInstance().getTime();
        //原角色信息
        SysRole role = roleService.findById(roleId, LanguageEnum.ZH_CN.getValue());
        //构建新角色信息
        //1.数据权限
        List<SysDataAuth> dataAuthList = role.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            dataAuthList.forEach(dataAuth -> {
                //数据权限对应的值列表
                List<SysDataAuthValue> authValueList = dataAuth.getDataAuthValueList();
                //修改数据权限ID
                dataAuth.setAuthId(idKeyGenerator.getId());
                //修改数据权限绑定的角色ID
                dataAuth.setObjectId(newRoleId);
                //创建人
                dataAuth.setCreateBy(currentUserId);
                //创建时间
                dataAuth.setCreateDate(nowDate);
                dataAuth.setLastUpdateLogin(lastLoginInfoId);
                //修改数据权限值绑定的数据权限ID
                authValueList.forEach(dataAuthValue -> {
                    dataAuthValue.setAuthId(dataAuth.getAuthId());
                    dataAuthValue.setCreateBy(currentUserId);
                    dataAuthValue.setCreateDate(nowDate);
                    dataAuthValue.setLastUpdateLogin(lastLoginInfoId);
                });
            });
        }
        //3.角色列权限
        List<SysRoleFunctionColumn> roleFunctionColumnList = role.getCheckedFunctionColumns();
        if (!CollectionUtils.isEmpty(roleFunctionColumnList)) {
            roleFunctionColumnList.forEach(o -> {
                o.setRoleId(newRoleId);
                o.setCreateBy(currentUserId);
                o.setCreateDate(nowDate);
                o.setLastUpdateLogin(lastLoginInfoId);
            });
        }
        //4.角色列权限值集
        List<SysRoleColumnValueSet> roleColumnValueSets = role.getRoleColumnValueSets();
        if (!CollectionUtils.isEmpty(roleColumnValueSets)) {
            roleColumnValueSets.forEach(o -> {
                o.setRoleId(newRoleId);
                o.setCreateBy(currentUserId);
                o.setCreateDate(nowDate);
                o.setLastUpdateLogin(lastLoginInfoId);
            });
        }
        //5.角色组织
        List<SysOrg> orgList = orgService.findByRoleId(roleId, LanguageEnum.ZH_CN.getValue());
        if (!CollectionUtils.isEmpty(orgList)) {
            List<SysRoleOrg> roleOrgList = orgList.stream().map(o -> {
                SysRoleOrg roleOrg = new SysRoleOrg(newRoleId, o.getOrgId());
                roleOrg.setCreateBy(currentUserId);
                roleOrg.setCreateDate(nowDate);
                roleOrg.setLastUpdateLogin(lastLoginInfoId);
                return roleOrg;
            }).collect(Collectors.toList());
            role.setRoleOrgList(roleOrgList);
        }
        //6.角色菜单
        List<SysRoleMenu> roleMenuList = role.getRoleMenuList();
        if (!CollectionUtils.isEmpty(roleMenuList)) {
            roleMenuList.forEach(o -> {
                o.setRoleId(newRoleId);
                o.setCreateBy(currentUserId);
                o.setCreateDate(nowDate);
                o.setLastUpdateLogin(lastLoginInfoId);
            });
        }
        //7.角色
        String nowRoleName = checkRoleName(role.getRoleName() + "_copy");
        role.setRoleName(nowRoleName);
        //校验角色编码
        String nowRoleCode = checkRoleCode(role.getRoleCode() + "_copy");
        role.setRoleCode(nowRoleCode);
        role.setRoleId(newRoleId);
        role.setCreateBy(currentUserId);
        role.setCreateDate(nowDate);
        role.setLastUpdateLogin(lastLoginInfoId);
        int result = roleService.add(role);
        if (result <= 0) {
            throw new BizException("复制失败!");
        }
    }

    /**
     * 校验角色编码唯一性，并返回新的编码
     *
     * @param roleCode 角色编码
     * @return 新的角色编码
     */
    private String checkRoleCode(String roleCode) {
        SysRole sysRole = roleService.findByCode(roleCode, LanguageEnum.ZH_CN.getValue());
        if (!ObjectUtils.isEmpty(sysRole)) {
            roleCode += "_copy";
            return checkRoleCode(roleCode);
        }
        return roleCode;
    }

    /**
     * 校验角色名称唯一性，并返回新的名称
     *
     * @param roleName 角色名称
     * @return 新的角色名称
     */
    private String checkRoleName(String roleName) {
        SysRole sysRole = roleService.findByName(roleName, LanguageEnum.ZH_CN.getValue());
        if (!ObjectUtils.isEmpty(sysRole)) {
            roleName += "_copy";
            return checkRoleName(roleName);
        }
        return roleName;
    }
}

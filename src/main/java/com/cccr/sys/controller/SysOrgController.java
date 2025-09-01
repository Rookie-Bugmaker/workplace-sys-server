package com.cccr.sys.controller;

import com.cccr.sys.client.AuthClient;
import com.cccr.sys.common.IdKeyGenerator;
import com.cccr.sys.common.PaginationGrid;
import com.cccr.sys.common.ResultData;
import com.cccr.sys.enums.EnableFlagEnum;
import com.cccr.sys.enums.LanguageEnum;
import com.cccr.sys.enums.OperationTypeEnum;
import com.cccr.sys.annotation.AutoIdempotent;
import com.cccr.sys.config.exception.BizException;
import com.cccr.sys.config.exception.ParamValidateException;
import com.cccr.sys.entity.org.SysOrg;
import com.cccr.sys.entity.org.SysOrgFindByPageParam;
import com.cccr.sys.entity.role.SysRole;
import com.cccr.sys.entity.role.SysRoleOrg;
import com.cccr.sys.entity.user.SysUser;
import com.cccr.sys.service.SysOrgService;
import com.cccr.sys.service.SysRoleService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织-控制器
 *
 * @author 谭永强
 * @date 2022-10-15
 */
@RestController
@RequestMapping("org")
public class SysOrgController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysOrgService orgService;
    @Resource
    private SysRoleService roleService;

    /**
     * 组织信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysOrg> findByPage(SysOrgFindByPageParam param) {
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
        if (!ObjectUtils.isEmpty(param.getOrgStatus()) && !EnableFlagEnum.contains(param.getOrgStatus())) {
            throw new ParamValidateException("状态参数错误!");
        }
        return orgService.findByPage(param);
    }

    /**
     * 组织新增
     *
     * @param org 组织
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(SysOrg org) {
        verification(OperationTypeEnum.ADD.getValue(), org);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        org.setOrgId(idKeyGenerator.getId());
        //创建人
        org.setCreateBy(currentUser.getUserId());
        //创建时间
        org.setCreateDate(Calendar.getInstance().getTime());
        int result = orgService.add(org);
        if (result <= 0) {
            throw new BizException("保存失败");
        }
    }

    /**
     * 组织修改
     *
     * @param org 组织
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysOrg org) {
        verification(OperationTypeEnum.UPDATE.getValue(), org);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //修改人
        org.setLastUpdateBy(currentUser.getUserId());
        //修改时间
        org.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改的登录信息ID
        org.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        int result = orgService.update(org);
        if (result <= 0) {
            throw new BizException("保存失败");
        }
    }

    /**
     * 根据当前角色查询可切换组织列表
     *
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByCurrentRole")
    public List<SysOrg> findByCurrentRole(String language) {
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        ResultData<SysUser> currentUserResultData = authClient.currentUser();
        SysUser currentUser = currentUserResultData.getResult();
        if (!currentUserResultData.getSuccess() || ObjectUtils.isEmpty(currentUser)) {
            throw new BizException("当前用户获取失败!");
        }
        if (ObjectUtils.isEmpty(currentUser.getCurrentRoleId())) {
            throw new BizException("当前用户无角色!");
        }
        List<SysOrg> roleOrgList = orgService.findByRoleId(currentUser.getCurrentRoleId(), language);
        if (ObjectUtils.isEmpty(currentUser.getCurrentRoleId())) {
            throw new BizException("当前角色无组织!");
        }
        ///根据用户ID查询用户组织列表
        List<SysOrg> userOrgList = orgService.findByRoleId(currentUser.getUserId(), language);
        //如果用户未绑定组织，那就直接取角色组织；如果用户有绑定组织，则与角色组织取交集。
        if (CollectionUtils.isEmpty(userOrgList)) {
            return roleOrgList;
        }
        List<Long> userOrgIdList = userOrgList.stream().map(SysOrg::getOrgId).collect(Collectors.toList());
        //获取用户组织与角色组织的交集
        roleOrgList = roleOrgList.stream().filter(o -> userOrgIdList.contains(o.getOrgId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleOrgList)) {
            SysRole sysRole = roleService.findByIdMin(currentUser.getCurrentRoleId(), language);
            throw new BizException("当前用户与当前角色[" + sysRole.getRoleName() + "]无组织交集!");
        }
        return roleOrgList;
    }

    /**
     * 根据(角色ID或用户)查询组织列表
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByRoleId")
    public List<SysOrg> findByRoleId(Long roleId, String language) {
        if (ObjectUtils.isEmpty(roleId)) {
            throw new ParamValidateException("roleId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        return orgService.findByRoleId(roleId, language);
    }

    /**
     * 根据组织ID查询组织
     *
     * @param orgId    组织ID
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findById")
    public SysOrg findById(Long orgId, String language) {
        if (ObjectUtils.isEmpty(orgId)) {
            throw new ParamValidateException("orgId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        return orgService.findById(orgId, language);
    }

    /**
     * 根据角色ID查询组织ID集合
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @PostMapping("findOrgIdListByRoleId")
    public List<Long> findOrgIdListByRoleId(long roleId) {
        List<SysOrg> orgList = orgService.findByRoleId(roleId, LanguageEnum.EN_US.getValue());
        if (!CollectionUtils.isEmpty(orgList)) {
            //筛选正常状态的组织ID
            return orgList.stream().filter(o -> "Y".equals(o.getOrgStatus())).map(SysOrg::getOrgId).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 根据角色ID查询组织ID集合(批量)
     *
     * @param roleIdList 角色ID集合
     * @return 结果
     */
    @PostMapping("findOrgIdListByRoleIds")
    public Map<Long, List<Long>> findOrgIdListByRoleIds(@RequestBody List<Long> roleIdList) {
        if (CollectionUtils.isEmpty(roleIdList)) {
            return null;
        }
        List<SysRoleOrg> roleOrgList = orgService.findByRoleIds(roleIdList);
        if (CollectionUtils.isEmpty(roleOrgList)) {
            return null;
        }
        Map<Long, List<Long>> map = new HashMap<>();
        Map<Long, List<SysRoleOrg>> roleOrgMap = roleOrgList.stream().collect(Collectors.groupingBy(SysRoleOrg::getRoleId));
        for (Map.Entry<Long, List<SysRoleOrg>> entry : roleOrgMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().stream().map(SysRoleOrg::getOrgId).collect(Collectors.toList()));
        }
        return map;
    }

    /**
     * 根据组织编码查询组织
     *
     * @param orgCode  组织编码
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByCode")
    public SysOrg findByCode(String orgCode, String language) {
        if (ObjectUtils.isEmpty(orgCode)) {
            throw new ParamValidateException("orgCode不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        return orgService.findByCode(orgCode, language);
    }


    /**
     * 根据组织ID集合查询组织
     *
     * @param orgIds   组织ID集合
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByIds")
    public List<SysOrg> findByIds(@RequestBody List<Long> orgIds, String language) {
        return orgService.findByIds(orgIds, language);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param org       组织信息
     */
    private void verification(String operation, SysOrg org) {
        if (ObjectUtils.isEmpty(org)) {
            throw new ParamValidateException("参数错误");
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(org.getOrgId())) {
                throw new ParamValidateException("orgId不能为空!");
            }
            if (ObjectUtils.isEmpty(org.getLanguage())) {
                throw new ParamValidateException("language不能为空");
            }
            if (!LanguageEnum.contains(org.getLanguage())) {
                throw new ParamValidateException("language参数错误");
            }
        }
        if (ObjectUtils.isEmpty(org.getOrgCode())) {
            throw new ParamValidateException("组织编码不能为空!");
        }
        if (org.getOrgCode().length() > 30) {
            throw new ParamValidateException("组织编码超长(最大30)!");
        }
        if (OperationTypeEnum.ADD.getValue().equals(operation)) {
            //根据组织编码查询组织
            SysOrg resultData = orgService.findByCode(org.getOrgCode(), LanguageEnum.ZH_CN.getValue());
            if (!ObjectUtils.isEmpty(resultData)) {
                throw new ParamValidateException("该组织编码已存在!");
            }
        }
        if (ObjectUtils.isEmpty(org.getOrgName())) {
            throw new ParamValidateException("组织名称不能为空!");
        }
        if (org.getOrgName().length() > 18) {
            throw new ParamValidateException("组织名称超长(最大18)!");
        }
        if (ObjectUtils.isEmpty(org.getOrgStatus())) {
            org.setOrgStatus(EnableFlagEnum.Y.getValue());
        } else {
            if (!EnableFlagEnum.contains(org.getOrgStatus())) {
                throw new ParamValidateException("状态参数错误!");
            }
        }
        if (!ObjectUtils.isEmpty(org.getOrgLocation()) && org.getOrgLocation().length() > 100) {
            throw new ParamValidateException("组织地点超长(最大100)!");
        }
    }

    /**
     * 根据组织编码列表查询组织
     *
     * @param orgCodeList 组织编码列表
     * @param language    语言
     * @return 结果
     */
    @PostMapping("/findByCodeList")
    public List<SysOrg> findByCodeList(@RequestBody List<String> orgCodeList, @RequestParam("language") String language) {
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        return orgService.findByCodeList(orgCodeList, language);
    }
}

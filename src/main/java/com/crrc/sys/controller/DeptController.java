package com.crrc.sys.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.CollectionUtils;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.model.dept.*;
import com.crrc.sys.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name = "系统管理 - 部门管理")
@RestController
@Slf4j
@RequestMapping("dept")
public class DeptController {
    @Autowired
    DeptService deptService;
    /*@Autowired
    DeptRoleService deptRoleService;
    @Autowired
    RoleService roleService;
    @Autowired
    UserDeptService userDeptService;
    @Autowired
    UserCompanyService userCompanyService;
    @Autowired
    UserService userService;*/

    @PostMapping("/create")
    @Operation(summary = "创建部门")
    @AuditLog(value = "创建菜单", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Long createDept(@Valid @RequestBody DeptCreateReqVO createReqVO) {
        Dept dept = BeanUtils.toBean(createReqVO, Dept.class);
        deptService.save(dept);
        return dept.getId();
    }

    @PostMapping("/update")
    @Operation(summary = "更新部门")
    @AuditLog(value = "更新部门", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Long updateDept(@Valid @RequestBody DeptUpdateReqVO updateReqVO) {
        Dept dept = BeanUtils.toBean(updateReqVO, Dept.class);
        Dept deptInDB = deptService.getById(dept.getId());
        if (deptInDB == null) {
            throw new BizException("部门不存在");
        }
        deptService.updateById(dept);
        return dept.getId();
    }

    @PostMapping("/list")
    @Operation(summary = "获取部门列表")
    public List<DeptListRespVO> listDept(@Valid @RequestBody DeptqueryVo deptqueryVo) {
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        //部门名称模糊查询
        queryWrapper.like(!ObjectUtils.isEmpty(deptqueryVo.getKeywords()), Dept::getDepartmentName, deptqueryVo.getKeywords());
        //根据部门ID集合查询
        queryWrapper.in(!ObjectUtils.isEmpty(deptqueryVo.getDeptIds()), Dept::getId, deptqueryVo.getDeptIds());
        //根据公司ID集合查询
        queryWrapper.in(!ObjectUtils.isEmpty(deptqueryVo.getCompanyIds()), Dept::getCompanyId, deptqueryVo.getCompanyIds());
        //排序
        queryWrapper.orderByAsc(Dept::getCompanyId, Dept::getOrgCode);
        List<Dept> deptList = deptService.list(queryWrapper);
        if (ObjectUtils.isEmpty(deptList)) {
            return null;
        }
        // 剔除临时部门
        deptList.removeIf(o -> o.getCompanyId() == 0 || o.getOrgCode().equals("0"));
        return deptService.getDeptListResp(deptList);
    }

    @PostMapping("/get_subdept_list")
    @Operation(summary = "通过部门id获取子部门id列表")
    public List<Long> getSubdeptList(@RequestBody Long id) {
        return deptService.getSubDeptId(id);
    }


    @PostMapping("/get_list")
    @Operation(summary = "通过部门id获取部门列表")
    public List<Dept> getDeptList(@RequestBody List<Long> ids) {
        LambdaQueryWrapper<Dept> deptLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deptLambdaQueryWrapper.in(!Objects.isNull(ids) && !ids.isEmpty(), Dept::getId, ids);
        deptLambdaQueryWrapper.orderByAsc(Dept::getCompanyId, Dept::getOrgCode);
        List<Dept> deptList = deptService.list(deptLambdaQueryWrapper);
        if (ObjectUtils.isEmpty(deptList)) {
            return null;
        }
        return deptList;
    }

    @GetMapping("/get")
    @Operation(summary = "获取部门")
    public DeptRespVO getDept(@Valid @NotNull Long id) {
        Dept dept = deptService.getById(id);
        if (Objects.isNull(dept)) {
            return null;
        }
        DeptRespVO deptResp = BeanUtils.toBean(dept, DeptRespVO.class);
        /*List<Role> roles = roleService.getRoleByDeptId(id);
        deptResp.setRoles(BeanUtils.toBean(roles, DeptRespVO.Role.class));*/
        return deptResp;
    }


    @DeleteMapping("/delete")
    @Operation(summary = "删除部门")
    @AuditLog(value = "删除部门", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public void deleteDept(@NotNull @RequestParam Long id) {
        Dept dept = deptService.getById(id);
        if (ObjectUtils.isEmpty(dept)) {
            throw new BizException("部门不存在");
        }
        deptService.removeById(id);
        /*LambdaQueryWrapper<DeptRole> deptRoleQueryWrapper = new LambdaQueryWrapper<>();
        deptRoleQueryWrapper.eq(DeptRole::getDepartmentId, id);
        deptRoleService.remove(deptRoleQueryWrapper);
        LambdaQueryWrapper<UserDept> userDeptLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userDeptLambdaQueryWrapper.eq(UserDept::getDepartmentId, id);
        userDeptService.remove(userDeptLambdaQueryWrapper);
        return new ResponseResult<>(200, "OK", id);*/
    }


    @PostMapping("/set_dept_role")
    @Operation(summary = "关联部门角色")
    @Transactional
    public void setDeptRole(@Valid @RequestBody DeptRoleCreateReqVO deptRoleCreateReqVO) {
       /* if (deptRoleCreateReqVO.getRoleIds().isEmpty()) {
            throw exception(ROLE_LIST_NULL_EXCEPTION);
        }
        List<DeptRole> deptRoles = new ArrayList<>();
        deptRoleCreateReqVO.getRoleIds().forEach(
                roleId -> {
                    deptRoles.add(new DeptRole().setRoleId(roleId).setDepartmentId(deptRoleCreateReqVO.getDeptId()));
                }
        );
        LambdaQueryWrapper<DeptRole> wapper = new LambdaQueryWrapper<>();
        wapper.eq(DeptRole::getDepartmentId, deptRoleCreateReqVO.getDeptId());
        deptRoleService.remove(wapper);
        deptRoleService.saveBatch(deptRoles);
        return new ResponseResult<>(200, "OK");*/
    }

    @GetMapping("/export")
    @Operation(summary = "部门导出")
    @AuditLog(value = "部门导出", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    public void exportMenu(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("部门列表-" + new SimpleDateFormat("yyyyMMddhhmmss").format(System.currentTimeMillis()), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
        List<Dept> deptList = deptService.list();
        List<DeptExportRespVO> deptRespList = new ArrayList<>();
        Map<String, Dept> deptNameMap = CollectionUtils.convertMap(deptList, Dept::getOrgCode);
        for (Dept dept : deptList) {
            DeptExportRespVO deptExportRespVO = BeanUtils.toBean(dept, DeptExportRespVO.class);
            if (dept.getParentOrgCode() != null && !Objects.isNull(deptNameMap.get(dept.getParentOrgCode()))) {
                deptExportRespVO.setParentDepartmentName(deptNameMap.get(dept.getParentOrgCode()).getDepartmentName());
            }
            deptRespList.add(deptExportRespVO);
        }
        EasyExcel.write(response.getOutputStream(), DeptExportRespVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("部门列表")
                .doWrite(deptRespList);

    }

    @PostMapping("/import")
    @Operation(summary = "部门导入")
    @AuditLog(value = "部门导入", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public void importMenu(MultipartFile file) throws IOException {
        /*EasyExcel.read(file.getInputStream(), DeptExportRespVO.class, new DeptListener(deptService, deptService.list())).sheet().doRead();
        return new ResponseResult<>(200, "OK");*/
    }

    @GetMapping("/get_depts_by_user_id")
    @Operation(summary = "通过用户id查询部门 - OpenFegin接口")
    public List<DeptOfUserDTO> getDeptofUser(@RequestParam List<Long> ids) {
        return deptService.getDeptByUserIdList(ids);
    }

    @GetMapping("/get_depts_by_user_name")
    @Operation(summary = "通过用户编码查询部门 - OpenFegin接口")
    public List<DeptOfUserDTO> getDeptofUserNameList(@RequestParam List<String> usernames) {
        /*List<Long> userIds = userService.getUsersByUsernames(usernames).stream().map(UserIdAndNameVO::getId).collect(Collectors.toList());
        return deptService.getDeptByUserIdList(userIds);*/
        return null;
    }

    @PostMapping("/get_depts_by_company_id")
    @Operation(summary = "通过公司id查询部门 - OpenFegin接口")
    public List<Dept> getDeptByCompanyId(@RequestBody Long id) {
        return deptService.getDeptListByCompanyId(id);
    }

    @PostMapping("/get_dept_by_org_code")
    @Operation(summary = "通过公司orgCode查询部门 - OpenFegin接口")
    public Dept getDeptByOrgCode(@RequestBody String orgCode) {
        return deptService.getDeptByOrgCode(orgCode);
    }

    @GetMapping("/get_dept_and_subDepts_by_orgCode")
    @Operation(summary = "通过用户编码查询部门 - OpenFegin接口")
    public List<Dept> getDeptAndSubDeptsByOrgCode(@RequestParam String orgCode) {
        return deptService.getDeptAndSubDeptsByOrgCode(orgCode);
    }

}

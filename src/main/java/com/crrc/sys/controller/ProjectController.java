package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.entity.project.ProjectServiceEntity;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.model.project.*;
import com.crrc.sys.service.ProjectService;
import com.crrc.sys.service.ProjectServiceService;
import com.crrc.sys.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "系统管理 - 项目管理")
@RestController
@Slf4j
@RequestMapping("project")
public class ProjectController {

    @Resource
    private ProjectService projectService;
    @Resource
    private ProjectServiceService projectServiceService;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysRoleService roleService;


    @GetMapping("/get")
    @Operation(summary = "根据项目ID查询项目信息")
    public ProjectRespVO getProject(@NotNull @RequestParam Long id) {
        ProjectEntity projectEntity = projectService.getById(id);
        if (ObjectUtils.isEmpty(projectEntity)) {
            throw new BizException("项目不存在");
        }
        return BeanUtils.toBean(projectEntity, ProjectRespVO.class);
    }

    @PostMapping("/create")
    @Operation(summary = "创建项目")
    @AuditLog(value = "创建项目", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public void createProject(@NotNull @RequestBody ProjectCreateReqVO createReqVO) {
        if(createReqVO == null) {
            throw new BizException("保存的项目信息为空!");
        }
        if(StringUtils.isNotBlank(createReqVO.getName())) {
            Long countByCode = projectService.findCountByCode(createReqVO.getName());
            if(countByCode > 0) {
                throw new BizException("保存错误，名称为"+createReqVO.getName()+"的项目已经存在!");
            }
        }
        ProjectEntity projectEntity = BeanUtils.toBean(createReqVO, ProjectEntity.class);
        boolean result = projectService.save(projectEntity);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新项目")
    @AuditLog(value = "更新项目", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_MODIFY)
    public void updateProject(@NotNull @RequestBody ProjectUpdateReqVO updateReqVO) {
        if(updateReqVO == null) {
            throw new BizException("更新的项目信息为空!");
        }
        ProjectEntity projectEntityInDB = projectService.getById(updateReqVO.getId());
        if (ObjectUtils.isEmpty(projectEntityInDB)) {
            throw new BizException("项目不存在");
        }
        // 项目存在,且更新项目名称时,要判断新的项目名称是否存在
        if(StringUtils.isNotBlank(updateReqVO.getName()) && !projectEntityInDB.getName().equals(updateReqVO.getName())) {
            Long countByCode = projectService.findCountByCode(updateReqVO.getName());
            if(countByCode > 0) {
                throw new BizException("修改错误，名称为"+updateReqVO.getName()+"的项目已经存在!");
            }
        }
        ProjectEntity projectEntity = BeanUtils.toBean(updateReqVO, ProjectEntity.class);
        boolean result = projectService.updateById(projectEntity);
        if (!result) {
            throw new BizException("保存失败");
        }
    }

    @PostMapping("/set_project_service")
    @Operation(summary = "设置项目服务关联")
    public void setUserProject(@RequestBody ProjectServiceCreateReqVO projectServiceCreateReqVO) {
        LambdaQueryWrapper<ProjectServiceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServiceEntity::getProjectId, projectServiceCreateReqVO.getProjectId());
        projectServiceService.remove(wrapper);
        if (CollectionUtils.isEmpty(projectServiceCreateReqVO.getServiceIds())) {
            return;
        }
        List<ProjectServiceEntity> projectServiceEntities = new ArrayList<>();
        for (int i = 0; i < projectServiceCreateReqVO.getServiceIds().size(); i++) {
            ProjectServiceEntity projectServiceEntity = new ProjectServiceEntity();
            projectServiceEntity.setProjectId(projectServiceCreateReqVO.getProjectId());
            projectServiceEntity.setServiceId(projectServiceCreateReqVO.getServiceIds().get(i));
            projectServiceEntities.add(projectServiceEntity);
        }
        boolean result = projectServiceService.saveBatch(projectServiceEntities);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目")
    @AuditLog(value = "删除项目", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public void deleteRole(@NotNull @RequestParam Long id) {
        ProjectEntity projectEntityInDB = projectService.getById(id);
        if (Objects.isNull(projectEntityInDB)) {
            throw new BizException("项目不存在");
        }
        LambdaQueryWrapper<ProjectServiceEntity> projectServiceWrapper = new LambdaQueryWrapper<>();
        projectServiceWrapper.eq(ProjectServiceEntity::getProjectId, id);
        projectServiceService.remove(projectServiceWrapper);
        projectService.removeById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "查询项目page")
    public PaginationGrid<ProjectPageRespVO> getProjectPage(ProjectPageReqVO pageReqVO) {
        LambdaQueryWrapper<ProjectEntity> wapper = new LambdaQueryWrapper<>();
        wapper.like(!StrUtil.isEmpty(pageReqVO.getName()), ProjectEntity::getName, pageReqVO.getName());
        wapper.like(!StrUtil.isEmpty(pageReqVO.getNameCn()), ProjectEntity::getNameCn, pageReqVO.getNameCn());
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getStatus()), ProjectEntity::getStatus, pageReqVO.getStatus());
        IPage<ProjectEntity> iPage = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        iPage = projectService.page(iPage, wapper);
        List<ProjectEntity> records = iPage.getRecords();
        PaginationGrid<ProjectPageRespVO> paginationGrid = new PaginationGrid<>((int) iPage.getCurrent(), (int) iPage.getSize());
        if (CollectionUtils.isEmpty(records)) {
            return paginationGrid;
        }
        List<ProjectPageRespVO> projectList = BeanUtils.toBean(iPage.getRecords(), ProjectPageRespVO.class);
        paginationGrid.setTotal(iPage.getTotal());
        paginationGrid.setRows(projectList);
        return paginationGrid;
    }

    @DeleteMapping("/delete_prject_user")
    @Operation(summary = "删除项目对应用户")
    public void deleteUsersOfProject(@NotNull @RequestParam List<Long> ids) {
    }

    @GetMapping("/get_project_users")
    @Operation(summary = "通过项目id查询用户列表")
    public void getProjectUserList() {
    }

    @PostMapping("/set_project_users")
    @Operation(summary = "关联项目用户")
    public void setUserRole() {
    }


    /**
     * 查询项目列表
     *
     * @param name   项目编码
     * @param nameCn 项目中文名
     * @return 结果
     */
    @GetMapping("list")
    public List<ProjectEntity> list(String name, String nameCn) {
        LambdaQueryWrapper<ProjectEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!ObjectUtils.isEmpty(name), ProjectEntity::getName, name);
        queryWrapper.like(!ObjectUtils.isEmpty(nameCn), ProjectEntity::getNameCn, nameCn);
        return projectService.list(queryWrapper);
    }

    /**
     * 获取当前用户绑定的项目列表
     *
     * @return 项目列表
     */
    @GetMapping("findByCurrentUser")
    public List<ProjectEntity> findByCurrentUser() {
        ResultData<SysUser> resultData = authClient.currentUser();
        if (!resultData.getSuccess()) {
            throw new BizException(resultData.getCode(), resultData.getMessage());
        }
        SysUser sysUser = resultData.getResult();
        //查询用户拥有的角色列表
        List<SysRole> roleList = roleService.findByUserId(sysUser.getUserId(), LanguageEnum.ZH_CN.getValue());
        if (CollectionUtils.isEmpty(roleList)) {
            throw new BizException("当前用户未绑定角色");
        }
        List<Long> projectIdList = roleList.stream().map(SysRole::getProjectId).distinct().collect(Collectors.toList());
        return projectService.listByIds(projectIdList);
    }

    /**
     * 获取当前用户拥有的项目列表
     *
     * @return 项目列表
     */
    @GetMapping("listByCurrentUser")
    public List<ProjectEntity> listByCurrentUser() {
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        SysUser sysUser = userResultData.getResult();
        //查询当前用户拥有的角色列表
        List<SysRole> roleList = roleService.findByUserId(sysUser.getUserId(), LanguageEnum.ZH_CN.getValue());
        if (CollectionUtils.isEmpty(roleList)) {
            throw new BizException("当前用户没有分配角色");
        }
        List<Long> projectIdList = roleList.stream().map(SysRole::getProjectId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<ProjectEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(!CollectionUtils.isEmpty(projectIdList), ProjectEntity::getId, projectIdList);
        //排除`中车平台`项目
        lambdaQueryWrapper.notIn(ProjectEntity::getName, "crrc_platform");
        return projectService.list(lambdaQueryWrapper);
    }
}

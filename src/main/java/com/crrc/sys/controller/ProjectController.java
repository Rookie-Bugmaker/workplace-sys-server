package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.entity.project.ProjectServiceEntity;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.model.project.*;
import com.crrc.sys.service.ProjectService;
import com.crrc.sys.service.ProjectServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Tag(name = "系统管理 - 项目管理")
@RestController
@Slf4j
@RequestMapping("project")
public class ProjectController {

    @Resource
    private ProjectService projectService;
    @Resource
    private ProjectServiceService projectServiceService;


    @GetMapping("/get")
    @Operation(summary = "根据项目ID查询项目信息")
    public ProjectRespVO getProject(@NotNull @RequestParam Long id) {
        ProjectEntity projectEntity = projectService.getById(id);
        if (ObjectUtils.isEmpty(projectEntity)) {
            throw new BizException("项目部存在");
        }
        return BeanUtils.toBean(projectEntity, ProjectRespVO.class);
    }

    @PostMapping("/create")
    @Operation(summary = "创建项目")
    @AuditLog(value = "创建项目", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public void createProject(@NotNull @RequestBody ProjectCreateReqVO createReqVO) {
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
        ProjectEntity projectEntityInDB = projectService.getById(updateReqVO.getId());
        if (ObjectUtils.isEmpty(projectEntityInDB)) {
            throw new BizException("项目不存在");
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

    @GetMapping("/list")
    @Operation(summary = "查询项目list 根据用户查询")
    public List<ProjectRespVO> getProjectListByUserId(@RequestParam Long id) {
        return null;
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
}

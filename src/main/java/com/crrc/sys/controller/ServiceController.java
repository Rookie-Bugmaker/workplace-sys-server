package com.crrc.sys.controller;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.server.ServiceAccessManagementConfig;
import com.crrc.sys.model.server.ServiceListReqVO;
import com.crrc.sys.model.server.ServiceSysListRespVO;
import com.crrc.sys.model.server.ServiceUpdateReqVO;
import com.crrc.sys.service.ServiceAccessManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;


@Tag(name = "系统管理 - 服务管理")
@RestController
@Slf4j
@RequestMapping("service")
public class ServiceController {
    @Resource
    private ServiceAccessManagementService serviceAccessManagementService;


    /**
     * 服务信息分页擦好像
     *
     * @param reqVO    查询参数
     * @param page     当前页码
     * @param pageSize 查询数据条数
     * @return 结果
     */
    @GetMapping("page")
    public PaginationGrid<ServiceAccessManagementConfig> page(ServiceListReqVO reqVO, Long page, Long pageSize) {
        if (ObjectUtils.isEmpty(page) || page <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(pageSize)) {
            throw new ParamValidateException("pageSize不能为空");
        }
        LambdaQueryWrapper<ServiceAccessManagementConfig> wapper = new LambdaQueryWrapper<>();
        wapper.like(!StrUtil.isEmpty(reqVO.getName()), ServiceAccessManagementConfig::getName, reqVO.getName());
        wapper.like(!StrUtil.isEmpty(reqVO.getNameCn()), ServiceAccessManagementConfig::getNameCn, reqVO.getNameCn());
        wapper.eq(!StrUtil.isEmpty(reqVO.getStatus()), ServiceAccessManagementConfig::getStatus, reqVO.getStatus());
        wapper.like(!StrUtil.isEmpty(reqVO.getSysName()), ServiceAccessManagementConfig::getSysName, reqVO.getSysName());
        wapper.eq(!Objects.isNull(reqVO.getSysService()), ServiceAccessManagementConfig::getSysService, reqVO.getSysService());
        IPage<ServiceAccessManagementConfig> iPage = Page.of(page, pageSize);
        iPage = serviceAccessManagementService.page(iPage, wapper);
        return PaginationGrid.success(iPage);
    }

    @GetMapping("/get")
    @Operation(summary = "查询服务 - id")
    public ServiceAccessManagementConfig getService(@NotNull @RequestParam Long id) {
        return serviceAccessManagementService.getById(id);
    }

    @PostMapping("/update")
    @Operation(summary = "修改服务")
    public void getService(@NotNull @RequestBody ServiceUpdateReqVO updateReqVO) {
        ServiceAccessManagementConfig serviceAccessManagementConfig = serviceAccessManagementService.getById(updateReqVO.getId());
        if (Objects.isNull(serviceAccessManagementConfig)) {
            throw new BizException("服务不存在");
        }
        ServiceAccessManagementConfig updateConfig = BeanUtils.toBean(updateReqVO, ServiceAccessManagementConfig.class);
        boolean result = serviceAccessManagementService.updateById(updateConfig);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    @GetMapping("/get_system")
    @Operation(summary = "查询系统")
    public List<ServiceSysListRespVO> getSystemList() {
        return serviceAccessManagementService.getSysList();
    }

    @GetMapping("/list")
    @Operation(summary = "查询服务 - list")
    public List<ServiceAccessManagementConfig> getServiceList(ServiceListReqVO reqVO) {
        LambdaQueryWrapper<ServiceAccessManagementConfig> wapper = new LambdaQueryWrapper<>();
        wapper.like(!StrUtil.isEmpty(reqVO.getName()), ServiceAccessManagementConfig::getName, reqVO.getName());
        wapper.like(!StrUtil.isEmpty(reqVO.getNameCn()), ServiceAccessManagementConfig::getNameCn, reqVO.getNameCn());
        wapper.eq(!StrUtil.isEmpty(reqVO.getStatus()), ServiceAccessManagementConfig::getStatus, reqVO.getStatus());
        wapper.like(!StrUtil.isEmpty(reqVO.getSysName()), ServiceAccessManagementConfig::getSysName, reqVO.getSysName());
        wapper.eq(!Objects.isNull(reqVO.getSysService()), ServiceAccessManagementConfig::getSysService, reqVO.getSysService());
        return serviceAccessManagementService.list(wapper);
    }

    @PostMapping("/deploy")
    @Operation(summary = "部署服务")
    public String deployService(@RequestBody String serviceInfo) {
        ServiceAccessManagementConfig serviceAccessManagementConfig = JSON.parseObject(serviceInfo, ServiceAccessManagementConfig.class);
        LambdaQueryWrapper<ServiceAccessManagementConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceAccessManagementConfig::getName, serviceAccessManagementConfig.getName());
        ServiceAccessManagementConfig configFromDB = serviceAccessManagementService.getOne(queryWrapper);
        if (!Objects.isNull(configFromDB) && Objects.equals(configFromDB.getStatus(), "0")) {
            return "OK";
        }
        serviceAccessManagementService.saveOrUpdate(serviceAccessManagementConfig);
        return "OK";
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除服务 - id")
    public void deleteService(@NotNull @RequestParam Long id) {
        boolean result = serviceAccessManagementService.removeById(id);
        if (!result) {
            throw new BizException("删除失败");
        }
    }
}

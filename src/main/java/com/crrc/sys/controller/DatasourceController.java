package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.datasource.DataSourceEntity;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.model.datasource.*;
import com.crrc.sys.service.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "系统管理 - 数据源管理")
@RestController
@Slf4j
@RequestMapping("datasource")
public class DatasourceController {
    @Autowired
    DataSourceService dataSourceService;
    @Value("${crrc.datasource.decrypt.type:aes}")
    public String decryptType;
    @Value("${crrc.datasource.decrypt.key:yMYlNKjJgojO/xLPH48RE7qyGxGexe8MLYvKOAR2sxc=}")
    public String decryptKey;

    @PostMapping("/create")
    @Operation(summary = "创建数据源")
    @AuditLog(value = "创建数据源", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public void createDatasource(@Valid @RequestBody DatasourceCreateReqVO createReqVO) {
        DataSourceEntity datasource = BeanUtils.toBean(createReqVO, DataSourceEntity.class);
        checkDatasource(datasource);
        encryptPass(datasource);
        boolean result = dataSourceService.save(datasource);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新数据源")
    @AuditLog(value = "更新数据源", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_MODIFY)
    public void updateDatasource(@Valid @RequestBody DatasourceUpdateReqVO updateReqVO) {
        DataSourceEntity datasource = BeanUtils.toBean(updateReqVO, DataSourceEntity.class);
        checkDatasource(datasource);
        encryptPass(datasource);
        boolean result = dataSourceService.updateById(datasource);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    @PostMapping("/update_password")
    @Operation(summary = "更新数据源密码")
    @AuditLog(value = "更改数据源密码", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_MODIFY)
    public void updateDatasourcePassword(@Valid @RequestBody DatasourceUpdatePasswordReqVO updateReqVO) {
        DataSourceEntity datasourceInDB = dataSourceService.getById(updateReqVO.getId());
        if (ObjectUtils.isEmpty(datasourceInDB)) {
            throw new BizException("数据源不存在!");
        }
        datasourceInDB.setPassword(updateReqVO.getPassword());
        checkDatasource(datasourceInDB);
        DataSourceEntity datasource = BeanUtils.toBean(updateReqVO, DataSourceEntity.class);
        encryptPass(datasource);
        boolean result = dataSourceService.updateById(datasource);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取数据源列表")
    public List<DatasourceRespVO> listDatasource() {
        List<DataSourceEntity> datasourceList = dataSourceService.list();
        List<DatasourceRespVO> datasourceRespVOList = BeanUtils.toBean(datasourceList, DatasourceRespVO.class);
        datasourceRespVOList.forEach(datasourceRespVO -> {
            datasourceRespVO.setPassword("******");
        });
        return datasourceRespVOList;
    }


    @DeleteMapping("/delete")
    @Operation(summary = "删除数据源")
    @AuditLog(value = "删除数据源", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public void deleteDatasource(@NotNull @RequestParam Long id) {
        boolean result = dataSourceService.removeById(id);
        if (!result) {
            throw new BizException("删除失败!");
        }
    }

    @GetMapping("/get_datasource_list")
    @Operation(summary = "获取数据源列表 - OpenFegin使用")
    public List<DataSourceEntityDTO> getDatasources() {
        List<DataSourceEntity> datasourceList = dataSourceService.list();
        return BeanUtils.toBean(datasourceList, DataSourceEntityDTO.class);
    }

    @GetMapping("/get_decrypt_type_and_key")
    @Operation(summary = "获取数据源加密key - OpenFegin使用")
    public Map<String, String> getDecryptTypeAndKey() {
        Map<String, String> map = new HashMap<>();
        map.put("type", decryptType);
        map.put("key", decryptKey);
        return map;
    }

    @GetMapping("/get_decrypt_type_and_key_global")
    @Operation(summary = "获取数据源加密key(未登录使用) - OpenFegin使用")
    public Map<String, String> getDecryptTypeAndKeyGlobal() {
        Map<String, String> map = new HashMap<>();
        map.put("type", decryptType);
        map.put("key", decryptKey);
        return map;
    }

    @PostMapping("/page")
    @Operation(summary = "数据源分页查询")
    public PaginationGrid<DatasourceRespVO> getDatasourcePage(@Valid @RequestBody DatasourcePageReqVO pageReqVO) {
        LambdaQueryWrapper<DataSourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!StrUtil.isEmpty(pageReqVO.getId()), DataSourceEntity::getId, pageReqVO.getId());
        queryWrapper.like(!StrUtil.isEmpty(pageReqVO.getDatasourceId()), DataSourceEntity::getDatasourceId, pageReqVO.getDatasourceId());
        queryWrapper.like(!StrUtil.isEmpty(pageReqVO.getUrl()), DataSourceEntity::getUrl, pageReqVO.getUrl());
        queryWrapper.like(!StrUtil.isEmpty(pageReqVO.getUsername()), DataSourceEntity::getUsername, pageReqVO.getUsername());
        queryWrapper.like(!StrUtil.isEmpty(pageReqVO.getDatabaseType()), DataSourceEntity::getDatabaseType, pageReqVO.getDatabaseType());
        queryWrapper.like(!StrUtil.isEmpty(pageReqVO.getDriverClassName()), DataSourceEntity::getDriverClassName, pageReqVO.getDriverClassName());
        IPage<DataSourceEntity> iPage = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        iPage = dataSourceService.page(iPage, queryWrapper);
        List<DataSourceEntity> records = iPage.getRecords();
        PaginationGrid<DatasourceRespVO> paginationGrid = new PaginationGrid<>((int) iPage.getCurrent(), (int) iPage.getSize());
        paginationGrid.setTotal(iPage.getTotal());
        if (ObjectUtils.isEmpty(records)) {
            return paginationGrid;
        }
        List<DatasourceRespVO> respList = BeanUtils.toBean(iPage.getRecords(), DatasourceRespVO.class);
        respList.forEach(datasourceRespVO -> {
            datasourceRespVO.setPassword("******");
        });
        paginationGrid.setRows(respList);
        return paginationGrid;
    }

    @GetMapping("/get")
    @Operation(summary = "获取数据源")
    public DatasourceRespVO findById(@Valid @NotNull Long id) {
        DataSourceEntity datasource = dataSourceService.getById(id);
        if (ObjectUtils.isEmpty(datasource)) {
            throw new BizException("数据源不存在!");
        }
        datasource.setPassword("");
        DatasourceRespVO datasourceResp = BeanUtils.toBean(datasource, DatasourceRespVO.class);
        datasourceResp.setDatasourceName(datasource.getDatasourceName());
        return datasourceResp;
    }

    private void checkDatasource(DataSourceEntity dataSource) {
        try {
            Class.forName(dataSource.getDriverClassName());
            Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            if (connection == null) {
                log.error("数据源配置有错误，DataSource：{}", dataSource);
                throw new BizException("数据源配置有错误");
            } else {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(e.getMessage());
        }
    }

    private void encryptPass(DataSourceEntity dataSource) {
        byte[] bytes = Base64.getDecoder().decode(decryptKey);
        AES aes = SecureUtil.aes(bytes);
        String encryptHex = aes.encryptHex(dataSource.getPassword());
        dataSource.setPassword(encryptHex);
    }
}

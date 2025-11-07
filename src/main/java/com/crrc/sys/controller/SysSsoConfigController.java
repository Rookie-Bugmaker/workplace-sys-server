package com.crrc.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.model.sso.SysSsoConfigDTO;
import com.crrc.sys.model.sso.SysSsoConfigPO;
import com.crrc.sys.service.SysSsoConfigService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单点配置-控制器
 *
 * @author 谭永强
 * @date 2025-11-06
 */
@RestController
@RequestMapping("/sso/config")
public class SysSsoConfigController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private SysSsoConfigService ssoConfigService;

    /**
     * 分页查询
     *
     * @param param 查询条件
     */
    @PostMapping("page")
    public PaginationGrid<SysSsoConfigPO> page(@RequestBody SysSsoConfigDTO param) {
        if (ObjectUtils.isEmpty(param.getPage())) {
            throw new ParamValidateException("page不能为空");
        }
        if (ObjectUtils.isEmpty(param.getPageSize())) {
            throw new ParamValidateException("pageSize不能为空");
        }
        Page<SysSsoConfigPO> page = new Page<>(param.getPage(), param.getPageSize());
        LambdaQueryWrapper<SysSsoConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!ObjectUtils.isEmpty(param.getConfigName()), SysSsoConfigPO::getConfigName, param.getConfigName());
        queryWrapper.eq(!ObjectUtils.isEmpty(param.getConfigCode()), SysSsoConfigPO::getConfigCode, param.getConfigCode());
        queryWrapper.eq(!ObjectUtils.isEmpty(param.getConfigType()), SysSsoConfigPO::getConfigType, param.getConfigType());
        page = ssoConfigService.page(page, queryWrapper);
        List<SysSsoConfigPO> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(o -> o.setClientSecret(null));
        }
        return PaginationGrid.success(page);
    }

    /**
     * 单点配置新增
     *
     * @param ssoConfig 参数
     */
    @PostMapping("add")
    public void add(@RequestBody SysSsoConfigPO ssoConfig) {
        verification(OperationTypeEnum.ADD.getValue(), ssoConfig);
        ssoConfig.setId(idKeyGenerator.getId());
        boolean result = ssoConfigService.save(ssoConfig);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 单点配置修改
     *
     * @param ssoConfig 参数
     */
    @PutMapping("update")
    public void update(@RequestBody SysSsoConfigPO ssoConfig) {
        verification(OperationTypeEnum.UPDATE.getValue(), ssoConfig);
        boolean result = ssoConfigService.updateById(ssoConfig);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 删除l
     *
     * @param idList 主键ID
     */
    @DeleteMapping("delete")
    public void delete(@RequestBody List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new ParamValidateException("参数错误");
        }
        boolean result = ssoConfigService.removeByIds(idList);
        if (!result) {
            throw new BizException("删除失败");
        }
    }

    /**
     * 根据配置编码查询配置信息
     *
     * @param configCode 配置编码
     * @return 配置信息
     */
    @GetMapping("findByCode")
    public SysSsoConfigPO findByCode(String configCode) {
        if (ObjectUtils.isEmpty(configCode)) {
            throw new ParamValidateException("编码不能为空!");
        }
        LambdaQueryWrapper<SysSsoConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysSsoConfigPO::getConfigCode, configCode);
        return ssoConfigService.getOne(lambdaQueryWrapper);
    }

    /**
     * 根据配置ID查询配置信息
     *
     * @param id 配置ID
     * @return 配置信息
     */
    @GetMapping("findById")
    public SysSsoConfigPO findById(String id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ParamValidateException("id不能为空!");
        }
        LambdaQueryWrapper<SysSsoConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysSsoConfigPO::getId, id);
        return ssoConfigService.getOne(lambdaQueryWrapper);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param ssoConfig 单点配置信息
     */
    private void verification(String operation, SysSsoConfigPO ssoConfig) {
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(ssoConfig.getId())) {
                throw new ParamValidateException("id不能为空!");
            }
        }
        if (ObjectUtils.isEmpty(ssoConfig.getConfigName())) {
            throw new ParamValidateException("配置名称不能为空");
        }
        if (ObjectUtils.isEmpty(ssoConfig.getConfigCode())) {
            throw new ParamValidateException("配置编码不能为空");
        }
        //配置编码唯一性校验
        SysSsoConfigPO sysSsoConfig = findByCode(ssoConfig.getConfigCode());
        if (!ObjectUtils.isEmpty(sysSsoConfig)) {
            if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                throw new ParamValidateException("配置编码已存在");
            }
            if (OperationTypeEnum.UPDATE.getValue().equals(operation) && !sysSsoConfig.getId().equals(ssoConfig.getId())) {
                throw new ParamValidateException("配置编码已存在");
            }
        }
        if (ObjectUtils.isEmpty(ssoConfig.getConfigType())) {
            throw new ParamValidateException("单点类型不能为空");
        }
        if (ObjectUtils.isEmpty(ssoConfig.getClientId())) {
            throw new ParamValidateException("应用ID不能为空!");
        }
        if (ObjectUtils.isEmpty(ssoConfig.getClientSecret())) {
            throw new ParamValidateException("应用密钥不能为空!");
        }
        if (ObjectUtils.isEmpty(ssoConfig.getAuthorizeRequestUrl())) {
            throw new ParamValidateException("登录地址不能为空");
        }
        if (ObjectUtils.isEmpty(ssoConfig.getTokenRequestUrl())) {
            throw new ParamValidateException("获取token地址不能为空");
        }
        if (ObjectUtils.isEmpty(ssoConfig.getUserRequestUrl())) {
            throw new ParamValidateException("获取用户地址不能为空");
        }
        if (ObjectUtils.isEmpty(ssoConfig.getRedirectUrl())) {
            throw new ParamValidateException("回调地址不能为空");
        }
    }
}

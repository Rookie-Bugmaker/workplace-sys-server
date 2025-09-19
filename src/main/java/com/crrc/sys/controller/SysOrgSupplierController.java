package com.crrc.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.convert.DeptConvert;
import com.crrc.sys.entity.BaseEntity;
import com.crrc.sys.entity.BaseEntityYunLing;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.org.supplier.SysOrgSupplier;
import com.crrc.sys.entity.org.supplier.SysOrgSupplierPageParam;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.GenderEnum;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.model.dept.DeptInfoDTO;
import com.crrc.sys.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组织架构-供应商-控制器
 *
 * @author 李江涛
 * @date 2025-09-13
 */
@RestController
@RequestMapping("/org/supplier/")
public class SysOrgSupplierController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysOrgSupplierService orgSupplierService;
    @Resource
    private SysUserService userService;
    @Resource
    private DeptService deptService;
    @Resource
    private CompanyService companyService;

    /**
     * 供应商分页查询(Copy)
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage/cp")
    public PaginationGrid<SysOrgSupplier> findByPageCopy(SysOrgSupplierPageParam param) {
        return findByPage(param);
    }

    /**
     * 供应商分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysOrgSupplier> findByPage(SysOrgSupplierPageParam param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(param.getPage()) || param.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(param.getPageSize()) || param.getPageSize() < 0) {
            throw new ParamValidateException("pageSize不能为空且必须>=0");
        }
        IPage<SysOrgSupplier> page = new Page<>(param.getPage(), param.getPageSize());
        LambdaQueryWrapper<SysOrgSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(param.getSupplierId() != null, SysOrgSupplier::getSupplierId, param.getSupplierId());
        queryWrapper.eq(StringUtils.isNotBlank(param.getMainDataId()), SysOrgSupplier::getMainDataId, param.getMainDataId());
        queryWrapper.like(StringUtils.isNotBlank(param.getSupplierCode()), SysOrgSupplier::getSupplierCode, param.getSupplierCode());
        queryWrapper.eq(StringUtils.isNotBlank(param.getPlantCode()), SysOrgSupplier::getPlantCode, param.getPlantCode());
        queryWrapper.eq(StringUtils.isNotBlank(param.getOrgCode()), SysOrgSupplier::getOrgCode, param.getOrgCode());
        queryWrapper.eq(param.getStatus() != null, SysOrgSupplier::getStatus, param.getStatus());
        queryWrapper.orderByDesc(SysOrgSupplier::getLastUpdateDate);
        page = orgSupplierService.page(page, queryWrapper);
        return PaginationGrid.success(page);
    }

    /**
     * 供应商新增
     *
     * @param supplier 供应商
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(SysOrgSupplier supplier) {
        verification(OperationTypeEnum.ADD.getValue(), supplier);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        supplier.setSupplierId(idKeyGenerator.getId());
        //创建人
        supplier.setCreateBy(currentUser.getUserId());
        //创建时间
        supplier.setCreateDate(Calendar.getInstance().getTime());
        Boolean result = orgSupplierService.save(supplier);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 供应商修改
     *
     * @param supplier 供应商
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysOrgSupplier supplier) {
        verification(OperationTypeEnum.UPDATE.getValue(), supplier);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //创建人
        supplier.setLastUpdateBy(currentUser.getUserId());
        //创建时间
        supplier.setLastUpdateDate(Calendar.getInstance().getTime());
        Boolean result = orgSupplierService.updateById(supplier);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param supplier    供应商
     */
    private void verification(String operation, SysOrgSupplier supplier) {
        if (ObjectUtils.isEmpty(supplier)) {
            throw new ParamValidateException("参数错误");
        }
        //移除语言中的逗号
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(supplier.getSupplierId())) {
                throw new ParamValidateException("supplierId不能为空!");
            }
        }
        if (!StringUtils.isBlank(supplier.getSupplierCode())) {
            throw new ParamValidateException("供应商编码不能为空!");
        }
        if (supplier.getSupplierCode().length() > 20) {
            throw new ParamValidateException("供应商编码超长(最大20)!");
        }
        LambdaQueryWrapper<SysOrgSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrgSupplier::getSupplierCode, supplier.getSupplierCode());
        long count = orgSupplierService.count(queryWrapper);
        // 根据供应商编码查询供应商信息
        if (OperationTypeEnum.ADD.getValue().equals(operation)) {
            if (count > 0) {
                throw new ParamValidateException("供应商编码已存在");
            }
        }
        if (ObjectUtils.isEmpty(supplier.getStatus())) {
            throw new ParamValidateException("供应商状态不能为空");
        }
    }

    /**
     * 供应商删除
     *
     * @param supplierIds 供应商ID，多个以逗号","拼接
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String supplierIds) {
        if (ObjectUtils.isEmpty(supplierIds)) {
            throw new ParamValidateException("supplierIds不能为空");
        }
        List<Long> idList = Arrays.stream(supplierIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        Boolean result = orgSupplierService.removeByIds(idList);
        if (!result) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 供应商列表查询
     *
     * @param param 查询条件
     * @return 结果
     */
    @PostMapping("findList")
    public List<SysOrgSupplier> findList(@RequestBody SysOrgSupplierPageParam param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new ParamValidateException("参数错误");
        }
        LambdaQueryWrapper<SysOrgSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(param.getSupplierId() != null, SysOrgSupplier::getSupplierId, param.getSupplierId());
        queryWrapper.eq(StringUtils.isNotBlank(param.getMainDataId()), SysOrgSupplier::getMainDataId, param.getMainDataId());
        queryWrapper.like(StringUtils.isNotBlank(param.getSupplierCode()), SysOrgSupplier::getSupplierCode, param.getSupplierCode());
        queryWrapper.eq(StringUtils.isNotBlank(param.getPlantCode()), SysOrgSupplier::getPlantCode, param.getPlantCode());
        queryWrapper.eq(StringUtils.isNotBlank(param.getOrgCode()), SysOrgSupplier::getOrgCode, param.getOrgCode());
        queryWrapper.eq(param.getStatus() != null, SysOrgSupplier::getStatus, param.getStatus());
        queryWrapper.orderByDesc(SysOrgSupplier::getLastUpdateDate);
        return orgSupplierService.list(queryWrapper);
    }

    /**
     * 供应商新增(批量)
     *
     * @param suppliers 供应商信息
     * @return 结果
     */
    @PostMapping("addList")
    public Boolean add(@RequestBody List<SysOrgSupplier> suppliers) {
        if (CollectionUtils.isEmpty(suppliers)) {
            throw new ParamValidateException("参数不能为空!");
        }
        Date now = Calendar.getInstance().getTime();
        suppliers.forEach(o -> {
            o.setSupplierId(idKeyGenerator.getId());
            o.setCreateBy(-1L);
            o.setCreateDate(now);
        });
        return orgSupplierService.saveBatch(suppliers);
    }

    /**
     * 根据供应商编码查询供应商信息
     *
     * @param supplierCode 供应商编码
     * @return 供应商实体
     */
    @PostMapping("findBySupplierCode")
    public SysOrgSupplier findBySupplierCode(@RequestParam("supplierCode") String supplierCode) {
        LambdaQueryWrapper<SysOrgSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrgSupplier::getSupplierCode, supplierCode);
        List<SysOrgSupplier> list = orgSupplierService.list(queryWrapper);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据供应商编码列表查询供应商信息
     *
     * @param supplierCodes 供应商编码列表
     * @return 结果
     */
    @PostMapping("findBySupplierCodes")
    public List<SysOrgSupplier> findBySupplierCodes(@RequestBody List<String> supplierCodes) {
        LambdaQueryWrapper<SysOrgSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysOrgSupplier::getSupplierCode, supplierCodes);
        List<SysOrgSupplier> list = orgSupplierService.list(queryWrapper);
        return list;
    }
}
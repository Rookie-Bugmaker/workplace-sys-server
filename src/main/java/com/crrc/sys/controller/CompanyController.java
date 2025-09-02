package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.model.company.*;
import com.crrc.sys.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Tag(name = "系统管理 - 公司管理")
@RestController
@Slf4j
@RequestMapping("company")
public class CompanyController {
    @Autowired
    CompanyService companyService;
    /*@Autowired
    UserCompanyService userCompanyService;*/

    @PostMapping("/create")
    @Operation(summary = "创建公司")
    @AuditLog(value = "创建公司", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public Long createCompany(HttpServletRequest request, @Valid @RequestBody CompanyCreateReqVO createReqVO) {
        Company company = BeanUtils.toBean(createReqVO, Company.class);
        companyService.save(company);
        return company.getId();
    }

    @PostMapping("/update")
    @Operation(summary = "修改公司信息")
    @AuditLog(value = "修改公司信息", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public Long updateCompany(HttpServletRequest request, @Valid @RequestBody CompanyUpdateReqVO updateReqVO) {
        Company company = BeanUtils.toBean(updateReqVO, Company.class);
        Company companyInDB = companyService.getById(company.getId());
        if (companyInDB == null) {
            throw new BizException("[updateCompany] 公司不存在");
        }
        companyService.updateById(company);
        return company.getId();
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除公司信息")
    @AuditLog(value = "删除公司信息", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public void deleteCompany(HttpServletRequest request, @NotNull @RequestParam Long id) {
        Company companyInDB = companyService.getById(id);
        if (companyInDB == null) {
            log.warn("[deleteCompany] 公司不存在 {}", id);
            throw new BizException("[deleteCompany] 公司不存在");
        }
        companyService.removeById(id);
        LambdaQueryWrapper<UserCompany> userCompanyQueryWrapper = new LambdaQueryWrapper<>();
        userCompanyQueryWrapper.eq(UserCompany::getCompanyId, id);
        //根据公司ID删除公司人员
        //userCompanyService.remove(userCompanyQueryWrapper);
    }

    @GetMapping("/get")
    @Operation(summary = "获取公司信息")
    public CompanyGetRespVO getCompany(@RequestParam Long id) {
        Company companyInDB = companyService.getById(id);
        if (companyInDB == null) {
            throw new BizException("[getCompany] 公司不存在");
        }
        CompanyGetRespVO companyGetRespVO = new CompanyGetRespVO();
        org.springframework.beans.BeanUtils.copyProperties(companyInDB, companyGetRespVO);
        return companyGetRespVO;
    }

    @GetMapping("/list")
    @Operation(summary = "获取公司信息list")
    public List<CompanyGetRespVO> getCompany(HttpServletRequest request) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getStatus, "0");
        List<Company> companies = companyService.list(wrapper);
        CompanyGetRespVO companyGetRespVO = new CompanyGetRespVO();
        org.springframework.beans.BeanUtils.copyProperties(companies, companyGetRespVO);
        List<CompanyGetRespVO> companyList = BeanUtils.toBean(companies, CompanyGetRespVO.class);
        return companyList;
    }

    @GetMapping("/page")
    @Operation(summary = "获取公司信息page")
    public PaginationGrid<CompanyGetRespVO> getCompany(HttpServletRequest request, CompanyPageReqVO pageReqVO) {
        LambdaQueryWrapper<Company> wapper = new LambdaQueryWrapper<>();
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getCompanyName()), Company::getCompanyName, pageReqVO.getCompanyName());
        wapper.eq(!Objects.isNull(pageReqVO.getCompanyId()), Company::getCompanyId, pageReqVO.getCompanyId());
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getStatus()), Company::getStatus, pageReqVO.getStatus());
        // 剔除临时公司
        wapper.ne(Company::getCompanyId, 0L);
        wapper.ne(Company::getOrgCode, "0");
        IPage<Company> pageReq = Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        IPage<Company> companyPage = companyService.page(pageReq, wapper);
        List<CompanyGetRespVO> companyList = BeanUtils.toBean(companyPage.getRecords(), CompanyGetRespVO.class);
        PaginationGrid<CompanyGetRespVO> paginationGrid =new PaginationGrid<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        paginationGrid.setTotal(companyPage.getTotal());
        paginationGrid.setRows(companyList);
        return paginationGrid;
    }

    @GetMapping("/simple")
    @Operation(summary = "获取公司简单信息")
    public List<CompanyGetRespVO> getSimpleCompany(HttpServletRequest request, CompanyPageReqVO pageReqVO) {
        LambdaQueryWrapper<Company> wapper = new LambdaQueryWrapper<>();
        wapper.select(Company::getCompanyId, Company::getCompanyName, Company::getId, Company::getOrgCode);
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getCompanyName()), Company::getCompanyName, pageReqVO.getCompanyName());
        wapper.eq(!Objects.isNull(pageReqVO.getCompanyId()), Company::getCompanyId, pageReqVO.getCompanyId());
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getStatus()), Company::getStatus, pageReqVO.getStatus());
        // 剔除临时公司
        wapper.ne(Company::getCompanyId, 0L);
        wapper.ne(Company::getOrgCode, "0");
        List<Company> companys = companyService.list(wapper);
        return BeanUtils.toBean(companys, CompanyGetRespVO.class);
    }

    @GetMapping("/get_company_by_ids")
    @Operation(summary = "通过ids查询公司 - OpenFegin接口")
    public List<Company> getCompanyByIds(@RequestParam List<Long> ids) {
        return companyService.listByIds(ids);
    }

    @GetMapping("/get_all_company")
    @Operation(summary = "查询所有公司 - OpenFegin接口")
    public List<Company> getAllCompany() {
        return companyService.list();
    }

    @GetMapping("/get_company_by_plants")
    @Operation(summary = "通过工厂编码集合查询公司 - OpenFegin接口")
    public List<Company> getCompanyByPlants(@RequestParam List<String> plants) {
        LambdaQueryWrapper<Company> wapper = new LambdaQueryWrapper<>();
        wapper.in(Company::getPlantCode, plants);
        return companyService.list(wapper);
    }

    @GetMapping("/get_company_by_orgCodes")
    @Operation(summary = "通过组织编码集合查询公司 - OpenFegin接口")
    public List<Company> getCompanyByOrgCodes(@RequestParam List<String> orgCodes) {
        LambdaQueryWrapper<Company> wapper = new LambdaQueryWrapper<>();
        wapper.in(Company::getOrgCode, orgCodes);
        return companyService.list(wapper);
    }

}

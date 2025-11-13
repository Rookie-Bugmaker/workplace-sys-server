package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.common.*;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.excel.ExcelResultData;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.enums.*;
import com.crrc.sys.listener.CompanyImportListener;
import com.crrc.sys.listener.SysOrgPersonImportListener;
import com.crrc.sys.model.company.*;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.model.org.person.SysOrgPersonDeliveryExcelVO;
import com.crrc.sys.model.org.person.SysOrgPersonVerifyExcelDTO;
import com.crrc.sys.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Tag(name = "系统管理 - 公司管理")
@RestController
@Slf4j
@RequestMapping("company")
public class CompanyController {
    @Autowired
    CompanyService companyService;

    @Resource
    private ValueOperations<String, Object> valueOperations;

    @Resource
    private IdKeyGenerator idKeyGenerator;

    private static final String REDIS_PREFIX = "import_company_data";
    /*@Autowired
    UserCompanyService userCompanyService;*/

    @PostMapping("/create")
    @Operation(summary = "创建公司")
    @AuditLog(value = "创建公司", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public Long createCompany(@Valid @RequestBody CompanyCreateReqVO createReqVO) {
        Company company = BeanUtils.toBean(createReqVO, Company.class);
        companyService.save(company);
        return company.getId();
    }

    @PostMapping("/update")
    @Operation(summary = "修改公司信息")
    @AuditLog(value = "修改公司信息", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_ADD)
    public Long updateCompany(@Valid @RequestBody CompanyUpdateReqVO updateReqVO) {
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
    public void deleteCompany(@NotNull @RequestParam Long id) {
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
    public List<CompanyGetRespVO> getCompany() {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getStatus, "0");
        List<Company> companies = companyService.list(wrapper);
        CompanyGetRespVO companyGetRespVO = new CompanyGetRespVO();
        org.springframework.beans.BeanUtils.copyProperties(companies, companyGetRespVO);
        return BeanUtils.toBean(companies, CompanyGetRespVO.class);
    }

    @GetMapping("/page")
    @Operation(summary = "获取公司信息page")
    public PaginationGrid<CompanyGetRespVO> getCompany(CompanyPageReqVO pageReqVO) {
        LambdaQueryWrapper<Company> wapper = new LambdaQueryWrapper<>();
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getCompanyName()), Company::getCompanyName, pageReqVO.getCompanyName());
        wapper.eq(!Objects.isNull(pageReqVO.getCompanyId()), Company::getCompanyId, pageReqVO.getCompanyId());
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getStatus()), Company::getStatus, pageReqVO.getStatus());
        // 剔除临时公司
        wapper.ne(Company::getCompanyId, 0L);
        wapper.ne(Company::getOrgCode, "0");
        IPage<Company> pageReq = Page.of(pageReqVO.getPage(), pageReqVO.getPageSize());
        IPage<Company> companyPage = companyService.page(pageReq, wapper);
        List<CompanyGetRespVO> companyList = BeanUtils.toBean(companyPage.getRecords(), CompanyGetRespVO.class);
        PaginationGrid<CompanyGetRespVO> paginationGrid =new PaginationGrid<>(pageReqVO.getPage(), pageReqVO.getPageSize());
        paginationGrid.setTotal(companyPage.getTotal());
        paginationGrid.setRows(companyList);
        return paginationGrid;
    }

    @GetMapping("/simple")
    @Operation(summary = "获取公司简单信息")
    public List<CompanyGetRespVO> getSimpleCompany(CompanyPageReqVO pageReqVO) {
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

    @GetMapping("/export")
    @AuditLog(value = "导出公司信息", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出公司信息")
    public void exportCompany(CompanyPageReqVO pageReqVO, HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("公司信息-" + new SimpleDateFormat("yyyyMMddhhmmss")
                        .format(System.currentTimeMillis()), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        List<Company> list = companyService.list(listCompanyQueryWrapper(pageReqVO));
        List<CompanyDeliveryExcelVO> excelDataList = transCompanyToExcelVOs(list);
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), CompanyDeliveryExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("公司信息")
                .doWrite(excelDataList);
    }

    @GetMapping("/download")
    @AuditLog(value = "导出公司信息模板", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出自公司信息模板")
    public void exportTemplateCompany(HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("公司信息模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), CompanyDeliveryExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("公司信息")
                .doWrite(new ArrayList<>());
    }

    @PostMapping("/import")
    @AuditLog(value = "导入公司信息报表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    @Operation(summary = "导入公司信息报表")
    public ResultData<ExcelResultData<List<CompanyVerifyExcelDTO>>> importsCompany(MultipartFile file) throws IOException {
        CompanyImportListener listener = new CompanyImportListener(companyService);
        EasyExcel.read(file.getInputStream(), CompanyDeliveryExcelVO.class, listener)
                .headRowNumber(1)
                .sheet()
                .doRead();
        // 形成数据
        List<CompanyVerifyExcelDTO> verifyExcelList = listener.getResult();
        if (CollectionUtils.isEmpty(verifyExcelList)) {
            throw new BizException("导入文件不能为空");
        }
        List<Company> companyList = transExcelVOToCompanys(verifyExcelList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("company", companyList);
        map.put("excel", verifyExcelList);
        //存入redis
        String redisKey = REDIS_PREFIX + idKeyGenerator.getId();
        String jsonStr = JSON.toJSONString(map);
        valueOperations.set(redisKey, jsonStr, 3600, TimeUnit.SECONDS);
        ExcelResultData<List<CompanyVerifyExcelDTO>> build = ExcelResultData.build(redisKey, verifyExcelList);
        // 验证是否有失败的数据
        List<String> validateStatus = verifyExcelList.stream().map(CompanyVerifyExcelDTO::getValidateStatus).filter(status -> StatusEnum.N.name().equals(status)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(validateStatus)) {
            return ResultData.success(build);
        }
        return ResultData.fail(ReturnCode.EXCEL_ERROR.getCode(), ReturnCode.EXCEL_ERROR.getMessage(), build);
    }

    @GetMapping("/download/data")
    @AuditLog(value = "下载导入验证结果", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "下载导入验证结果")
    public void downloadVerifyResult(HttpServletResponse response, @RequestParam String redisKey) throws IOException {
        String redisData = (String) valueOperations.get(redisKey);
        // 反序列化
        HashMap<String, Object> map = JSON.parseObject(redisData,
                new TypeReference<HashMap<String, Object>>() {
                });
        // 获取数据
        List<CompanyVerifyExcelDTO> list = JSON.parseObject(
                JSON.toJSONString(map.get("excel")),
                new TypeReference<List<CompanyVerifyExcelDTO>>() {
                });
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("人员导入验证结果下载", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), CompanyVerifyExcelDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("人员导入验证结果下载")
                .doWrite(list);
    }

    /**
     * 验证数据入库
     *
     * @param redisKey redis缓存主键
     */
    @Transactional
    @AutoIdempotent(seconds = 30)
    @AuditLog(value = "验证数据入库", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    @GetMapping("/import/data")
    public void importData(@RequestParam String redisKey) {
        String redisData = (String) valueOperations.get(redisKey);
        // 反序列化
        HashMap<String, Object> map = JSON.parseObject(redisData,
                new TypeReference<HashMap<String, Object>>() {
                });
        // 获取数据
        List<Company> companyList = JSON.parseObject(
                JSON.toJSONString(map.get("company")),
                new TypeReference<List<Company>>() {
                });

        if(companyList != null && companyList.size() > 0) {
            boolean result = companyService.saveBatch(companyList);
            if (!result) {
                throw new BizException("公司数据写入失败！");
            }
        }
    }

    private LambdaQueryWrapper listCompanyQueryWrapper(CompanyPageReqVO pageReqVO){
        LambdaQueryWrapper<Company> wapper = new LambdaQueryWrapper<>();
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getCompanyName()), Company::getCompanyName, pageReqVO.getCompanyName());
        wapper.eq(!Objects.isNull(pageReqVO.getCompanyId()), Company::getCompanyId, pageReqVO.getCompanyId());
        wapper.eq(!StrUtil.isEmpty(pageReqVO.getStatus()), Company::getStatus, pageReqVO.getStatus());
        // 剔除临时公司
        wapper.ne(Company::getCompanyId, 0L);
        wapper.ne(Company::getOrgCode, "0");
        return wapper;
    }

    private List<CompanyDeliveryExcelVO> transCompanyToExcelVOs(List<Company> list) {
        List<CompanyDeliveryExcelVO> excelVOList = new ArrayList<>();
        list.forEach(data -> {
            CompanyStatusEnum companyStatusEnum = Arrays.stream(CompanyStatusEnum.values())
                    .filter(x -> x.getValue().equals(data.getStatus()))
                    .findFirst()
                    .orElse(null);
            CompanyDeliveryExcelVO excelVO = new CompanyDeliveryExcelVO();
            excelVO.setCompanyId(String.valueOf(data.getCompanyId()));
            excelVO.setOrgCode(data.getOrgCode());
            excelVO.setCompanyName(data.getCompanyName());
            excelVO.setPlantCode(data.getPlantCode());
            excelVO.setRemark(data.getRemark());
            excelVO.setStatus(companyStatusEnum == null ? null : companyStatusEnum.getText());
            excelVOList.add(excelVO);
        });
        return excelVOList;
    }

    private List<Company> transExcelVOToCompanys(List<CompanyVerifyExcelDTO> list) {
        List<Company> companyList = new ArrayList<>();
        list.forEach(data -> {

            Company company = new Company();
            if(StringUtils.isNotBlank(data.getCompanyId()) && CompanyImportListener.isLongNumber(data.getCompanyId())) {
                company.setCompanyId(Long.parseLong(data.getCompanyId()));
            }
            company.setOrgCode(data.getOrgCode());
            company.setCompanyName(data.getCompanyName());
            company.setPlantCode(data.getPlantCode());
            company.setRemark(data.getRemark());
            if(StringUtils.isNotBlank(data.getStatus())) {
                CompanyStatusEnum companyStatusEnum = Arrays.stream(CompanyStatusEnum.values())
                        .filter(x -> x.getText().equals(data.getStatus()))
                        .findFirst()
                        .orElse(null);
                company.setStatus(companyStatusEnum == null ? null : companyStatusEnum.getValue());
            }
            companyList.add(company);
        });
        return companyList;
    }
}

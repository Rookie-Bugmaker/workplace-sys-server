package com.crrc.sys.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.common.ReturnCode;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.convert.DeptConvert;
import com.crrc.sys.convert.SysOrgPersonConvert;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.excel.ExcelResultData;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.entity.user.UserExcelEntity;
import com.crrc.sys.enums.*;
import com.crrc.sys.listener.SysOrgPersonImportListener;
import com.crrc.sys.model.dept.DeptInfoDTO;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.model.org.person.SysOrgPersonDTO;
import com.crrc.sys.model.org.person.SysOrgPersonDeliveryExcelVO;
import com.crrc.sys.model.org.person.SysOrgPersonVerifyExcelDTO;
import com.crrc.sys.service.*;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 组织架构-人员-控制器
 *
 * @author 谭永强
 * @date 2022-12-09
 */
@RestController
@RequestMapping("/org/person/")
public class SysOrgPersonController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysOrgPersonService orgPersonService;
    @Resource
    private SysUserService userService;
    @Resource
    private SysDeptPersonService deptPersonService;
    @Resource
    private DeptService deptService;
    @Resource
    private CompanyService companyService;
    @Resource
    private ValueOperations<String, Object> valueOperations;

    private static final String REDIS_PREFIX = "import_person_data";

    /**
     * 人员分页查询(Copy)
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage/cp")
    public PaginationGrid<SysOrgPerson> findByPageCopy(SysOrgPersonFindByPageParam param) {
        return findByPage(param);
    }

    /**
     * 人员分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysOrgPerson> findByPage(SysOrgPersonFindByPageParam param) {
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
        if (!ObjectUtils.isEmpty(param.getPersonType())) {
            param.setPersonTypeList(Arrays.stream(param.getPersonType().split(",")).map(Long::parseLong).collect(Collectors.toList()));
        }
        return orgPersonService.findByPage(param);
    }

    /**
     * 人员新增
     *
     * @param person 人员
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(SysOrgPerson person) {
        verification(OperationTypeEnum.ADD.getValue(), person);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        person.setPersonId(idKeyGenerator.getId());
        //创建人
        person.setCreateBy(currentUser.getUserId());
        //创建时间
        person.setCreateDate(Calendar.getInstance().getTime());
        int result = orgPersonService.add(person);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 人员修改
     *
     * @param person 人员
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysOrgPerson person) {
        verification(OperationTypeEnum.UPDATE.getValue(), person);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //创建人
        person.setLastUpdateBy(currentUser.getUserId());
        //创建时间
        person.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改人登录日志
        person.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        int result = orgPersonService.update(person);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param person    人员
     */
    private void verification(String operation, SysOrgPerson person) {
        if (ObjectUtils.isEmpty(person)) {
            throw new ParamValidateException("参数错误");
        }
        //移除语言中的逗号
        person.setLanguage(person.getLanguage().contains(",") ? person.getLanguage().replace(",", "") : person.getLanguage());
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(person.getPersonId())) {
                throw new ParamValidateException("personId不能为空!");
            }
        }
        if (ObjectUtils.isEmpty(person.getJobNo())) {
            throw new ParamValidateException("工号不能为空!");
        }
        if (person.getJobNo().length() > 20) {
            throw new ParamValidateException("工号超长(最大20)!");
        }
        // 根据工号查询人员信息
        SysOrgPerson byJobNo = orgPersonService.findByJobNo(person.getJobNo());
        if (OperationTypeEnum.ADD.getValue().equals(operation)) {
            if (!ObjectUtils.isEmpty(byJobNo)) {
                throw new ParamValidateException("工号已存在");
            }
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation) && !byJobNo.getPersonId().equals(person.getPersonId())) {
            if (!ObjectUtils.isEmpty(byJobNo)) {
                throw new ParamValidateException("工号已存在");
            }
        }
        if (ObjectUtils.isEmpty(person.getFullName())) {
            throw new ParamValidateException("姓名不能为空!");
        }
        if (person.getFullName().length() > 30) {
            throw new ParamValidateException("姓名超长(最大30)!");
        }
        if (ObjectUtils.isEmpty(person.getGender())) {
            throw new ParamValidateException("性别不能为空!");
        }
        if (!GenderEnum.contains(person.getGender())) {
            throw new ParamValidateException("性别参数错误!");
        }
        if (!ObjectUtils.isEmpty(person.getPhone()) && person.getPhone().length() > 11) {
            throw new ParamValidateException("手机超长(最大11)!");
        }
        //邮箱校验
        String emailReg = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        if (!ObjectUtils.isEmpty(person.getEmail()) && !person.getEmail().matches(emailReg)) {
            throw new ParamValidateException("邮箱格式错误!");
        }
        if (person.getEmail().length() > 50) {
            throw new ParamValidateException("邮箱超长(最大50)!");
        }
        if (ObjectUtils.isEmpty(person.getStatus())) {
            throw new ParamValidateException("人员状态不能为空");
        }
    }

    /**
     * 人员删除
     *
     * @param personIds 人员ID，多个以逗号","拼接
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String personIds) {
        if (ObjectUtils.isEmpty(personIds)) {
            throw new ParamValidateException("personIds不能为空");
        }
        List<Long> idList = Arrays.stream(personIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        int result = orgPersonService.delete(idList);
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 人员列表查询
     *
     * @param param 查询条件
     * @return 结果
     */
    @PostMapping("findList")
    public List<SysOrgPerson> findList(@RequestBody SysOrgPersonFindByPageParam param) {
        return orgPersonService.findList(param);
    }

    /**
     * 人员新增(批量)
     *
     * @param persons 人员信息
     * @return 结果
     */
    @PostMapping("addList")
    public Boolean add(@RequestBody List<SysOrgPerson> persons) {
        if (CollectionUtils.isEmpty(persons)) {
            throw new ParamValidateException("参数不能为空!");
        }
        Date now = Calendar.getInstance().getTime();
        persons.forEach(o -> {
            o.setPersonId(idKeyGenerator.getId());
            o.setCreateBy(-1L);
            o.setCreateDate(now);
        });
        return orgPersonService.addList(persons);
    }

    /**
     * 人员修改
     *
     * @param persons 人员列表
     */
    @AutoIdempotent
    @PostMapping("updateList")
    public Boolean updateList(@RequestBody List<SysOrgPerson> persons) {
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        for (int i = 0; i < persons.size(); i++) {
            SysOrgPerson person = persons.get(i);
            //创建人
            person.setLastUpdateBy(currentUser.getUserId());
            //创建时间
            person.setLastUpdateDate(Calendar.getInstance().getTime());
            //最后修改人登录日志
            person.setLastUpdateLogin(currentUser.getLastLoginInfoId());
            int result = orgPersonService.update(person);
            if (result <= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据工号查询人员信息
     *
     * @param jobNo 工号
     * @return 人员实体
     */
    @PostMapping("findByJobNo")
    public SysOrgPerson findByJobNo(@RequestParam("jobNo") String jobNo) {
        return orgPersonService.findByJobNo(jobNo);
    }

    /**
     * 根据工号列表查询人员信息
     *
     * @param jobNos 工号列表
     * @return 结果
     */
    @PostMapping("findByJobNos")
    public List<SysOrgPerson> findByJobNos(@RequestBody List<String> jobNos) {
        return orgPersonService.findByJobNos(jobNos);
    }

    /**
     * 离职日期监控(当查询到离职时间<=当前时间，则修改状态为离职)
     * 该接口提供给定时任务访问
     */
    @PostMapping("expirationTateMonitor")
    public void expirationTateMonitor() {
        //查询[离职时间<=当前时间]的人员
        List<SysOrgPerson> rows = orgPersonService.findByQuitDate(Calendar.getInstance().getTime());
        if (!CollectionUtils.isEmpty(rows)) {
            List<Long> personIdList = rows.stream().map(SysOrgPerson::getPersonId).collect(Collectors.toList());
            //修改人员状态
            orgPersonService.updateStatus(personIdList, 2);
        }
    }

    /**
     * 根据当前用户查询人员信息
     *
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByCurrentUserNew")
    public SysOrgPersonDTO findByCurrentUserNew(String language) {
        return findByCurrentUser(language);
    }

    /**
     * 根据当前用户查询人员信息
     *
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByCurrentUser")
    public SysOrgPersonDTO findByCurrentUser(String language) {
        if (ObjectUtils.isEmpty(language)) {
            language = LanguageEnum.ZH_CN.getValue();
        }
        ResultData<SysUser> currentUserResultData = authClient.currentUser();
        SysUser currentUser = currentUserResultData.getResult();
        if (!currentUserResultData.getSuccess() || ObjectUtils.isEmpty(currentUser)) {
            throw new BizException("当前用户获取失败!");
        }
        return findByUserId(currentUser.getUserId(), language);
    }

    /**
     * 根据用户ID查询人员信息
     *
     * @param userId   人员ID
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByUserId")
    public SysOrgPersonDTO findByUserId(Long userId, String language) {
        if (ObjectUtils.isEmpty(userId)) {
            throw new ParamValidateException("userId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            language = LanguageEnum.ZH_CN.getValue();
        }
        SysUser sysUser = userService.findById(userId, language);
        if (ObjectUtils.isEmpty(sysUser)) {
            throw new BizException("用户不存在!");
        }
        if (ObjectUtils.isEmpty(sysUser.getUserRelationId())) {
            throw new BizException("用户未关联员工!");
        }
        return findById(sysUser.getUserRelationId());
    }

    /**
     * 根据人员ID查询人员信息
     *
     * @param personId 人员ID
     * @return 人员信息
     */
    @PostMapping("findById")
    public SysOrgPersonDTO findById(Long personId) {
        if (ObjectUtils.isEmpty(personId)) {
            throw new ParamValidateException("参数不能为空");
        }
        List<SysOrgPersonDTO> personList = findByIds(Collections.singletonList(personId));
        if (CollectionUtils.isEmpty(personList)) {
            return null;
        }
        return personList.get(0);
    }

    /**
     * 根据人员ID查询人员信息(批量)
     *
     * @param personIdList 人员ID集合
     * @return 人员列表
     */
    @PostMapping("findByIds")
    public List<SysOrgPersonDTO> findByIds(@RequestBody List<Long> personIdList) {
        if (CollectionUtils.isEmpty(personIdList)) {
            throw new ParamValidateException("参数不能为空");
        }
        List<SysOrgPerson> personList = orgPersonService.findByIds(personIdList);
        if (CollectionUtils.isEmpty(personList)) {
            return null;
        }
        List<SysOrgPersonDTO> personDtoList = SysOrgPersonConvert.INSTANCE.orgPersonToDto(personList);
        List<Long> nowPersonIdList = personList.stream().map(SysOrgPerson::getPersonId).collect(Collectors.toList());
        //查询人员部门列表
        List<SysDeptPersonPO> deptPersonList = deptPersonService.findByPersonId(nowPersonIdList);
        if (CollectionUtils.isEmpty(deptPersonList)) {
            return personDtoList;
        }
        List<Long> deptIdList = deptPersonList.stream().map(SysDeptPersonPO::getDeptId).distinct().collect(Collectors.toList());
        //查询部门信息
        List<Dept> deptList = deptService.findByDeptIds(deptIdList);
        if (CollectionUtils.isEmpty(deptList)) {
            return personDtoList;
        }
        List<DeptInfoDTO> deptInfoList = DeptConvert.INSTANCE.deptToInfoDto(deptList);
        //查询公司信息
        List<Long> companyIdList = deptList.stream().map(Dept::getCompanyId).distinct().collect(Collectors.toList());
        List<Company> companyList = companyService.listByIds(companyIdList);
        if (!CollectionUtils.isEmpty(companyList)) {
            deptInfoList.forEach(p -> {
                Company company = companyList.stream().filter(o -> o.getCompanyId().equals(p.getCompanyId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(company)) {
                    p.setCompany(company);
                }
            });
        }
        //绑定人员部门
        personDtoList.forEach(p -> {
            //查询该人员绑定了哪些部门
            List<Long> deptIdList1 = deptPersonList.stream().filter(o -> o.getPersonId().equals(p.getPersonId())).map(SysDeptPersonPO::getDeptId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deptIdList1)) {
                List<DeptInfoDTO> deptInfoDtoList = deptInfoList.stream().filter(o -> deptIdList1.contains(o.getId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(deptInfoDtoList)) {
                    p.setDeptList(deptInfoDtoList);
                }
            }
        });
        return personDtoList;
    }

    @GetMapping("/export")
    @AuditLog(value = "导出人员信息", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出人员信息")
    public void exportSysPerson(SysOrgPersonFindByPageParam exportReqVO, HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("人员信息-" + new SimpleDateFormat("yyyyMMddhhmmss")
                        .format(System.currentTimeMillis()), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        exportReqVO = (exportReqVO == null) ? new SysOrgPersonFindByPageParam() : exportReqVO;
        List<SysOrgPerson> personList = orgPersonService.findList(exportReqVO);
        List<SysOrgPersonDeliveryExcelVO> personExcelVOS = transPersonToExcelVOS(personList);
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), SysOrgPersonDeliveryExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("人员信息")
                .doWrite(personExcelVOS);
    }

    @GetMapping("/download")
    @AuditLog(value = "导出人员信息模板", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出自人员信息模板")
    public void exportTemplateSysPerson(HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("人员信息模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), SysOrgPersonDeliveryExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("人员信息")
                .doWrite(new ArrayList<>());
    }

    @PostMapping("/import")
    @AuditLog(value = "导入人员信息报表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    @Operation(summary = "导入人员信息报表")
    public ResultData<ExcelResultData<List<SysOrgPersonVerifyExcelDTO>>> importsSysPerson(MultipartFile file) throws IOException {
        SysOrgPersonImportListener listener = new SysOrgPersonImportListener(orgPersonService, companyService, deptService);
        EasyExcel.read(file.getInputStream(), SysOrgPersonDeliveryExcelVO.class, listener)
                .headRowNumber(1)
                .sheet()
                .doRead();
        // 形成数据
        List<SysOrgPersonVerifyExcelDTO> verifyExcelList = listener.getResult();
        if (CollectionUtils.isEmpty(verifyExcelList)) {
            throw new BizException("导入文件不能为空");
        }
        List<SysOrgPerson> orgPersonList = transExcelVOsToOrgPerson(verifyExcelList);
        Map<String, Long> jobNoToPersonIdMap = orgPersonList.stream()
                .filter(person -> person.getJobNo() != null)  // 过滤null
                .collect(Collectors.toMap(
                        SysOrgPerson::getJobNo,
                        SysOrgPerson::getPersonId
                ));
        List<SysDeptPersonPO> deptPersonList = transExcelVOsToDeptPerson(jobNoToPersonIdMap, verifyExcelList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("person", orgPersonList);
        map.put("excel", verifyExcelList);
        map.put("deptPerson", deptPersonList);
        //存入redis
        String redisKey = REDIS_PREFIX + idKeyGenerator.getId();
        String jsonStr = JSON.toJSONString(map);
        valueOperations.set(redisKey, jsonStr, 3600, TimeUnit.SECONDS);
        ExcelResultData<List<SysOrgPersonVerifyExcelDTO>> build = ExcelResultData.build(redisKey, verifyExcelList);
        // 验证是否有失败的数据
        List<String> validateStatus = verifyExcelList.stream().map(SysOrgPersonVerifyExcelDTO::getValidateStatus).filter(status -> StatusEnum.N.name().equals(status)).collect(Collectors.toList());
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
        List<SysOrgPersonVerifyExcelDTO> list = JSON.parseObject(
                JSON.toJSONString(map.get("excel")),
                new TypeReference<List<SysOrgPersonVerifyExcelDTO>>() {
                });
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("人员导入验证结果下载", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), SysOrgPersonVerifyExcelDTO.class)
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
    @AuditLog(value = "下载导入验证结果", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    @GetMapping("/import/data")
    public void importData(@RequestParam String redisKey) {
        String redisData = (String) valueOperations.get(redisKey);
        // 反序列化
        HashMap<String, Object> map = JSON.parseObject(redisData,
                new TypeReference<HashMap<String, Object>>() {
                });
        // 获取数据
        List<SysOrgPerson> personList = JSON.parseObject(
                JSON.toJSONString(map.get("person")),
                new TypeReference<List<SysOrgPerson>>() {
                });
        List<SysDeptPersonPO> deptPersonList = JSON.parseObject(
                JSON.toJSONString(map.get("deptPerson")),
                new TypeReference<List<SysDeptPersonPO>>() {
                });

        if(personList != null && personList.size() > 0) {
            boolean result = orgPersonService.addList(personList);
            if (!result) {
                throw new BizException("人员数据写入失败！");
            }
        }

        if(deptPersonList != null && deptPersonList.size() > 0) {
            boolean result = deptPersonService.saveBatch(deptPersonList);
            if (!result) {
                throw new BizException("人员部门关联关系写入失败！");
            }
        }
    }

    private List<SysDeptPersonPO> transExcelVOsToDeptPerson(Map<String, Long> jobNoToPersonIdMap, List<SysOrgPersonVerifyExcelDTO> excelVOs) {
        List<SysDeptPersonPO> deptPersonList = new ArrayList<>();
        excelVOs.forEach(excelVO -> {
            // 获取当前用户
            ResultData<SysUser> resultData = authClient.currentUser();
            if (!resultData.getSuccess()) {
                throw new BizException(resultData.getMessage());
            }
            // 只有存在公司和部门才保存关联关系
            if (StringUtils.isNotBlank(excelVO.getCompanyCode()) && StringUtils.isNotBlank(excelVO.getDeptCode())) {
                // 获取公司部门和直属上级
                Company company = getCompanyByCode(excelVO.getCompanyCode());
                Dept dept = getDeptByCompanyIdAndCode(company.getId(), excelVO.getDeptCode());
                SysOrgPerson parentPerson = orgPersonService.findByJobNo(excelVO.getParentPersonCode());

                SysDeptPersonPO deptPerson = new SysDeptPersonPO();
                deptPerson.setId(idKeyGenerator.getId());
                deptPerson.setCompanyId(company.getId());
                deptPerson.setDeptId(dept.getId());
                deptPerson.setPersonId(jobNoToPersonIdMap.get(excelVO.getJobNo()));
                deptPerson.setPosition(excelVO.getPosition());
                deptPerson.setParentPersonId(parentPerson.getPersonId());
                deptPersonList.add(deptPerson);
            }
        });
        return deptPersonList;
    }

    private List<SysOrgPersonDeliveryExcelVO> transPersonToExcelVOS(List<SysOrgPerson> personList) {

        // 批量查询人员关联的公司和部门信息
        List<SysDeptPersonPO> deptPersonList = deptPersonService.list();

        // 一次性获取所有公司和部门的OrgCode映射
        Map<Long, String> companyIdToCodeMap = getCompanyCodeByIds(
                deptPersonList.stream().map(SysDeptPersonPO::getCompanyId).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Company::getId, Company::getOrgCode));

        Map<Long, String> deptIdToCodeMap = getDeptCodeByIds(
                deptPersonList.stream().map(SysDeptPersonPO::getDeptId).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Dept::getId, Dept::getOrgCode));

        // 分组映射
        Map<Long, List<SysDeptPersonPO>> personToDeptRelations = deptPersonList.stream()
                .collect(Collectors.groupingBy(SysDeptPersonPO::getPersonId));
        // 获取直属上级映射
        Map<Long, String> parentPersonIdToJobNo = orgPersonService.findByIds(
                deptPersonList.stream().map(SysDeptPersonPO::getParentPersonId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(SysOrgPerson::getPersonId, SysOrgPerson::getJobNo));

        List<SysOrgPersonDeliveryExcelVO> excelVOList = new ArrayList<>();
        personList.forEach(person -> {
            PersonTypeEnum personTypeEnum = Arrays.stream(PersonTypeEnum.values())
                    .filter(x -> x.getValue().equals(String.valueOf(person.getPersonType())))
                    .findFirst()
                    .orElse(null);
            PersonStatusEnum personStatusEnum = Arrays.stream(PersonStatusEnum.values())
                    .filter(x -> x.getValue().equals(String.valueOf(person.getStatus())))
                    .findFirst()
                    .orElse(null);
            GenderEnum genderEnum = Arrays.stream(GenderEnum.values())
                    .filter(x -> x.getValue().equals(person.getGender()))
                    .findFirst()
                    .orElse(null);
            // 获取公司部门和直属上级
            SysOrgPersonDeliveryExcelVO excelVO = new SysOrgPersonDeliveryExcelVO();
            excelVO.setFullName(person.getFullName());
            excelVO.setJobNo(person.getJobNo());
            excelVO.setPersonType(personTypeEnum == null ? null : personTypeEnum.getText());
            excelVO.setStatus(personStatusEnum.getText());
            excelVO.setGender(genderEnum.getText());
            excelVO.setPhone(person.getPhone());
            excelVO.setEmail(person.getEmail());
            excelVO.setInductionDate(person.getInductionDate());
            excelVO.setQuitDate(person.getQuitDate());
            // 获取该人员所有部门关联关系
            List<SysDeptPersonPO> relations = personToDeptRelations.get(person.getPersonId());
            if (relations != null && relations.size() > 0) {
                // 为每个关联关系生成独立记录
                relations.forEach(relation -> {
                    // 设置额外信息
                    SysOrgPersonDeliveryExcelVO repeatExcelVo = new SysOrgPersonDeliveryExcelVO();
                    BeanUtils.copyProperties(excelVO, repeatExcelVo);
                    repeatExcelVo.setCompanyCode(companyIdToCodeMap.get(relation.getCompanyId()));
                    repeatExcelVo.setDeptCode(deptIdToCodeMap.get(relation.getDeptId()));
                    repeatExcelVo.setParentPersonCode(parentPersonIdToJobNo.get(person.getPersonId()));
                    repeatExcelVo.setPosition(relation.getPosition());
                    excelVOList.add(repeatExcelVo);
                });
            }else {
                excelVOList.add(excelVO);
            }
        });
        return excelVOList;
    }

    private List<SysOrgPerson> transExcelVOsToOrgPerson(List<SysOrgPersonVerifyExcelDTO> excelVOs) {
        List<SysOrgPerson> orgPersonList = new ArrayList<>();
        List<String> existedJobNo = new ArrayList<>();
        for (int i = 0; i < excelVOs.size(); i++) {
            SysOrgPersonVerifyExcelDTO excelVO = excelVOs.get(i);
            if(StringUtils.isNotBlank(excelVO.getJobNo())){
                // 去除重复的人员数据
                if(existedJobNo.contains(excelVO.getJobNo())){
                    continue;
                }else {
                    existedJobNo.add(excelVO.getJobNo());
                }
            }
            // 获取当前用户
            ResultData<SysUser> resultData = authClient.currentUser();
            if (!resultData.getSuccess()) {
                throw new BizException(resultData.getMessage());
            }
            SysOrgPerson person = new SysOrgPerson();
            person.setFullName(excelVO.getFullName());
            person.setJobNo(excelVO.getJobNo());
            person.setPhone(excelVO.getPhone());
            person.setEmail(excelVO.getEmail());
            person.setInductionDate(excelVO.getInductionDate());
            person.setQuitDate(excelVO.getQuitDate());
            person.setPersonId(idKeyGenerator.getId());
            person.setLanguage(LanguageEnum.ZH_CN.getValue());
            person.setCreateBy(resultData.getResult().getUserId());
            person.setCreateDate(Calendar.getInstance().getTime());
            if(StringUtils.isNotBlank(excelVO.getPersonType())) {
                // 人员类型
                PersonTypeEnum personTypeEnum = Arrays.stream(PersonTypeEnum.values())
                        .filter(x -> x.getText().equals(excelVO.getPersonType()))
                        .findFirst()
                        .orElse(null);
                person.setPersonType(Long.valueOf(personTypeEnum.getValue()));
            }
            if(StringUtils.isNotBlank(excelVO.getStatus())) {
                // 人员状态
                PersonStatusEnum statusEnum = Arrays.stream(PersonStatusEnum.values())
                        .filter(x -> x.getText().equals(excelVO.getStatus()))
                        .findFirst()
                        .orElse(null);
                person.setStatus(Integer.valueOf(statusEnum.getValue()));
            }
            if(StringUtils.isNotBlank(excelVO.getGender())) {
                // 性别
                GenderEnum genderEnum = Arrays.stream(GenderEnum.values())
                        .filter(x -> x.getText().equals(excelVO.getGender()))
                        .findFirst()
                        .orElse(null);
                person.setGender(genderEnum.getValue());
            }
            orgPersonList.add(person);
        }
        return orgPersonList;
    }

    private Company getCompanyByCode(String code) {
        return companyService.getOne(
                new LambdaQueryWrapper<Company>()
                        .eq(Company::getOrgCode, code));
    }

    private Dept getDeptByCompanyIdAndCode(Long companyId, String deptCode) {
        return deptService.getOne(
                new LambdaQueryWrapper<Dept>()
                        .eq(Dept::getCompanyId, companyId)
                        .eq(Dept::getOrgCode, deptCode));
    }

    private List<Company> getCompanyCodeByIds(List<Long> companyIds) {
        return companyService.list(
                new LambdaQueryWrapper<Company>()
                        .select(Company::getOrgCode, Company::getId)
                        .in(Company::getId, companyIds));
    }

    private List<Dept> getDeptCodeByIds(List<Long> deptIds) {
        return deptService.list(
                new LambdaQueryWrapper<Dept>()
                        .select(Dept::getOrgCode, Dept::getId)
                        .in(Dept::getId, deptIds));
    }
}
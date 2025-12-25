package com.crrc.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.common.ReturnCode;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.excel.ExcelResultData;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.*;
import com.crrc.sys.listener.CompanyImportListener;
import com.crrc.sys.listener.DeptImportListener;
import com.crrc.sys.model.company.CompanyDeliveryExcelVO;
import com.crrc.sys.model.company.CompanyPageReqVO;
import com.crrc.sys.model.company.CompanyVerifyExcelDTO;
import com.crrc.sys.model.dept.*;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
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

@Tag(name = "系统管理 - 部门管理")
@RestController
@Slf4j
@RequestMapping("dept")
public class DeptController {
    @Autowired
    private DeptService deptService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysOrgPersonService personService;
    @Autowired
    private SysDeptPersonService deptPersonService;
    @Autowired
    private CompanyService companyService;

    @Resource
    private ValueOperations<String, Object> valueOperations;

    @Resource
    private IdKeyGenerator idKeyGenerator;

    private static final String REDIS_PREFIX = "import_dept_data";

    @PostMapping("/create")
    @Operation(summary = "创建部门")
    @AuditLog(value = "创建菜单", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Long createDept(@Valid @RequestBody DeptCreateReqVO createReqVO) {
        if (ObjectUtils.isEmpty(createReqVO.getOrgCode())) {
            throw new BizException("部门编码不能为空");
        }
        Dept deptOld = deptService.findByCode(createReqVO.getOrgCode());
        if (!ObjectUtils.isEmpty(deptOld)) {
            throw new BizException("该部门编码已存在!");
        }
        Dept dept = BeanUtils.toBean(createReqVO, Dept.class);
        deptService.save(dept);
        return dept.getId();
    }

    @PostMapping("/update")
    @Operation(summary = "更新部门")
    @AuditLog(value = "更新部门", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    public Long updateDept(@Valid @RequestBody DeptUpdateReqVO updateReqVO) {
        if (ObjectUtils.isEmpty(updateReqVO.getId())) {
            throw new BizException("部门ID不能为空");
        }
        if (ObjectUtils.isEmpty(updateReqVO.getOrgCode())) {
            throw new BizException("部门编码不能为空");
        }
        Dept deptOld = deptService.findByCode(updateReqVO.getOrgCode());
        if (!ObjectUtils.isEmpty(deptOld) && !deptOld.getId().equals(updateReqVO.getId())) {
            throw new BizException("该部门编码已存在!");
        }
        Dept dept = BeanUtils.toBean(updateReqVO, Dept.class);
        Dept deptInDB = deptService.getById(dept.getId());
        if (deptInDB == null) {
            throw new BizException("部门不存在");
        }
        deptService.updateById(dept);
        return dept.getId();
    }

    @PostMapping("/list_vo")
    public List<DeptListRespVO> listNow(@Valid @RequestBody DeptqueryVo deptqueryVo) {
        return listDept(deptqueryVo);
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
        deptList.removeIf(o -> o.getCompanyId() == 0 || o.getOrgCode().equals("-".concat(String.valueOf(o.getCompanyId()))));
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


    @GetMapping("/get_depts_by_user_id")
    @Operation(summary = "通过用户id查询部门 - OpenFegin接口")
    public List<DeptOfUserDTO> getDeptofUser(@RequestParam List<Long> ids) {
        List<DeptOfUserDTO> deptOfUserDTOs = new ArrayList<>();
        List<SysUser> userList = userService.findByUserIds(ids);
        Map<Long, Long> personIdToUserIdMap = userList.stream()
                .filter(user -> user.getUserRelationId() != null)  // 过滤掉userRelationId为null的条目
                .collect(Collectors.toMap(
                        SysUser::getUserRelationId,
                        SysUser::getUserId,
                        (oldVal, newVal) -> oldVal
                ));
        List<Long> personIds = userList.stream().map(SysUser::getUserRelationId).collect(Collectors.toList());
        List<SysDeptPersonPO> deptPersonPOs = deptPersonService.findByPersonId(personIds);
        deptPersonPOs.forEach(deptPersonPO -> {
            List<Dept> deptList = deptService.findByDeptIds(Arrays.asList(deptPersonPO.getDeptId()));
            if (deptList != null && deptList.size() > 0) {
                DeptOfUserDTO deptOfUserDTO = new DeptOfUserDTO();
                deptOfUserDTO.setId(deptPersonPO.getDeptId());
                deptOfUserDTO.setUserId(personIdToUserIdMap.get(deptPersonPO.getPersonId()));
                deptOfUserDTO.setDepartmentName(deptList.get(0).getDepartmentName());
                deptOfUserDTO.setParentOrgCode(deptList.get(0).getParentOrgCode());
                deptOfUserDTO.setDepartmentOrder(deptList.get(0).getDepartmentOrder());
                deptOfUserDTO.setRemark(deptList.get(0).getRemark());
                deptOfUserDTO.setStatus(deptList.get(0).getStatus());
                deptOfUserDTO.setDeleted("0".equals(deptList.get(0).getDeleted()) ? false : true);
                deptOfUserDTO.setOrgCode(deptList.get(0).getOrgCode());
                deptOfUserDTOs.add(deptOfUserDTO);
            }
        });
        return deptOfUserDTOs;
    }

    @GetMapping("/get_depts_by_user_name")
    @Operation(summary = "通过用户编码查询部门 - OpenFegin接口")
    public List<DeptOfUserDTO> getDeptofUserNameList(@RequestParam List<String> usernames) {
        List<Long> personIds = personService.findByJobNos(usernames).stream().map(SysOrgPerson::getPersonId).collect(Collectors.toList());
        List<Long> userIds = userService.findByRelationIds(personIds).stream().map(SysUser::getUserId).collect(Collectors.toList());
        return getDeptofUser(userIds);
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

    @GetMapping("/export")
    @AuditLog(value = "导出部门信息", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出部门信息")
    public void exportDept(DeptqueryVo deptqueryVo, HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("部门信息-" + new SimpleDateFormat("yyyyMMddhhmmss")
                        .format(System.currentTimeMillis()), "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        List<Dept> list = deptService.list(listDeptQueryWrapper(deptqueryVo));
        // 剔除临时部门
        list.removeIf(o -> o.getCompanyId() == 0 || o.getOrgCode().equals("-".concat(String.valueOf(o.getCompanyId()))));
        List<DeptDeliveryExcelVO> excelDataList = transDeptToExcelVOs(list);
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), DeptDeliveryExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("部门信息")
                .doWrite(excelDataList);
    }

    @GetMapping("/download")
    @AuditLog(value = "导出部门信息模板", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    @Operation(summary = "导出自部门信息模板")
    public void exportTemplateDept(HttpServletResponse response) throws IOException {
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("部门信息模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), DeptDeliveryExcelVO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(true)
                .excelType(typeEnum)
                .sheet("部门信息")
                .doWrite(new ArrayList<>());
    }

    @PostMapping("/import")
    @AuditLog(value = "导入部门信息报表", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_IMPORT)
    @Operation(summary = "导入部门信息报表")
    public ResultData<ExcelResultData<List<DeptVerifyExcelDTO>>> importsDept(MultipartFile file) throws IOException {
        DeptImportListener listener = new DeptImportListener(deptService, companyService);
        EasyExcel.read(file.getInputStream(), DeptDeliveryExcelVO.class, listener)
                .headRowNumber(1)
                .sheet()
                .doRead();
        // 形成数据
        List<DeptVerifyExcelDTO> verifyExcelList = listener.getResult();
        if (org.springframework.util.CollectionUtils.isEmpty(verifyExcelList)) {
            throw new BizException("导入文件不能为空");
        }
        List<Dept> deptList = transExcelVOToDepts(verifyExcelList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("dept", deptList);
        map.put("excel", verifyExcelList);
        //存入redis
        String redisKey = REDIS_PREFIX + idKeyGenerator.getId();
        String jsonStr = JSON.toJSONString(map);
        valueOperations.set(redisKey, jsonStr, 3600, TimeUnit.SECONDS);
        ExcelResultData<List<DeptVerifyExcelDTO>> build = ExcelResultData.build(redisKey, verifyExcelList);
        // 验证是否有失败的数据
        List<String> validateStatus = verifyExcelList.stream().map(DeptVerifyExcelDTO::getValidateStatus).filter(status -> StatusEnum.N.name().equals(status)).collect(Collectors.toList());
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
        List<DeptVerifyExcelDTO> list = JSON.parseObject(
                JSON.toJSONString(map.get("excel")),
                new TypeReference<List<DeptVerifyExcelDTO>>() {
                });
        String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        response.setContentType(contentType);
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode("人员导入验证结果下载", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        ExcelTypeEnum typeEnum = ExcelTypeEnum.XLSX;
        EasyExcel.write(response.getOutputStream(), DeptVerifyExcelDTO.class)
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
        List<Dept> deptList = JSON.parseObject(
                JSON.toJSONString(map.get("dept")),
                new TypeReference<List<Dept>>() {
                });

        if (deptList != null && deptList.size() > 0) {
            boolean result = deptService.saveBatch(deptList);
            if (!result) {
                throw new BizException("部门数据写入失败！");
            }
        }
    }

    private LambdaQueryWrapper listDeptQueryWrapper(DeptqueryVo deptqueryVo){
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        //部门名称模糊查询
        queryWrapper.like(!ObjectUtils.isEmpty(deptqueryVo.getKeywords()), Dept::getDepartmentName, deptqueryVo.getKeywords());
        //根据部门ID集合查询
        queryWrapper.in(!ObjectUtils.isEmpty(deptqueryVo.getDeptIds()), Dept::getId, deptqueryVo.getDeptIds());
        //根据公司ID集合查询
        queryWrapper.in(!ObjectUtils.isEmpty(deptqueryVo.getCompanyIds()), Dept::getCompanyId, deptqueryVo.getCompanyIds());
        //排序
        queryWrapper.orderByAsc(Dept::getCompanyId, Dept::getOrgCode);
        return queryWrapper;
    }

    private List<DeptDeliveryExcelVO> transDeptToExcelVOs(List<Dept> list) {
        List<DeptDeliveryExcelVO> excelVOList = new ArrayList<>();
        list.forEach(data -> {
            DeptStatusEnum deptStatusEnum = Arrays.stream(DeptStatusEnum.values())
                    .filter(x -> x.getValue().equals(data.getStatus()))
                    .findFirst()
                    .orElse(null);
            DeptDeliveryExcelVO excelVO = new DeptDeliveryExcelVO();
            excelVO.setDeptCode(data.getOrgCode());
            excelVO.setDepartmentName(data.getDepartmentName());
            excelVO.setCompanyCode(getCompanyById(data.getCompanyId()).getOrgCode());
            excelVO.setParentDeptCode(data.getParentOrgCode());
            excelVO.setDepartmentOrder(String.valueOf(data.getDepartmentOrder()));
            excelVO.setRemark(data.getRemark());
            excelVO.setStatus(deptStatusEnum == null ? null : deptStatusEnum.getText());
            excelVOList.add(excelVO);
        });
        return excelVOList;
    }

    private List<Dept> transExcelVOToDepts(List<DeptVerifyExcelDTO> list) {
        List<Dept> deptList = new ArrayList<>();
        list.forEach(data -> {

            Dept dept = new Dept();
            dept.setOrgCode(data.getDeptCode());
            dept.setDepartmentName(data.getDepartmentName());
            dept.setParentOrgCode(data.getParentDeptCode());
            dept.setRemark(data.getRemark());
            if (StringUtils.isNotBlank(data.getCompanyCode())) {
                Company company = getCompanyByCode(data.getCompanyCode());
                if(company != null) {
                    dept.setCompanyId(company.getId());
                }
            }
            if (StringUtils.isNotBlank(data.getStatus())) {
                DeptStatusEnum deptStatusEnum = Arrays.stream(DeptStatusEnum.values())
                        .filter(x -> x.getText().equals(data.getStatus()))
                        .findFirst()
                        .orElse(null);
                dept.setStatus(deptStatusEnum == null ? null : deptStatusEnum.getValue());
            }
            if (StringUtils.isNotBlank(data.getDepartmentOrder()) && DeptImportListener.isLongNumber(data.getDepartmentOrder())) {
                dept.setDepartmentOrder(Integer.parseInt(data.getDepartmentOrder()));
            }
            deptList.add(dept);
        });
        return deptList;
    }

    private Company getCompanyByCode(String code) {
        return companyService.getOne(
                new LambdaQueryWrapper<Company>()
                        .eq(Company::getOrgCode, code));
    }

    private Company getCompanyById(Long companyId) {
        return companyService.getOne(
                new LambdaQueryWrapper<Company>()
                        .select(Company::getOrgCode, Company::getId)
                        .eq(Company::getId, companyId));
    }

}

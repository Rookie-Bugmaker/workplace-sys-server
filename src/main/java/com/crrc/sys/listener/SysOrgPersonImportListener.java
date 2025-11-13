package com.crrc.sys.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.enums.*;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.model.org.person.SysOrgPersonDeliveryExcelVO;
import com.crrc.sys.model.org.person.SysOrgPersonVerifyExcelDTO;
import com.crrc.sys.service.CompanyService;
import com.crrc.sys.service.DeptService;
import com.crrc.sys.service.SysDeptPersonService;
import com.crrc.sys.service.SysOrgPersonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
public class SysOrgPersonImportListener extends AnalysisEventListener<SysOrgPersonDeliveryExcelVO> {
    private final SysOrgPersonService personService;

    private final CompanyService companyService;

    private final DeptService deptService;

    private List<SysOrgPersonVerifyExcelDTO> verifyExcelList = new ArrayList<>();

    private Map<String, List<SysOrgPersonDeliveryExcelVO>> containJobNosToExcelDatasMap = new HashMap<>();

    public SysOrgPersonImportListener(SysOrgPersonService personService,
                                      CompanyService companyService,
                                      DeptService deptService) {
        this.personService = personService;
        this.companyService = companyService;
        this.deptService = deptService;
    }

    @Override
    public void invoke(SysOrgPersonDeliveryExcelVO excelData, AnalysisContext context) {
        List<SysOrgPersonDeliveryExcelVO> alreadyExistedExcelData = containJobNosToExcelDatasMap.get(excelData.getJobNo());
        int row = context.readRowHolder().getRowIndex() + 1;
        StringBuilder errorBuilder = new StringBuilder();
        if (excelData == null) {
            throw new BizException("【第" + row + "行: 数据不能为空】");
        }
        if (StringUtils.isBlank(excelData.getFullName())) {
            errorBuilder.append("【第" + row + "行: 姓名不能为空】");
        }
        if (StringUtils.isBlank(excelData.getPersonType())) {
            errorBuilder.append("【第" + row + "行: 人员类型（员工/供应商）不能为空】");
        }
        if (StringUtils.isBlank(excelData.getStatus())) {
            errorBuilder.append("【第" + row + "行: 人员状态（在职/离职）不能为空】");
        }
        if (StringUtils.isBlank(excelData.getGender())) {
            errorBuilder.append("【第" + row + "行: 性别（男/女）不能为空】");
        }

        // 验证当前数据，验证工号及其与公司/部门的关系
        if (StringUtils.isBlank(excelData.getJobNo())) {
            errorBuilder.append("【第" + row + "行: 工号不能为空】");
        } else {
            // 检查数据库重复
            SysOrgPerson personInDB = personService.findByJobNo(excelData.getJobNo());
            if (personInDB != null) {
                errorBuilder.append("【第" + row + "行: 工号为" + excelData.getJobNo() + "的人员系统中已存在】");
            }
            if (StringUtils.isBlank(excelData.getCompanyCode())) {
                if (StringUtils.isNotBlank(excelData.getDeptCode())) {
                    errorBuilder.append("【第" + row + "行: 已填写部门编码，公司编码不能为空】");
                }
            } else {
                Company company = getCompanyByCode(excelData.getCompanyCode());
                if (company != null) {
                    if (StringUtils.isNotBlank(excelData.getDeptCode())) {
                        Dept dept = getDeptByCompanyIdAndCode(company.getId(), excelData.getDeptCode());
                        if (dept == null) {
                            errorBuilder.append("【第" + row + "行: " + excelData.getCompanyCode() + "公司下不存在部门" + excelData.getDeptCode() + "】");
                        }
                    } else {
                        errorBuilder.append("【第" + row + "行: 已填写公司编码，部门编码不能为空】");
                    }
                } else {
                    errorBuilder.append("【第" + row + "行: " + excelData.getCompanyCode() + "公司在系统中不存在】");
                }
            }
        }

        // 验证与旧数据是否存在错误关系
        if (StringUtils.isNotBlank(excelData.getJobNo())) {
            if (alreadyExistedExcelData != null && alreadyExistedExcelData.size() > 0) {

                alreadyExistedExcelData.forEach(existedData -> {
                    // 只有新旧数据公司/部门都不为空，且不完全相同则正确
                    if (
                            !(
                                    (
                                            StringUtils.isNotBlank(existedData.getCompanyCode())
                                                    && StringUtils.isNotBlank(existedData.getDeptCode())
                                                    && StringUtils.isNotBlank(excelData.getCompanyCode())
                                                    && StringUtils.isNotBlank(excelData.getDeptCode())
                                    )
                                            &&
                                            (
                                                    !existedData.getCompanyCode().equals(excelData.getCompanyCode())
                                                            || !existedData.getDeptCode().equals(excelData.getDeptCode())
                                            )
                            )
                    ) {
                        errorBuilder.append("【第" + row + "行: 工号为" + excelData.getJobNo() + "的人员工号重复上传,去除重复行或根据公司/部门区分】");
                    }
                });
            } else {
                // 不存在旧数据，则加入当前数据
                containJobNosToExcelDatasMap.put(excelData.getJobNo(), Arrays.asList(excelData));
            }
        }

        // 验证上级人员工号
        if (StringUtils.isNotBlank(excelData.getParentPersonCode())) {
            SysOrgPerson person = personService.findByJobNo(excelData.getParentPersonCode());
            if (person == null) {
                errorBuilder.append("【第" + row + "行: " + excelData.getParentPersonCode() + "直属上级工号在系统中不存在】");
            }
        }
        // 验证人员类型
        PersonTypeEnum personTypeEnum = Arrays.stream(PersonTypeEnum.values())
                .filter(x -> x.getText().equals(excelData.getPersonType()))
                .findFirst()
                .orElse(null);
        if (personTypeEnum == null) {
            errorBuilder.append("【第" + row + "行: 人员类型未知】");
        }
        // 验证人员状态
        PersonStatusEnum statusEnum = Arrays.stream(PersonStatusEnum.values())
                .filter(x -> x.getText().equals(excelData.getStatus()))
                .findFirst()
                .orElse(null);
        if (statusEnum == null) {
            errorBuilder.append("【第" + row + "行: 人员状态未知】");
        }
        // 验证性别
        GenderEnum genderEnum = Arrays.stream(GenderEnum.values())
                .filter(x -> x.getText().equals(excelData.getGender()))
                .findFirst()
                .orElse(null);
        if (genderEnum == null) {
            errorBuilder.append("【第" + row + "行: 性别未知】");
        }
        SysOrgPersonVerifyExcelDTO verifyExcelDTO
                = BeanUtils.toBean(excelData, SysOrgPersonVerifyExcelDTO.class);
        verifyExcelDTO.setValidateStatus(StatusEnum.Y.name());
        if (errorBuilder.length() > 0) {
            verifyExcelDTO.setValidateStatus(StatusEnum.N.name());
            verifyExcelDTO.setValidateMsg(errorBuilder.toString());
            errorBuilder.setLength(0);
        }
        verifyExcelList.add(verifyExcelDTO);
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // nothing
    }

    public List<SysOrgPersonVerifyExcelDTO> getResult() {
        return verifyExcelList;
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
}
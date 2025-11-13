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
import com.crrc.sys.model.company.CompanyDeliveryExcelVO;
import com.crrc.sys.model.company.CompanyVerifyExcelDTO;
import com.crrc.sys.model.org.person.SysOrgPersonDeliveryExcelVO;
import com.crrc.sys.model.org.person.SysOrgPersonVerifyExcelDTO;
import com.crrc.sys.service.CompanyService;
import com.crrc.sys.service.DeptService;
import com.crrc.sys.service.SysOrgPersonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class CompanyImportListener extends AnalysisEventListener<CompanyDeliveryExcelVO> {

    private final CompanyService companyService;

    private List<CompanyVerifyExcelDTO> verifyExcelList = new ArrayList<>();

    public CompanyImportListener(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public void invoke(CompanyDeliveryExcelVO excelData, AnalysisContext context) {
        int row = context.readRowHolder().getRowIndex() + 1;
        StringBuilder errorBuilder = new StringBuilder();
        if (excelData == null) {
            throw new BizException("【第" + row + "行: 数据不能为空】");
        }
        if (StringUtils.isBlank(excelData.getCompanyId())) {
            errorBuilder.append("【第" + row + "行: 公司ID不能为空】");
        } else {
            if (!isLongNumber(excelData.getCompanyId())) {
                errorBuilder.append("【第" + row + "行: 公司ID必须为数字格式】");
            }else {
                LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<Company>().eq(Company::getCompanyId, excelData.getCompanyId());
                if(companyService.count(wrapper) > 0) {
                    errorBuilder.append("【第" + row + "行: 公司ID为"+excelData.getCompanyId()+"的数据已存在】");
                }
            }
        }
        if (StringUtils.isBlank(excelData.getOrgCode())) {
            errorBuilder.append("【第" + row + "行: 组织编码不能为空】");
        }else {
            LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<Company>().eq(Company::getOrgCode, excelData.getOrgCode());
            if(companyService.count(wrapper) > 0) {
                errorBuilder.append("【第" + row + "行: 组织编码为"+excelData.getOrgCode()+"的数据已存在】");
            }
        }
        if (StringUtils.isBlank(excelData.getCompanyName())) {
            errorBuilder.append("【第" + row + "行: 公司名称不能为空】");
        }else {
            LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<Company>().eq(Company::getCompanyName, excelData.getCompanyName());
            if(companyService.count(wrapper) > 0) {
                errorBuilder.append("【第" + row + "行: 公司名称为"+excelData.getCompanyName()+"的数据已存在】");
            }
        }
        if (StringUtils.isBlank(excelData.getStatus())) {
            errorBuilder.append("【第" + row + "行: 公司状态(正常/停用)不能为空】");
        } else {
            CompanyStatusEnum companyStatusEnum = Arrays.stream(CompanyStatusEnum.values())
                    .filter(x -> x.getText().equals(excelData.getStatus()))
                    .findFirst()
                    .orElse(null);
            if (companyStatusEnum == null) {
                errorBuilder.append("【第" + row + "行: 公司状态未知】");
            }
        }
        if (StringUtils.isNotBlank(excelData.getPlantCode())) {
            LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<Company>().eq(Company::getPlantCode, excelData.getPlantCode());
            if(companyService.count(wrapper) > 0) {
                errorBuilder.append("【第" + row + "行: 工厂编码为"+excelData.getPlantCode()+"的数据已存在】");
            }
        }

        CompanyVerifyExcelDTO verifyExcelDTO
                = BeanUtils.toBean(excelData, CompanyVerifyExcelDTO.class);
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

    public List<CompanyVerifyExcelDTO> getResult() {
        return verifyExcelList;
    }

    /**
     * 判断字符串是否为合法的Long类型数字
     *
     * @param str 待检测的字符串
     * @return true-符合Long格式，false-不符合
     */
    public static boolean isLongNumber(String str) {
        // 空字符串直接返回false
        if (str == null || str.trim().isEmpty()) {
            return false;
        }

        try {
            // 尝试转换为Long类型
            Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            // 捕获数字格式异常
            return false;
        }
    }
}
package com.crrc.sys.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.enums.DeptStatusEnum;
import com.crrc.sys.enums.StatusEnum;
import com.crrc.sys.model.dept.DeptDeliveryExcelVO;
import com.crrc.sys.model.dept.DeptVerifyExcelDTO;
import com.crrc.sys.service.CompanyService;
import com.crrc.sys.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class DeptImportListener extends AnalysisEventListener<DeptDeliveryExcelVO> {

    private final DeptService deptService;

    private final CompanyService companyService;

    private List<DeptVerifyExcelDTO> verifyExcelList = new ArrayList<>();

    public DeptImportListener(DeptService deptService, CompanyService companyService) {
        this.deptService = deptService;
        this.companyService = companyService;
    }

    @Override
    public void invoke(DeptDeliveryExcelVO excelData, AnalysisContext context) {
        int row = context.readRowHolder().getRowIndex() + 1;
        StringBuilder errorBuilder = new StringBuilder();
        if (excelData == null) {
            throw new BizException("【第" + row + "行: 数据不能为空】");
        }
        if (StringUtils.isBlank(excelData.getDeptCode())) {
            errorBuilder.append("【第" + row + "行: 部门编码不能为空】");
        }else {
            LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<Dept>().eq(Dept::getOrgCode, excelData.getDeptCode());
            if(deptService.count(wrapper) > 0) {
                errorBuilder.append("【第" + row + "行: 部门编码为"+excelData.getDeptCode()+"的数据已存在】");
            }
        }
        if (StringUtils.isBlank(excelData.getDepartmentName())) {
            errorBuilder.append("【第" + row + "行: 部门名称不能为空】");
        }
        if (StringUtils.isBlank(excelData.getCompanyCode())) {
            errorBuilder.append("【第" + row + "行: 所属公司编码不能为空】");
        } else {
            Company company = getCompanyByCode(excelData.getCompanyCode());
            if(company == null) {
                errorBuilder.append("【第" + row + "行: 所属公司编码为"+excelData.getCompanyCode()+"的数据不存在】");
            } else {
                // 验证父级部门
                if (StringUtils.isNotBlank(excelData.getParentDeptCode())) {
                    LambdaQueryWrapper<Dept> wrapper =
                            new LambdaQueryWrapper<Dept>()
                                    .eq(Dept::getOrgCode, excelData.getParentDeptCode())
                                    .eq(Dept::getCompanyId, company.getId());
                    if(deptService.count(wrapper) <= 0) {
                        errorBuilder.append("【第" + row + "行: 公司"+excelData.getCompanyCode()+"下不存在父级部门"+excelData.getParentDeptCode()+"】");
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(excelData.getDepartmentOrder())) {
            if (!isLongNumber(excelData.getDepartmentOrder())) {
                errorBuilder.append("【第" + row + "行: 部门排序必须为数字格式】");
            }
        }
        if (StringUtils.isBlank(excelData.getStatus())) {
            errorBuilder.append("【第" + row + "行: 部门状态(正常/停用)不能为空】");
        } else {
            DeptStatusEnum deptStatusEnum = Arrays.stream(DeptStatusEnum.values())
                    .filter(x -> x.getText().equals(excelData.getStatus()))
                    .findFirst()
                    .orElse(null);
            if (deptStatusEnum == null) {
                errorBuilder.append("【第" + row + "行: 部门状态未知】");
            }
        }
        DeptVerifyExcelDTO verifyExcelDTO
                = BeanUtils.toBean(excelData, DeptVerifyExcelDTO.class);
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

    public List<DeptVerifyExcelDTO> getResult() {
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

    private Company getCompanyByCode(String code) {
        return companyService.getOne(
                new LambdaQueryWrapper<Company>()
                        .eq(Company::getOrgCode, code));
    }
}
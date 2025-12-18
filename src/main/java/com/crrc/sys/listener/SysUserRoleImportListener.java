package com.crrc.sys.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.*;
import com.crrc.sys.model.org.person.SysOrgPersonDeliveryExcelVO;
import com.crrc.sys.model.org.person.SysOrgPersonVerifyExcelDTO;
import com.crrc.sys.model.user.role.SysUserRoleDeliveryExcelVO;
import com.crrc.sys.model.user.role.SysUserRoleVerifyExcelDTO;
import com.crrc.sys.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SysUserRoleImportListener extends AnalysisEventListener<SysUserRoleDeliveryExcelVO> {
    private final SysUserService userService;

    private final SysRoleService roleService;

    private List<SysUserRoleVerifyExcelDTO> verifyExcelList = new ArrayList<>();

    private Map<String, List<String>> containRoleCodesToUserNamesMap = new HashMap<>();

    public SysUserRoleImportListener(SysUserService userService,
                                     SysRoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void invoke(SysUserRoleDeliveryExcelVO excelData, AnalysisContext context) {
        if(!containRoleCodesToUserNamesMap.containsKey(excelData.getRoleCode())) {
            containRoleCodesToUserNamesMap.put(excelData.getRoleCode(), new ArrayList<>());
        }
        List<String> alreadyExistedUserNameData = containRoleCodesToUserNamesMap.get(excelData.getRoleCode());
        int row = context.readRowHolder().getRowIndex() + 1;
        StringBuilder errorBuilder = new StringBuilder();
        SysRole sysRole = null;
        if (excelData == null) {
            throw new BizException("【第" + row + "行: 数据不能为空】");
        }
        if (StringUtils.isBlank(excelData.getRoleCode())) {
            errorBuilder.append("【第" + row + "行: 角色编码不能为空】");
        }
        if (StringUtils.isNotBlank(excelData.getRoleCode())) {
            sysRole = roleService.findByCode(excelData.getRoleCode(), LanguageEnum.ZH_CN.getValue());
            if(sysRole == null) {
                errorBuilder.append("【第" + row + "行: "+excelData.getRoleCode()+"编码对应角色不存在】");
            }
        }
        if (StringUtils.isBlank(excelData.getUserName())) {
            errorBuilder.append("【第" + row + "行: 用户名不能为空】");
        }
        if (StringUtils.isNotBlank(excelData.getUserName())) {
            SysUser sysUser = userService.findByUserName(excelData.getUserName());
            if(sysUser == null) {
                errorBuilder.append("【第" + row + "行: 用户名"+excelData.getUserName()+"对应用户不存在】");
            }
        }
        if(StringUtils.isNotBlank(excelData.getRoleCode()) && StringUtils.isNotBlank(excelData.getUserName())) {
            if(alreadyExistedUserNameData.contains(excelData.getUserName())) {
                errorBuilder.append("【第" + row + "行: 用户名"+excelData.getUserName()+"重复上传】");
            } else {
                containRoleCodesToUserNamesMap.get(excelData.getRoleCode()).add(excelData.getUserName());
            }
            if(sysRole != null) {
                List<SysUser> userByRoleId = userService.findUserByRoleId(sysRole.getRoleId());
                if(userByRoleId != null && userByRoleId.size() > 0) {
                    Set<String> useNameInDB = userByRoleId.stream()
                            .map(SysUser::getUserName)
                            .collect(Collectors.toSet());
                    if(useNameInDB.contains(excelData.getUserName())) {
                        errorBuilder.append("【第" + row + "行: 用户"+excelData.getUserName()+"已经绑定角色"+excelData.getRoleCode()+"】");
                    }
                }
            }
        }

        SysUserRoleVerifyExcelDTO verifyExcelDTO
                = BeanUtils.toBean(excelData, SysUserRoleVerifyExcelDTO.class);
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

    public List<SysUserRoleVerifyExcelDTO> getResult() {
        return verifyExcelList;
    }

}
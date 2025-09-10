package com.crrc.sys.entity.role;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 谭永强
 * @date 2025-09-10
 */
@TableName("sys_role_company_dept")
@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleCompanyDept extends BaseEntity {

    /**
     * 主键
     */
    private Long id;
    /**
     * 角色ID
     */
    private Long roleId;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司ID
     */
    private Long deptId;
}

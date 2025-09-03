package com.crrc.sys.model.dept.person;

import com.baomidou.mybatisplus.annotation.*;
import com.crrc.sys.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门人员实体
 *
 * @author 谭永强
 * @date 2025-09-03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dept_person")
public class SysDeptPersonPO extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long id;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 人员ID
     */
    private Long personId;
    /**
     * 职务
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String position;
    /**
     * 上级人员ID
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long parentPersonId;
}

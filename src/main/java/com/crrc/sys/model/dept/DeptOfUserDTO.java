package com.crrc.sys.model.dept;

import lombok.Data;

@Data
public class DeptOfUserDTO {

    private Long id;

    private Long userId;

    private String parentOrgCode;

    private String departmentName;

    private Integer departmentOrder;

    private String status;

    private String remark;

    private Boolean deleted;

}

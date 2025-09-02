package com.crrc.sys.model.dept;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeptDTO {

    private Long id;


    private String orgCode;


    private String parentOrgCode;


    private String departmentName;


    private Integer departmentOrder;


    private String status;


    private Long companyId;


    private String remark;

    /**
     * 是否删除
     */
    private Integer deleted;

    List<DeptDTO> children = new ArrayList<>();
}

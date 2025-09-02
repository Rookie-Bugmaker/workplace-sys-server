package com.crrc.sys.model.dept;

import lombok.Data;

import java.util.Set;

@Data
public class DeptqueryVo {
    private Set<Long> companyIds;

    private Set<Long> deptIds;

    private String keywords;
}

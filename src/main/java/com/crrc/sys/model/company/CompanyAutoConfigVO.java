package com.crrc.sys.model.company;

import lombok.Data;


@Data
public class CompanyAutoConfigVO {

    private Long id;

    private String companyName;

    private Long companyId;

    private String status;

    private String remark;

    private Integer deleted;
}

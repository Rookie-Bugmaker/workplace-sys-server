package com.crrc.sys.entity.admindivision;

import lombok.Data;

import java.util.Date;

@Data
public class AdminDivisionListVo {

    private Long id;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;
    private Integer deleted;

    private String countryName;
    private String countryNumber;

    private String divisionNumber;
    private String divisionName;
    private String parentNumber;
    private String parentName;
    private String divisionLevel;

    private String fullSpell;
    private String simpleSpell;
    private String description;
    private String cityNumber;
    private String timeZone;
    private String cityFlag;
    private String areaCode;
    private String enableFlag;
}
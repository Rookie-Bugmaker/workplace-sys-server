package com.crrc.sys.entity.admindivision;

import lombok.Data;

@Data
public class AdminDivisionQueryVo {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /**
     * 左侧点击国家时传
     */
    private String treeCountryNumber;

    /**
     * 左侧点击行政区域时传
     */
    private String treeDivisionNumber;

    /**
     * 右侧查询条件
     */
    private String countryNumber;
    private String countryName;
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
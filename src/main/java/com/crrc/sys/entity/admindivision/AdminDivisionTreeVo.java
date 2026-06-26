package com.crrc.sys.entity.admindivision;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminDivisionTreeVo {

    private Long id;
    /**
     * country / division
     */
    private String nodeType;

    /**
     * 节点编码
     */
    private String number;

    /**
     * 节点名称
     */
    private String name;

    private String countryNumber;

    private String parentNumber;

    private String divisionLevel;

    private List<AdminDivisionTreeVo> children = new ArrayList<>();
}
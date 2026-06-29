package com.crrc.sys.entity.measureunit;

import lombok.Data;

import java.util.Date;

@Data
public class MeasureUnitListVo {

    private Long id;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;

    private String unitNumber;

    private String unitName;

    private String unitDescCn;

    private String unitDescEn;

    private String mdmUseFlag;

    private String unitType;

    private String enableFlag;
}
package com.crrc.sys.entity.measureunit;

import lombok.Data;

@Data
public class MeasureUnitQueryVo {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String unitNumber;

    private String unitName;

    private String unitDescCn;

    private String unitDescEn;

    private String mdmUseFlag;

    private String unitType;

    private String enableFlag;
}
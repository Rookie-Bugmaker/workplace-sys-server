package com.crrc.sys.entity.currency;

import lombok.Data;

import java.util.Date;

@Data
public class CurrencyListVo {

    private Long id;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;

    private String currencyNumber;
    private String currencyName;
    private String amountPrecision;
    private String pricePrecision;
    private String description;
    private String currencySign;
    private String enableFlag;
}
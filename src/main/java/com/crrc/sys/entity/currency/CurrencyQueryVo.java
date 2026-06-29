package com.crrc.sys.entity.currency;

import lombok.Data;

@Data
public class CurrencyQueryVo {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String currencyNumber;

    private String currencyName;

    private String amountPrecision;

    private String pricePrecision;

    private String description;

    private String currencySign;

    private String enableFlag;
}
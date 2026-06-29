package com.crrc.sys.entity.currency;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_md_base_currency")
public class SysMdBaseCurrency {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;
    private Integer deleted;

    private String currencyNumber;
    private String currencyName;
    private String amountPrecision;
    private String pricePrecision;
    private String description;
    private String currencySign;
    private String enableFlag;
}
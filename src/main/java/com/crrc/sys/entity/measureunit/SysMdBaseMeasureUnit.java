package com.crrc.sys.entity.measureunit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_md_base_measure_unit")
public class SysMdBaseMeasureUnit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;
    private Integer deleted;

    private String unitNumber;
    private String unitName;
    private String unitDescCn;
    private String unitDescEn;
    private String mdmUseFlag;
    private String unitType;
    private String enableFlag;
}
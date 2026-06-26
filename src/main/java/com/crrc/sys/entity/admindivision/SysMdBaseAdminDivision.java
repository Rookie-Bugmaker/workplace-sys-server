package com.crrc.sys.entity.admindivision;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_md_base_admin_division")
public class SysMdBaseAdminDivision {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;
    private Integer deleted;

    private String divisionNumber;
    private String divisionName;
    private String parentNumber;
    private String divisionLevel;
    private String countryNumber;
    private String fullSpell;
    private String simpleSpell;
    private String description;
    private String cityNumber;
    private String timeZone;
    private String cityFlag;
    private String areaCode;
    private String enableFlag;
}
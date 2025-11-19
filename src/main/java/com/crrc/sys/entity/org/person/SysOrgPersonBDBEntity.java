package com.crrc.sys.entity.org.person;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 组织架构-人员-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_org_person_b")
public class SysOrgPersonBDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long personId;
    
    private String gender;
    private String jobNo;
    private Long personType;
    private String phone;
    private String email;
    private Integer status;
    private Date inductionDate;
    private Date quitDate;
}
package com.crrc.sys.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;
import java.util.Date;

/**
 * 系统用户表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_user")
public class SysUserDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long userId;
    
    private String userName;
    private String userDescription;
    private String password;
    private Date passwordUpdateDate;
    private String userType;
    private Long userRelationId;
    private Integer loginErrorCount;
    private Long lastLoginInfoId;
    private Date disableDeadlineDate;
    private String disableState;
    private Date passwordExpireDate;
    private String nextLoginUpdatePassword;
}
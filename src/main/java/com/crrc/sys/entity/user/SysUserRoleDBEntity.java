package com.crrc.sys.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户角色关联表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_user_role")
public class SysUserRoleDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long id;
    
    private Long userId;
    private Long roleId;
}
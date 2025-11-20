package com.crrc.sys.entity.role;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色列权限值集表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_role_column_value_set")
public class SysRoleColumnValueSetDBEntity extends BaseEntityYunLing {
    private Long roleId;
    private Long fcId;
    private Long vsId;
    private Long blockId;
}
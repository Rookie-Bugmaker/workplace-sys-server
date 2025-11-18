package com.crrc.sys.entity.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色列权限关系表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_role_function_column")
public class SysRoleFunctionColumnDBEntity extends BaseEntityYunLing {
    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 列权限ID
     */
    private Long fcId;

    /**
     * 是否可编辑
     */
    private String fcEditableFlag;
}
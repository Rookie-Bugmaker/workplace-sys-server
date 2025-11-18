package com.crrc.sys.entity.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 角色菜单关系表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_role_menu")
public class SysRoleMenuDBEntity extends BaseEntityYunLing {
    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 半选中状态(1:是/0:否)
     */
    private Boolean halfChecked;

    /**
     * 是否是子菜单/功能ID(1:是/0:否)
     */
    private Boolean childFlag;
}
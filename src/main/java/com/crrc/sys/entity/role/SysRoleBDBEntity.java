package com.crrc.sys.entity.role;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 系统角色表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_role_b")
public class SysRoleBDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long roleId;
    
    private String roleCode;
    private Long menuId;
    private String enableFlag;
    private Long projectId;
    private String defaultFlag;
}
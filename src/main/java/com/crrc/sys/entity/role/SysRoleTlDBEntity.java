package com.crrc.sys.entity.role;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 系统角色多语言表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_role_tl")
public class SysRoleTlDBEntity extends BaseEntityYunLing {
    private Long roleId;
    private String roleName;
    private String roleDescription;
    private String language;
}
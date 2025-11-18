package com.crrc.sys.entity.menu;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 菜单多语言-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_menu_tl")
public class SysMenuTlDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long menuId;
    
    private String language;
    private String menuName;
    private String menuDescription;
}
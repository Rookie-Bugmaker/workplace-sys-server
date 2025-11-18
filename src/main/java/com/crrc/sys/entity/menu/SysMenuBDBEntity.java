package com.crrc.sys.entity.menu;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 菜单-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_menu_b")
public class SysMenuBDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long menuId;
    
    private String menuCode;
    private Integer menuSort;
    private Long functionId;
    private String menuIcon;
    private Long parentId;
    private Long childId;
    private Long projectId;
}
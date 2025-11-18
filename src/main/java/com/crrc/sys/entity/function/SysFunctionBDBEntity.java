package com.crrc.sys.entity.function;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 系统功能基表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_function_b")
public class SysFunctionBDBEntity extends BaseEntityYunLing {
    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private Long functionId;
    
    /**
     * 父功能ID
     */
    private Long parentId;
    
    /**
     * 功能代码
     */
    private String functionCode;
    
    /**
     * 功能类型(A:功能、B:按钮)
     */
    private String functionType;
    
    /**
     * 路径
     */
    private String path;
    
    /**
     * 参数
     */
    private String parameters;
    
    /**
     * 组件
     */
    private String component;
    
    /**
     * 路由名称
     */
    private String name;
    
    /**
     * 重定向地址
     */
    private String redirect;
    
    /**
     * 是否在菜单中隐藏子组件 1是0否
     */
    private Boolean hideChildrenInMenu;
    
    /**
     * 是否在菜单中隐藏该功能 1是0否
     */
    private Boolean hideInMenu;
    
    /**
     * 是否在tab选项卡中锁定 1是0否
     */
    private Boolean lockInTab;
    
    /**
     * 是否在合并同名tab选项卡 1是0否
     */
    private Boolean mergeTab;
    
    /**
     * 打开方式 0 内部 1 外部
     */
    private Integer openMethod;
    
    /**
     * 是否缓存 1是0否
     */
    private Boolean keepAlive;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 值集ID
     */
    private Long vsId;
    
    /**
     * 页面ID
     */
    private Long pageId;
    
    /**
     * 项目ID
     */
    private Long projectId;
}
package com.crrc.sys.entity.function.column;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 列权限表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_function_column_b")
public class SysFunctionColumnBDBEntity extends BaseEntityYunLing {
    /**
     * 主键
     */
    @TableId(type = IdType.INPUT)
    private Long fcId;
    
    /**
     * 功能ID
     */
    private Long functionId;
    
    /**
     * (块/列)编码
     */
    private String fcCode;
    
    /**
     * 默认显示(Y:是/N:否)
     */
    private String fcDisplayFlag;
    
    /**
     * 是否可编辑
     */
    private String fcEditableFlag;
    
    /**
     * 上级ID
     */
    private Long parentId;
    
    /**
     * 值集ID
     */
    private Long vsId;
    
    /**
     * 列类型(varchar:字符、number:数字、date:日期)
     */
    private String fcType;
}
package com.crrc.sys.entity.function.column;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 列权限多语言表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_function_column_tl")
public class SysFunctionColumnTlDBEntity extends BaseEntityYunLing {
    /**
     * 列权限ID
     */
    private Long fcId;
    
    /**
     * 语言(zh-CN简体中文/en-US美式英语)
     */
    private String language;
    
    /**
     * 名称
     */
    private String fcName;
    
    /**
     * 描述
     */
    private String fcDesc;
}
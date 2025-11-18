package com.crrc.sys.entity.function;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 系统功能多语言表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_function_tl")
public class SysFunctionTlDBEntity extends BaseEntityYunLing {
    /**
     * 主键ID
     */
    private Long functionId;
    
    /**
     * 功能名称
     */
    private String functionName;
    
    /**
     * 描述
     */
    private String functionDescription;
    
    /**
     * 语言zh-CN简体中文 en-US美式英语
     */
    private String language;
}
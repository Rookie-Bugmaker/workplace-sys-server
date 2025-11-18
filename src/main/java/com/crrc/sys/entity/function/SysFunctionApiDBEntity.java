package com.crrc.sys.entity.function;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 功能API关系表-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-17
 */
@Data
@Accessors(chain = false)
@TableName("sys_function_api")
public class SysFunctionApiDBEntity extends BaseEntityYunLing {
    /**
     * 功能ID
     */
    private Long functionId;
    
    /**
     * 接口ID
     */
    private Long apiId;
}
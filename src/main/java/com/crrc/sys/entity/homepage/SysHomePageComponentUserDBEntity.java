package com.crrc.sys.entity.homepage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 主页组件用户分配-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_home_page_component_user")
public class SysHomePageComponentUserDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long componentUserId;
    
    private Long componentId;
    private Long userId;
}
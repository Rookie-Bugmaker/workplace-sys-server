package com.crrc.sys.entity.homepage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户主页自定义配置-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_home_page_user_config")
public class SysHomePageUserConfigDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long configId;
    
    private String userConfig;
    private Long userId;
}
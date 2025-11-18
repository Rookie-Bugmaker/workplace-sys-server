package com.crrc.sys.entity.homepage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 主页组件定义-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_home_page_component")
public class SysHomePageComponentDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long componentId;
    
    private String homePageTitle;
    private String functionName;
    private String functionCode;
    private Integer refreshRate;
    private Integer loadingType;
    private String userType;
    private String remarks;
    private String status;
    private String showStatus;
    private String functionUrl;
}
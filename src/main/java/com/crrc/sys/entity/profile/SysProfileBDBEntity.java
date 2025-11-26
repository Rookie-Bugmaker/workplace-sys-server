package com.crrc.sys.entity.profile;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 配置文件-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_profile_b")
public class SysProfileBDBEntity extends BaseEntityYunLing {
    @TableId(type = IdType.INPUT)
    private Long profileId;

    private String profileCode;
    private String profileSourceType;
    private String profileSource;
    private String profileValueColumn;
    private String profileCodeColumn;
    private String profileDescColumn;
    private String profileParentColumn;
    private String profileComponentType;
    private String profileType;
    private String profileFieldAlias;
}
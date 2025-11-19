package com.crrc.sys.entity.profile;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 配置文件国际化-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_profile_tl")
public class SysProfileTlDBEntity extends BaseEntityYunLing {
    private Long profileId;
    private String language;
    private String profileName;
}
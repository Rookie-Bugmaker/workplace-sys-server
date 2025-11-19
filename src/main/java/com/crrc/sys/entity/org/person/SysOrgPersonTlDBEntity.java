package com.crrc.sys.entity.org.person;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntityYunLing;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 组织架构-人员国际化-数据库实体
 *
 * @author 李江涛
 * @date 2025-11-18
 */
@Data
@Accessors(chain = false)
@TableName("sys_org_person_tl")
public class SysOrgPersonTlDBEntity extends BaseEntityYunLing {
    private Long personId;
    private String language;
    private String fullName;
}
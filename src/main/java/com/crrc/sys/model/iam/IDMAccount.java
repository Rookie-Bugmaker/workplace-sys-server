package com.crrc.sys.model.iam;

import lombok.Data;

import java.util.List;

/**
 * 第三方登录IDM账户实体类
 */
@Data
public class IDMAccount {
    private String app_account__account_id; // 账号id 唯一值
    private String app_account__account_no; // 账号
    private String app_account__account_name; // 账号id 唯一值
    private Integer app_account__status; // 账号状态 3新增&启用 0禁用
    private String idt_user__work_no; // 工号 主账号存在工号
    private Integer request_log__action_flag; // 下推状态
    private String resource_system; // 应用编码
    private List<IDMOrg> orgs; // 组织信息
    private List<IDMJob> jobs; // 岗位信息
}

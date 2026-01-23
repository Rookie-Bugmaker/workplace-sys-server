package com.crrc.sys.model.iam;

import lombok.Data;

@Data
public class IDMOrg {
    private String idt_org__id; // 账号如果绑定组织 必须传
    private String idt_org__name; // 组织名称
}
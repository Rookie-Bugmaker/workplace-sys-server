package com.crrc.sys.model.profile;

import lombok.Data;

/**
 * 权限值项（配置文件维度绑定接口用）
 * 携带权限值与其描述，用于 findBindObjects 返回与 saveBindObjects 入参。
 * 去重与差集对比以 {@link #authValue} 为唯一键，{@link #authValueDesc} 作为附属描述随值写入/更新。
 *
 * @author 李江涛
 * @date 2026-09-02
 */
@Data
public class AuthValueItem {

    /**
     * 权限值(唯一键)
     */
    private String authValue;
    /**
     * 权限值描述
     */
    private String authValueDesc;
}

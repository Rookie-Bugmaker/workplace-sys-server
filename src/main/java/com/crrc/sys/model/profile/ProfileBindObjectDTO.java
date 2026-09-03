package com.crrc.sys.model.profile;

import lombok.Data;

import java.util.List;

/**
 * 配置文件绑定角色/用户-入参（配置文件维度）
 * 每次修改由前端传递完整的对象绑定集合(每项=对象ID+该对象拥有的权限值)，后端做差集比对实现新增与删除，
 * 保留对象上做权限值的增量增删(相同的值行保留不删不插)。
 *
 * @author 李江涛
 * @date 2026-08-31
 */
@Data
public class ProfileBindObjectDTO {

    /**
     * 配置文件ID
     */
    private Long profileId;
    /**
     * 对象类型(user:用户、role:角色)
     */
    private String authType;
    /**
     * 完整的对象绑定集合(全量替换语义)：每项=一个对象及其拥有的权限值
     */
    private List<BindItem> bindList;

    /**
     * 对象绑定项
     * <p>后端按 {@code objectId} 聚合：同一 {@code objectId} 在 bindList 中重复出现多次时会被合并为同一对象处理；
     * 其下 {@link #authValues} 以 {@code authValue} 为唯一键去重(相同的 authValue 仅保留一条)，
     * {@code authValueDesc} 优先保留首个非空白描述。</p>
     */
    @Data
    public static class BindItem {

        /**
         * 对象ID(用户ID或角色ID)
         */
        private Long objectId;
        /**
         * 该对象拥有的权限值集合(前端按对象维度勾选的权限值，含权限值描述；
         * 空字符串/空白的 authValue 会被过滤，以 authValue 为唯一键去重，
         * 同一 objectId 在多 BindItem 重复出现时会合并)
         */
        private List<AuthValueItem> authValues;
    }
}

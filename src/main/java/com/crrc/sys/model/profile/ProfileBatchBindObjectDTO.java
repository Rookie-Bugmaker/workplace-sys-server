package com.crrc.sys.model.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 批量绑定/解绑配置文件对象-入参（配置文件维度）
 * 入参与 {@link ProfileBindObjectDTO} 基本相同，新增 {@link #bindMode} 区分绑定/解绑模式：
 * <ul>
 *   <li>绑定模式(bindMode=true)：仅新增对象缺失的权限值，对象已拥有的权限值不处理(不更新描述)；
 *       新对象(库中不存在)且带权限值时插入主表+权限值；bindList 为空或对象 authValues 为空时不处理。</li>
 *   <li>解绑模式(bindMode=false)：仅删除对象已有的权限值，没有的权限值不处理；
 *       若删除后对象该配置文件下的权限值全部被清空，则同步删除主表记录(sys_data_auth)及其剩余权限值，
 *       避免出现有数据权限但权限值为空的脏数据；库中不存在的对象不做任何处理。</li>
 * </ul>
 * <p>bindList 仍按 {@link ProfileBindObjectDTO.BindItem} 维度传递，同一 objectId 重复出现会合并、
 * authValue 以唯一键去重(描述优先保留首个非空白)，与 {@link ProfileBindObjectDTO} 语义一致。</p>
 *
 * @author 李江涛
 * @date 2026-09-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "批量绑定/解绑配置文件对象入参(配置文件维度)")
public class ProfileBatchBindObjectDTO extends ProfileBindObjectDTO {

    /**
     * 操作模式：true=绑定模式(仅新增对象缺失的权限值)，false=解绑模式(仅删除对象已有的权限值)；为空时抛参数异常
     */
    @Schema(description = "操作模式：true=绑定模式(仅新增缺失权限值)，false=解绑模式(仅删除已有权限值)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean bindMode;
}
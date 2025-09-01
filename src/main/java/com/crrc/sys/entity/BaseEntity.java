package com.crrc.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.crrc.sys.handler.SerializableHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Schema(description = "基础实体类")
@JsonSerialize(using = SerializableHandler.EntitySerializer.class)
public abstract class BaseEntity implements Serializable {
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建人", example = "1")
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2024-01-01 00:00:00")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新人", example = "1")
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2024-01-01 00:00:00")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    @Schema(description = "创建人名称", example = "admin")
    private String createPerson;

    @TableField(exist = false)
    @Schema(description = "更新人名称", example = "admin")
    private String updatePerson;

    // 动态字段存储
    @TableField(exist = false)
    private Map<String, Object> dynamicFields = new HashMap<>();
}

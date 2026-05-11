package com.crrc.sys.entity.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crrc.sys.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@TableName(value="sys_project")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "项目")
@Accessors(chain = false)
public class ProjectEntity extends BaseEntity {

    private static final long serialVersionUID = -7859243665118274174L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目名
     */
    private String name;

    /**
     * 项目中文名称
     */
    private String nameCn;

    /**
     * 前端访问url
     */
    private String url;

    /**
     * 项目描述
     */
    private String description;


    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 图形符号
     */
    private String graphicSymbol;

    /**
     * 归属分组
     */
    private String ownershipGroup;

    @TableLogic(value = "0",delval = "id")
    @Schema(description = "删除" ,example = "false")
    private Integer deleted;

    /**
     * 微前端(Y/N)
     */
    private String microFlag;
}

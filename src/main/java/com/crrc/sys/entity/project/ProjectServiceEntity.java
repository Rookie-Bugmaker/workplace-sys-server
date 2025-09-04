package com.crrc.sys.entity.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_project_service")
public class ProjectServiceEntity {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "项目服务id" ,example = "1")
    private Long id;
    @Schema(description = "项目id" ,example = "1")
    private Long projectId;

    @Schema(description = "服务id" ,example = "1")
    private Long serviceId;
}

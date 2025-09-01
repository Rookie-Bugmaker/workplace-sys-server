package com.crrc.sys.model.company;

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
@TableName("sys_user_company")
@Schema(description = "用户公司关联表")
public class UserCompany {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id" ,example = "1")
    private Long id;
    @Schema(description = "用户id" ,example = "1")
    private Long userId;
    @Schema(description = "公司id" ,example = "1")
    private Long companyId;
    @Schema(description = "偏好设置" ,example = "1")
    private String prefer;

}

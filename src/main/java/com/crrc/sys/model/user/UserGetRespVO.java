package com.crrc.sys.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据传输对象(兼容旧平台)
 *
 * @author 谭永强
 * @date 2025-09-05
 */
@Data
@Schema(description = "用户 - 查询响应VO")
public class UserGetRespVO {
    @Schema(
            description = "用户id",
            example = "1"
    )
    private Long id;
    @Schema(
            description = "用户名称",
            example = "超级管理员"
    )
    private String userName;
    @Schema(
            description = "用户昵称",
            example = "admin"
    )
    private String nickName;
    @Schema(
            description = "用户密码",
            example = "xxx"
    )
    private String password;
    @Schema(
            description = "账号状态",
            example = "1"
    )
    private String status;
    @Schema(
            description = "邮箱",
            example = "example@cccar.com"
    )
    private @Email String email;
    @Schema(
            description = "电话号",
            example = "+86-188xxxx8888"
    )
    private String phonenumber;
    @Schema(
            description = "用户性别",
            example = "0"
    )
    private String sex;
    @Schema(
            description = "头像",
            example = "image"
    )
    private String avatar;
    @Schema(
            description = "用户类型",
            example = "0"
    )
    private String userType;

    @Schema(
            description = "部门领导",
            example = "1"
    )
    private Boolean leader;

    /**
     * 密码修改时间
     */
    private LocalDateTime passwordUpdateTime;

    private List<Role> roles;

    private List<Dept> depts;

    private List<Company> companys;

    @Data
    @Schema(description = "角色")
    public static class Role {

        @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private String id;


        @Schema(description = "角色名", requiredMode = Schema.RequiredMode.REQUIRED, example = "角色名称")
        private String name;

        @Schema(description = "角色key" ,requiredMode = Schema.RequiredMode.REQUIRED,example = "admin")
        private String roleKey;

    }

    @Data
    @Schema(description = "部门")
    public static class Dept {

        @Schema(description = "部门id", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Long id;

        private Long companyId;

        private String orgCode;

        @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "部门名称")
        private String departmentName;


    }

    @Data
    @Schema(description = "工厂")
    public static class Company {
        private Long id;

        @Schema(description = "部门id", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private String companyId;

        private String orgCode;

        @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "部门名称")
        private String companyName;



    }

}

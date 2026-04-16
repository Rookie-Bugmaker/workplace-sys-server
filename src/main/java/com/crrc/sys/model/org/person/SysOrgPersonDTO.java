package com.crrc.sys.model.org.person;

import com.crrc.sys.model.dept.DeptInfoDTO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 人员信息
 *
 * @author 谭永强
 * @date 2025-09-08
 */
@Data
public class SysOrgPersonDTO {

    /**
     * 人员ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long personId;
    /**
     * 姓名
     */
    private String fullName;
    /**
     * 性别(M/F:男/女)
     */
    private String gender;
    /**
     * 工号
     */
    private String jobNo;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 人员状态(1:在职、2:离职)
     */
    private Integer status;
    /**
     * 入职日期
     */
    private Date inductionDate;
    /**
     * 离职日期
     */
    private Date quitDate;
    /**
     * 部门信息集合
     */
    private List<DeptInfoDTO> deptList;
}

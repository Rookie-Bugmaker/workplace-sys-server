package com.crrc.sys.model.org.person;

import lombok.Data;

import java.util.Date;

/**
 * 人员新增/修改入参
 * <p>
 * 只包含可编辑字段：本类用于表单参数绑定(非 @RequestBody)，
 * 因此严禁添加部门、公司等出参字段(如 List 类型的关联对象)，
 * 否则前端回传整行数据时会导致参数绑定失败。
 *
 * @author 李江涛
 * @date 2026-10-09
 */
@Data
public class SysOrgPersonSaveParam {

    /**
     * 人员ID(修改时必填)
     */
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
     * 类型(1:员工、2:供应商、3:客户)
     */
    private Long personType;
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
     * 语言(zh-CN:中文、en-US:英文)，
     * 取值：LanguageEnum
     */
    private String language;
}

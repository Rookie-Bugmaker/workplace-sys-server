package com.yunling.sys.entity.org.person;

import com.yunling.sys.entity.BaseEntity;

import java.util.Date;

/**
 * 组织架构-人员
 *
 * @author 谭永强
 * @date 2022-12-09
 */
public class SysOrgPerson extends BaseEntity {

    /**
     * 人员ID
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
     * 类型(来源于值集HR_EMPLOYEE_TYPE)
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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public Long getPersonType() {
        return personType;
    }

    public void setPersonType(Long personType) {
        this.personType = personType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getInductionDate() {
        return inductionDate;
    }

    public void setInductionDate(Date inductionDate) {
        this.inductionDate = inductionDate;
    }

    public Date getQuitDate() {
        return quitDate;
    }

    public void setQuitDate(Date quitDate) {
        this.quitDate = quitDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

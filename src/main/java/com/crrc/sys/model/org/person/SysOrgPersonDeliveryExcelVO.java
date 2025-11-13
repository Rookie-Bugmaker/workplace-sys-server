package com.crrc.sys.model.org.person;

import com.alibaba.excel.annotation.ExcelProperty;
import com.crrc.sys.entity.excel.BaseExcelEntity;

import java.util.Date;

/**
 * 组织架构-人员-excel导入\导出VO
 */
public class SysOrgPersonDeliveryExcelVO extends BaseExcelEntity {

    /**
     * 姓名
     */
    @ExcelProperty(value = {"*姓名"}, index = 0)
    private String fullName;
    /**
     * 工号
     */
    @ExcelProperty(value = {"*工号"}, index = 1)
    private String jobNo;
    /**
     * 类型(1:内部员工、2:供应商)
     */
    @ExcelProperty(value = {"*人员类型（员工/供应商）"}, index = 2)
    private String personType;
    /**
     * 人员状态(1:在职、2:离职)
     */
    @ExcelProperty(value = {"*人员状态（在职/离职）"}, index = 3)
    private String status;
    /**
     * 性别(M/F:男/女)
     */
    @ExcelProperty(value = {"*性别（男/女）"}, index = 4)
    private String gender;
    /**
     * 所属公司编码
     */
    @ExcelProperty(value = {"所属公司编码(多公司人员新增一行)"}, index = 5)
    private String CompanyCode;
    /**
     * 所属部门编码
     */
    @ExcelProperty(value = {"所属部门编码(多部门人员新增一行)"}, index = 6)
    private String DeptCode;
    /**
     * 直属上级工号
     */
    @ExcelProperty(value = {"上级人员工号"}, index = 7)
    private String parentPersonCode;
    /**
     * 人员职务
     */
    @ExcelProperty(value = {"人员职务"}, index = 8)
    private String position;
    /**
     * 手机号码
     */
    @ExcelProperty(value = {"手机号码"}, index = 9)
    private String phone;
    /**
     * 邮箱
     */
    @ExcelProperty(value = {"邮箱"}, index = 10)
    private String email;

    /**
     * 入职日期
     */
    @ExcelProperty(value = {"入职日期（如2025-09-03）"}, index = 11)
    private Date inductionDate;
    /**
     * 离职日期
     */
    @ExcelProperty(value = {"离职日期（如2025-09-03）"}, index = 12)
    private Date quitDate;



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

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompanyCode() {
        return CompanyCode;
    }

    public void setCompanyCode(String companyCode) {
        CompanyCode = companyCode;
    }

    public String getDeptCode() {
        return DeptCode;
    }

    public void setDeptCode(String deptCode) {
        DeptCode = deptCode;
    }

    public String getParentPersonCode() {
        return parentPersonCode;
    }

    public void setParentPersonCode(String parentPersonCode) {
        this.parentPersonCode = parentPersonCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}

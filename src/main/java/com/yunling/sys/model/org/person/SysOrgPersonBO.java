package com.yunling.sys.model.org.person;

import com.yunling.sys.model.org.dept.SysOrgDeptBO;

import java.util.Date;
import java.util.List;

/**
 * 员工信息业务对象
 *
 * @author 谭永强
 * @date 2024-01-30
 */
public class SysOrgPersonBO {

    /**
     * 人员ID
     */
    private Long personId;
    /**
     * 姓名
     */
    private String fullName;
    /**
     * 性别(M/W:男/女)
     */
    private String gender;
    /**
     * 工号
     */
    private String jobNo;
    /**
     * 类型(1:正式员工、2:实习、3:外包、4:劳务、5:三方)
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
    private Long status;
    /**
     * 入职日期
     */
    private Date inductionDate;
    /**
     * 离职日期
     */
    private Date quitDate;
    /**
     * 所属部门
     */
    private List<SysOrgDeptBO> deptList;

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

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
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

    public List<SysOrgDeptBO> getDeptList() {
        return deptList;
    }

    public void setDeptList(List<SysOrgDeptBO> deptList) {
        this.deptList = deptList;
    }
}

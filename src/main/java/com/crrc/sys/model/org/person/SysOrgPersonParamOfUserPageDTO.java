package com.crrc.sys.model.org.person;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 人员信息-查询用户用
 *
 * @author 谭永强
 * @date 2025-09-08
 */
public class SysOrgPersonParamOfUserPageDTO {

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 工号
     */
    private String jobNo;
    /**
     * 人员类型List
     */
    private List<Long> personTypeList = new ArrayList<>();

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Long> getPersonTypeList() {
        return personTypeList;
    }

    public void setPersonTypeList(List<Long> personTypeList) {
        this.personTypeList = personTypeList;
    }
}

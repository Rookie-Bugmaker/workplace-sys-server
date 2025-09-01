package com.cccr.sys.entity.org.person;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-12-09
 */
public class SysOrgPersonFindByPageParam {

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 偏移量，当pageSize=0时查询所有数据
     */
    private Integer pageSize;
    /**
     * 语言(zh-CN:中文、en-US:英文)，
     * 取值：LanguageEnum
     */
    private String language;
    /**
     * 姓名(模糊查询)
     */
    private String fullName;
    /**
     * 工号(模糊查询)
     */
    private String jobNo;
    /**
     * [姓名&工号]模糊搜索拼接条件(and | or)
     */
    private String montage;
    /**
     * 人员状态(1:在职、2:离职)
     */
    private Long status;
    /**
     * 人员类型(来源于值集:HR_EMPLOYEE_TYPE，实际存储的是值集ID)，多个人员类型使用逗号(,)分隔
     */
    private String personType;
    /**
     * 人员类型List，由personType字符串转换而来
     */
    private List<Long> personTypeList;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

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

    public String getMontage() {
        return montage;
    }

    public void setMontage(String montage) {
        this.montage = montage;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public List<Long> getPersonTypeList() {
        return personTypeList;
    }

    public void setPersonTypeList(List<Long> personTypeList) {
        this.personTypeList = personTypeList;
    }
}

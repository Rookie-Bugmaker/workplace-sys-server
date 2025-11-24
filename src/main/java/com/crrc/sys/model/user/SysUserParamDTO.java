package com.crrc.sys.model.user;

import com.crrc.sys.model.org.person.SysOrgPersonBO;

import java.util.List;

/**
 * 用户值对象
 *
 * @author 谭永强
 * @date 2024-01-30
 */
public class SysUserParamDTO {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户描述或姓名
     */
    private String nickName;
    /**
     * 部门id
     */
    private List<Long> deptIdList;
    private Integer pageNo = 1;

    private Integer pageSize = 10;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public List<Long> getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(List<Long> deptIdList) {
        this.deptIdList = deptIdList;
    }
}

package com.yunling.sys.model.user;

import com.yunling.sys.model.org.person.SysOrgPersonBO;

/**
 * 用户值对象
 *
 * @author 谭永强
 * @date 2024-01-30
 */
public class SysUserVO {

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户描述或姓名
     */
    private String userDescription;
    /**
     * 员工信息
     */
    private SysOrgPersonBO person;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public SysOrgPersonBO getPerson() {
        return person;
    }

    public void setPerson(SysOrgPersonBO person) {
        this.person = person;
    }
}

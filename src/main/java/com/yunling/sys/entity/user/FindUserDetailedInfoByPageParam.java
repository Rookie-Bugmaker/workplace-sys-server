package com.yunling.sys.entity.user;

import java.util.List;

/**
 * @author 谭永强
 * @date 2024-01-31
 */
public class FindUserDetailedInfoByPageParam {

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 偏移量，pageSize=0查询全部
     */
    private Integer pageSize;
    /**
     * 语言
     */
    private String language;
    /**
     * 用户ID集合
     */
    private List<Long> userIdList;
    /**
     * 用户名(模糊搜索)
     */
    private String userName;
    /**
     * 姓名(模糊搜索)
     */
    private String fullName;

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

    public List<Long> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Long> userIdList) {
        this.userIdList = userIdList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}

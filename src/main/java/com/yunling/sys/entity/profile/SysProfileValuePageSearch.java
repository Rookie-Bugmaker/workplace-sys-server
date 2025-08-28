package com.yunling.sys.entity.profile;

/**
 * 参数传递实体，用于封装多个参数
 *
 * @author 谭永强
 * @date 2022-12-20
 */
public class SysProfileValuePageSearch extends SysProfile{

    /**
     * 当前页码
     */
    private int page;
    /**
     * 偏移量
     */
    private int pageSize;
    /**
     * SQL语句
     */
    private String sql;
    /**
     * 模糊查询
     */
    private String fuzzyQuery;
    /**
     * 动态参数值列表，例如：{"personId": "12345"}
     * ${user.organizationId} 当前组织ID
     * ${user.roleId} 当前角色ID
     * ${user.userId} 当前用户ID
     * ${?} 其他
     */
    private String paramMapStr;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getFuzzyQuery() {
        return fuzzyQuery;
    }

    public void setFuzzyQuery(String fuzzyQuery) {
        this.fuzzyQuery = fuzzyQuery;
    }

    public String getParamMapStr() {
        return paramMapStr;
    }

    public void setParamMapStr(String paramMapStr) {
        this.paramMapStr = paramMapStr;
    }
}

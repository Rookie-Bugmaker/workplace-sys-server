package com.crrc.sys.entity.api;

/**
 * 权限分页查询参数
 *
 * @author 谭永强
 * @date 2024-01-03
 */
public class SysApiAuthPageDTO {

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 偏移量，当pageSize=0时查询所有数据
     */
    private Integer pageSize;
    /**
     * 权限URI(可使用通配符)
     */
    private String authUri;
    /**
     * 权限类型(A:白名单、B:黑名单、C:无需认证)
     * 白名单：该API不需要权限校验。
     * 黑名单：该API禁止前端调用。
     * 无需认证：该API不需要登录即可访问。
     */
    private String authType;
    /**
     * 描述
     */
    private String authDesc;

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

    public String getAuthUri() {
        return authUri;
    }

    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthDesc() {
        return authDesc;
    }

    public void setAuthDesc(String authDesc) {
        this.authDesc = authDesc;
    }
}

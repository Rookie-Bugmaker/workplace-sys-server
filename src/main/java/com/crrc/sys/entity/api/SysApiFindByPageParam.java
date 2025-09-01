package com.crrc.sys.entity.api;

/**
 * API信息分页查询参数
 *
 * @author 谭永强
 * @date 2022-09-13
 */
public class SysApiFindByPageParam {

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 偏移量，当pageSize=0时查询所有数据
     */
    private Integer pageSize;
    /**
     * API名称(模糊搜索)
     */
    private String apiName;
    /**
     * URI(模糊搜索)
     */
    private String apiUri;
    /**
     * API描述(模糊搜索)
     */
    private String apiDesc;

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

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiUri() {
        return apiUri;
    }

    public void setApiUri(String apiUri) {
        this.apiUri = apiUri;
    }

    public String getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }
}

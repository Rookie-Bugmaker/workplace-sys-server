package com.cccr.sys.entity.function;

import java.util.List;
import java.util.Map;

/**
 * API信息分页查询参数
 *
 * @author 谭永强
 * @date 2022-09-07
 */
public class SysFunctionFindByPageParam {

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
     * 功能编码(模糊搜索)
     */
    private String functionCode;
    /**
     * 功能名称(模糊搜索)
     */
    private String functionName;
    /**
     * 路径(模糊搜索)
     */
    private String path;
    /**
     * 参数(模糊搜索)
     */
    private String parameters;
    /**
     * 组件(模糊搜索)
     */
    private String component;
    /**
     * 路由名称(模糊搜索)
     */
    private String name;
    /**
     * 重定向地址(模糊搜索)
     */
    private String redirect;
    /**
     * 描述(模糊搜索)
     */
    private String functionDescription;
	/**
	 * 列-值集
	 * {列编码: 值集}
	 */
	private Map<String, List<Object>> columnValueSetMap;

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

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getFunctionDescription() {
        return functionDescription;
    }

    public void setFunctionDescription(String functionDescription) {
        this.functionDescription = functionDescription;
    }

	public Map<String, List<Object>> getColumnValueSetMap() {
		return columnValueSetMap;
	}

	public void setColumnValueSetMap(Map<String, List<Object>> columnValueSetMap) {
		this.columnValueSetMap = columnValueSetMap;
	}
}

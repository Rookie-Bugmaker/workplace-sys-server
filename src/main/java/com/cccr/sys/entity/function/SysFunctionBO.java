package com.cccr.sys.entity.function;

import com.cccr.sys.entity.BaseEntity;
import com.cccr.sys.entity.api.SysApi;

import java.util.List;

/**
 * 导出/导入对象
 *
 * @author 谭永强
 * @date 2024-01-11
 */
public class SysFunctionBO extends BaseEntity {

    /**
     * 主键ID
     */
    private Long functionId;
    /**
     * 父功能ID
     */
    private Long parentId;
    /**
     * 功能代码
     */
    private String functionCode;
    /**
     * 功能类型(A:功能、B:按钮、C:列)
     */
    private String functionType;
    /**
     * 路径
     */
    private String path;
    /**
     * 参数
     */
    private String parameters;
    /**
     * 打开方式 0:内部、1:外部
     */
    private Integer openMethod;
    /**
     * 是否缓存 1:是、0:否
     */
    private Boolean keepAlive;
    /**
     * 组件
     */
    private String component;
    /**
     * 路由名称
     */
    private String name;
    /**
     * 重定向地址
     */
    private String redirect;
    /**
     * 是否在菜单中隐藏子组件 1:是、0:否
     */
    private Boolean hideChildrenInMenu;
    /**
     * 是否在菜单中隐藏该功能 1:是、0:否
     */
    private Boolean hideInMenu;
    /**
     * 是否在tab选项卡中锁定 1:是、0:否
     */
    private Boolean lockInTab;
    /**
     * 是否在合并同名tab选项卡 1:是、0:否
     */
    private Boolean mergeTab;
    /**
     * 描述
     */
    private String functionDescription;
    /**
     * 图标
     */
    private String icon;
    /**
     * 值集ID
     */
    private Long vsId;
    /**
     * 页面ID
     */
    private Long pageId;
    /**
     * 绑定API集合
     */
    private List<SysApi> apiList;
    /**
     * 多语言数据
     */
    private List<SysFunctionTl> functionTlList;

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
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

    public Integer getOpenMethod() {
        return openMethod;
    }

    public void setOpenMethod(Integer openMethod) {
        this.openMethod = openMethod;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
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

    public Boolean getHideChildrenInMenu() {
        return hideChildrenInMenu;
    }

    public void setHideChildrenInMenu(Boolean hideChildrenInMenu) {
        this.hideChildrenInMenu = hideChildrenInMenu;
    }

    public Boolean getHideInMenu() {
        return hideInMenu;
    }

    public void setHideInMenu(Boolean hideInMenu) {
        this.hideInMenu = hideInMenu;
    }

    public Boolean getLockInTab() {
        return lockInTab;
    }

    public void setLockInTab(Boolean lockInTab) {
        this.lockInTab = lockInTab;
    }

    public Boolean getMergeTab() {
        return mergeTab;
    }

    public void setMergeTab(Boolean mergeTab) {
        this.mergeTab = mergeTab;
    }

    public String getFunctionDescription() {
        return functionDescription;
    }

    public void setFunctionDescription(String functionDescription) {
        this.functionDescription = functionDescription;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getVsId() {
        return vsId;
    }

    public void setVsId(Long vsId) {
        this.vsId = vsId;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public List<SysApi> getApiList() {
        return apiList;
    }

    public void setApiList(List<SysApi> apiList) {
        this.apiList = apiList;
    }

    public List<SysFunctionTl> getFunctionTlList() {
        return functionTlList;
    }

    public void setFunctionTlList(List<SysFunctionTl> functionTlList) {
        this.functionTlList = functionTlList;
    }
}

package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.function.*;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-15
 */
public interface SysFunctionService {

    /**
     * 功能分页查询
     *
     * @param param 参数
     * @return 结果
     */
    PaginationGrid<SysFunction> findByPage(SysFunctionFindByPageParam param);

    /**
     * 功能新增
     *
     * @param function 功能信息
     * @return 结果
     */
    int add(SysFunction function);

    /**
     * 功能修改
     *
     * @param function 功能信息
     * @return 结果
     */
    int update(SysFunction function);

    /**
     * 功能删除
     *
     * @param functionIds 功能ID集合
     * @return 结果
     */
    int delete(List<Long> functionIds);

    /**
     * 根据功能ID查询功能信息
     *
     * @param functionId 功能ID
     * @param language   语言
     * @return 功能信息
     */
    SysFunction findById(long functionId, String language);

    /**
     * 功能接口绑定
     *
     * @param functionId      功能ID
     * @param functionApiList 功能接口信息
     * @return 结果
     */
    int bindingApi(long functionId, List<SysFunctionApiPO> functionApiList);

    /**
     * 根据功能编码查询功能信息
     *
     * @param functionCode 功能编码
     * @param language     语言
     * @return 结果
     */
    List<SysFunction> findByCode(String functionCode, String language);

    /**
     * 根据页面ID查询功能信息
     *
     * @param pageId   页面ID
     * @param language 语言
     * @return 结果
     */
    SysFunction findByPageId(long pageId, String language);

    /**
     * 根据功能ID查询按钮权限列表
     *
     * @param functionId 功能ID
     * @return 按钮权限集合
     */
    List<SysFunction> findButtonById(long functionId);

    /**
     * 根据路由名称查询功能信息
     *
     * @param routeName 路由名称
     * @return 结果
     */
    SysFunction findByRouteName(String routeName);

    /**
     * 导出数据查询
     *
     * @param functionIdList 功能ID集合
     * @return 结果
     */
    List<SysFunctionBO> exportData(List<Long> functionIdList);

    /**
     * 导入
     *
     * @param sysFunctionBoListImport 参数
     * @return 结果
     */
    int importData(List<SysFunctionBO> sysFunctionBoListImport);

    /**
     * 根据功能ID查询api接口(批量)
     *
     * @param functionIds 功能ID集合
     * @return uri接口
     */
    List<FunctionApiUri> getApiUris(List<Long> functionIds);

    void init();

    void initWorkflow();

    /**
     * 根据功能ID查询功能信息(最小查询)
     *
     * @param functionId 功能ID
     * @param language   语言
     * @return 功能信息
     */
    SysFunction findMinById(long functionId, String language);

    /**
     * 根据项目编码查询功能列表
     *
     * @param projectId 项目ID
     * @return 功能ID集合
     */
    List<SysFunction> findByProjectId(Long projectId);
}

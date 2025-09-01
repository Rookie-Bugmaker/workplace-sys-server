package com.cccr.sys.service;

import com.cccr.sys.common.PaginationGrid;
import com.cccr.sys.entity.api.SysApi;
import com.cccr.sys.entity.api.SysApiFindByPageParam;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-14
 */
public interface SysApiService {

    /**
     * API分页查询
     *
     * @param param 查询条件
     * @return 结果
     */
    PaginationGrid<SysApi> findByPage(SysApiFindByPageParam param);

    /**
     * API新增
     *
     * @param api API信息
     * @return 结果
     */
    int add(SysApi api);

    /**
     * API修改
     *
     * @param api API信息
     * @return 结果
     */
    int update(SysApi api);

    /**
     * API删除
     *
     * @param ids API信息ID，多个以逗号","拼接
     * @return 结果
     */
    int delete(List<Long> ids);

    /**
     * 根据ID查询API信息
     *
     * @param id API信息ID
     * @return 结果
     */
    SysApi findById(long id);

    /**
     * 根据ID查询API信息(批量)
     *
     * @param ids API信息ID
     * @return 结果
     */
    List<SysApi> findByIds(List<Long> ids);

    /**
     * 根据URI查询接口信息
     *
     * @param uri 接口地址
     * @return 结果
     */
    SysApi findByUri(String uri);

    /**
     * 根据请求类型+请求地址查询API信息
     *
     * @param apiMethod 请求类型
     * @param apiUri    请求地址
     * @return API信息
     */
    SysApi findByMethodAndUri(String apiMethod, String apiUri);

    /**
     * 查询所有API
     *
     * @return API集合
     */
    List<SysApi> findAll();

    /**
     * 导入
     *
     * @param apiListImport API信息列表
     * @return 结果
     */
    int importList(List<SysApi> apiListImport);

    /**
     * 根据URI查询API信息(批量)
     *
     * @param uriList 参数
     * @return 结果
     */
    List<SysApi> findByUris(List<String> uriList);
}

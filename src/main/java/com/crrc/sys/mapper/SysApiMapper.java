package com.crrc.sys.mapper;

import com.crrc.sys.entity.api.SysApi;
import com.crrc.sys.entity.api.SysApiFindByPageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-14
 */
public interface SysApiMapper {

    /**
     * API查询
     *
     * @param param 查询条件
     * @return 结果
     */
    List<SysApi> findByFuzzy(SysApiFindByPageParam param);

    /**
     * API新增
     *
     * @param api API信息
     * @return 结果
     */
    int insert(SysApi api);

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
     * @param ids API信息ID集合
     * @return 结果
     */
    int delete(@Param("ids") List<Long> ids);

    /**
     * 根据ID查询API信息
     *
     * @param id API信息ID集合
     * @return 结果
     */
    SysApi findById(@Param("id") long id);

    /**
     * 根据ID查询API信息(批量)
     *
     * @param ids API信息ID
     * @return 结果
     */
    List<SysApi> findByIds(@Param("ids") List<Long> ids);

    /**
     * 根据URI查询接口信息
     *
     * @param uri 接口地址
     * @return 结果
     */
    SysApi findByUri(@Param("uri") String uri);

    /**
     * 根据请求类型+请求地址查询API信息
     *
     * @param apiMethod 请求类型
     * @param apiUri    请求地址
     * @return API信息
     */
    SysApi findByMethodAndUri(@Param("apiMethod") String apiMethod, @Param("apiUri") String apiUri);

    /**
     * 查询所有API信息
     *
     * @return API集合
     */
    List<SysApi> findAll();

    /**
     * 根据请求类型+请求地址查询API信息(批量)
     *
     * @param apiList API集合
     * @return 返回值
     */
    List<SysApi> findByMethodAndUris(@Param("apiList") List<SysApi> apiList);

    /**
     * 根据URI查询接口信息(批量)
     *
     * @param apiUriList 接口地址集合
     * @return 结果
     */
    List<SysApi> findByUris(@Param("uriList") List<String> apiUriList);
}
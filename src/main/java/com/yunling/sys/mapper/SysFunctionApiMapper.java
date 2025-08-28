package com.yunling.sys.mapper;

import com.yunling.sys.entity.function.SysFunctionApi;
import com.yunling.sys.entity.function.SysFunctionApiBO;
import com.yunling.sys.entity.function.SysFunctionApiPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-19
 */
public interface SysFunctionApiMapper {

    /**
     * 根据功能ID删除功能接口数据
     *
     * @param functionId 功能ID
     * @return 成功删除的数据量
     */
    int deleteByFunctionId(@Param("functionId") Long functionId);

    /**
     * 根据功能ID删除功能接口数据(批量)
     *
     * @param functionIdList 功能ID集合
     * @return 成功删除的数据量
     */
    int deleteByFunctionIds(@Param("functionIdList") List<Long> functionIdList);

    /**
     * 功能接口保存
     *
     * @param functionApiList 功能ID
     * @return 结果
     */
    int insert(@Param("functionApiList") List<SysFunctionApiPO> functionApiList);

    /**
     * 根据功能ID集合查询接口权限(批量)
     *
     * @param functionIdList 功能ID集合，为null则查询所有
     * @return 结果
     */
    List<SysFunctionApiBO> findByFunctionIds(@Param("functionIdList") List<Long> functionIdList);

    /**
     * 根据功能ID集合查询接口权限
     *
     * @param functionId 功能ID
     * @return 结果
     */
    SysFunctionApiBO findByFunctionId(@Param("functionId") Long functionId);

    /**
     * 查询所有功能API总数
     *
     * @return 结果
     */
    int findAllFunctionApi();

    /**
     * 分页查询功能API列表
     *
     * @param offset 起
     * @param endset 止
     * @return 结果
     */
    List<SysFunctionApi> findPageFunctionApi(@Param("offset") Integer offset, @Param("endset") Integer endset);

    /**
     * 根据API_ID集合删除功能接口数据
     *
     * @param ids API_ID集合
     * @return 删除的数据量
     */
    int deleteByApiIds(@Param("apiIdList") List<Long> ids);
}

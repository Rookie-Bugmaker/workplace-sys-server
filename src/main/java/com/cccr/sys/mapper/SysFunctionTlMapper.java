package com.cccr.sys.mapper;

import com.cccr.sys.entity.function.SysFunctionTl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2024-01-11
 */
public interface SysFunctionTlMapper {

    /**
     * 功能(多语言)新增
     *
     * @param functionTlList 功能(多语言)信息
     * @return 结果
     */
    int insertTl(@Param("functionTlList") List<SysFunctionTl> functionTlList);

    /**
     * 功能(多语言)删除
     *
     * @param functionIds 功能ID集合
     * @return 结果
     */
    int deleteTl(@Param("functionIds") List<Long> functionIds);

    /**
     * 根据功能ID查询多语言
     *
     * @param functionIdList 功能ID集合
     * @return 结果
     */
    List<SysFunctionTl> findByFunctionIds(@Param("functionIdList") List<Long> functionIdList);

}

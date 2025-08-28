package com.yunling.sys.service;

import com.yunling.sys.entity.function.column.SysFunctionColumn;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-10-31
 */
public interface SysFunctionColumnService {

	/**
	 * 列权限新增
	 *
	 * @param functionColumn 列权限信息
	 * @return 结果
	 */
	int add(SysFunctionColumn functionColumn);

	/**
	 * 根据块编码查询块信息
	 *
	 * @param blockCode 块编码
	 * @param language  语言
	 * @return 结果
	 */
	SysFunctionColumn findByBlockCode(String blockCode, String language);

	/**
	 * 根据列编码查询列信息
	 *
	 * @param columnCode 列编码
	 * @param language   语言
	 * @param blockId    块ID
	 * @return 结果
	 */
	SysFunctionColumn findByColumnCode(String columnCode, String language, long blockId);

	/**
	 * 列权限修改
	 *
	 * @param functionColumn 列权限信息
	 * @return 结果
	 */
	int update(SysFunctionColumn functionColumn);

	/**
	 * 列权限删除
	 *
	 * @param ids (块/列)权限ID
	 * @return 结果
	 */
	int delete(List<Long> ids);

	/**
	 * 根据功能ID查询列权限(批量)
	 *
	 * @param functionIdList 功能ID集合
	 * @param language       语言
	 * @return 结果
	 */
    List<SysFunctionColumn> findByFunctionIds(List<Long> functionIdList, String language);

	/**
	 * 根据列ID查询列信息(批量)
	 *
	 * @param columnIdList 列ID集合
	 * @param language     语言
	 * @return 结果
	 */
    List<SysFunctionColumn> findByColumnIds(List<Long> columnIdList, String language);
}

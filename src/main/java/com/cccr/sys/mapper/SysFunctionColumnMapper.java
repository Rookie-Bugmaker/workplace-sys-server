package com.cccr.sys.mapper;

import com.cccr.sys.entity.function.column.SysFunctionColumn;
import com.cccr.sys.entity.function.column.SysFunctionColumnTl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-10-31
 */
public interface SysFunctionColumnMapper {

	/**
	 * 根据ID查询列权限信息
	 *
	 * @param blockIdList 块ID
	 * @param language    语言
	 * @return 结果
	 */
	List<SysFunctionColumn> findByParentIds(@Param("blockIdList") List<Long> blockIdList, @Param("language") String language);

	/**
	 * 列权限-多语言新增
	 *
	 * @param functionColumnTlList 列权限-多语言
	 */
	void insertTl(@Param("list") List<SysFunctionColumnTl> functionColumnTlList);

	/**
	 * 列权限新增
	 *
	 * @param functionColumn 列权限
	 * @return 结果
	 */
	int insert(SysFunctionColumn functionColumn);

	/**
	 * 根据块编码查询块信息
	 *
	 * @param blockCode 块编码
	 * @param language  语言
	 * @return 结果
	 */
	SysFunctionColumn findByBlockCode(@Param("blockCode")String blockCode, @Param("language")String language);

	/**
	 * 根据列编码查询列信息
	 *
	 * @param columnCode 列编码
	 * @param language   语言
	 * @param blockId    块ID
	 * @return 结果
	 */
	SysFunctionColumn findByColumnCode(@Param("columnCode") String columnCode, @Param("language") String language, @Param("blockId") long blockId);

	/**
	 * 列权限-多语言修改
	 * @param functionColumn 列权限
	 */
	void updateTl(SysFunctionColumn functionColumn);

	/**
	 * 列权限修改
	 * @param functionColumn 列权限
	 * @return 结果
	 */
	int update(SysFunctionColumn functionColumn);

	/**
	 * 列权限-多语言删除
	 * @param ids 列权限ID
	 */
	void deleteTlByIds(@Param("ids") List<Long> ids);

	/**
	 * 列权限删除
	 * @param ids 列权限ID
	 */
	int deleteByIds(@Param("ids") List<Long> ids);

	/**
	 * 根据功能ID查询块权限(批量)
	 *
	 * @param functionIdList 功能ID集合
	 * @param language       语言
	 * @return 结果
	 */
    List<SysFunctionColumn> findByFunctionIds(@Param("functionIdList") List<Long> functionIdList, @Param("language") String language);

	/**
	 * 根据角色ID和快编码查询未拥有的列权限
	 *
	 * @param roleId    角色ID
	 * @param blockCode 块编码
	 * @return 未拥有的列权限
	 */
    List<SysFunctionColumn> getHaveNoColumnCode(@Param("roleId") long roleId, @Param("blockCode") String blockCode);

	/**
	 * 根据列ID查询列信息(批量)
	 *
	 * @param columnIdList 列ID集合
	 * @param language     语言
	 * @return 结果
	 */
    List<SysFunctionColumn> findByColumnIds(@Param("columnIdList") List<Long> columnIdList, @Param("language") String language);
}
package com.yunling.sys.service;

import com.yunling.sys.common.PaginationGrid;
import com.yunling.sys.entity.profile.SysProfile;
import com.yunling.sys.entity.profile.SysProfileFindByPageParam;
import com.yunling.sys.entity.profile.SysProfileValuePageSearch;
import com.yunling.sys.entity.profile.ValueSet;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-12-19
 */
public interface SysProfileService {

	/**
	 * 配置文件分页查询
	 *
	 * @param param 参数
	 * @return 结果
	 */
	PaginationGrid<SysProfile> findByPage(SysProfileFindByPageParam param);

	/**
	 * 配置文件新增
	 *
	 * @param profile 配置信息
	 * @return 结果
	 */
	int add(SysProfile profile);

	/**
	 * 配置文件修改
	 *
	 * @param profile 配置信息
	 * @return 结果
	 */
	int update(SysProfile profile);

	/**
	 * 根据配置ID查询配置信息
	 *
	 * @param profileId 配置ID
	 * @param language  语言
	 * @return 结果
	 */
	SysProfile findById(long profileId, String language);

	/**
	 * 配置文件删除
	 *
	 * @param profileIdList 配置ID
	 * @return 结果
	 */
	int delete(List<Long> profileIdList);

	/**
	 * 根据配置编码查询配置文件信息
	 *
	 * @param profileCode 配置编码
	 * @return 结果
	 */
	SysProfile findByCode(String profileCode);

	/**
	 * 根据SQL分页查询值集
	 *
	 * @param search 参数
	 * @return 结果
	 */
	PaginationGrid<ValueSet> findValuePageBySql(SysProfileValuePageSearch search);

	/**
	 * 根据配置分类查询配置信息
	 *
	 * @param profileType 配置ID
	 * @param language    语言
	 * @return 结果
	 */
    List<SysProfile> findByType(String profileType, String language);

	/**
	 * 根据配置文件ID查询配文件信息(批量)
	 *
	 * @param profileIdList 配置文件ID集合
	 * @param language      语言
	 * @return 结果
	 */
    List<SysProfile> findByIds(List<Long> profileIdList, String language);
}

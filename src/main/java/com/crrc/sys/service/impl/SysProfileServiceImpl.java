package com.crrc.sys.service.impl;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.profile.*;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.mapper.mapperImpl.SysProfileMapperImpl;
import com.crrc.sys.mapper.mapperImpl.SysProfileTlMapperImpl;
import com.crrc.sys.service.SysProfileService;
import com.crrc.sys.entity.profile.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-12-19
 */
@Service
public class SysProfileServiceImpl implements SysProfileService {

	@Autowired
	private SysProfileMapperImpl profileMapper;
	@Autowired
	private SysProfileTlMapperImpl profileTlMapper;

	/**
	 * 配置文件分页查询
	 *
	 * @param param 参数
	 * @return 结果
	 */
	@Override
	public PaginationGrid<SysProfile> findByPage(SysProfileFindByPageParam param) {
		PageHelper.startPage(param.getPage(), param.getPageSize());
		List<SysProfile> rows = profileMapper.findByFuzzy(param);
		PageInfo<SysProfile> pageInfo = new PageInfo<>(rows);
		return PaginationGrid.success(pageInfo);
	}

	/**
	 * 配置文件新增
	 *
	 * @param profile 配置信息
	 * @return 结果
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int add(SysProfile profile) {
		List<SysProfileTl> tlList = new ArrayList<>(2);
		SysProfileTl tl = new SysProfileTl();
		BeanUtils.copyProperties(profile, tl);
		tl.setLanguage(LanguageEnum.ZH_CN.getValue());
		tlList.add(tl);
		tl = new SysProfileTl();
		BeanUtils.copyProperties(profile, tl);
		tl.setLanguage(LanguageEnum.EN_US.getValue());
		tlList.add(tl);
		profileTlMapper.insertList(tlList);
		return profileMapper.insert(profile);
	}

	/**
	 * 配置文件修改
	 *
	 * @param profile 配置信息
	 * @return 结果
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int update(SysProfile profile) {
		profileTlMapper.update(profile);
		return profileMapper.update(profile);
	}

	/**
	 * 根据配置ID查询配置信息
	 *
	 * @param profileId 配置ID
	 * @param language  语言
	 * @return 结果
	 */
	@Override
	public SysProfile findById(long profileId, String language) {
		return profileMapper.findById(profileId, language);
	}

	/**
	 * 配置文件删除
	 *
	 * @param profileIdList 配置ID
	 * @return 结果
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int delete(List<Long> profileIdList) {
		profileTlMapper.delete(profileIdList);
		return profileMapper.delete(profileIdList);
	}

	/**
	 * 根据配置编码查询配置文件信息
	 *
	 * @param profileCode 配置编码
	 * @return 结果
	 */
	@Override
	public SysProfile findByCode(String profileCode) {
		return profileMapper.findByCode(profileCode);
	}

	/**
	 * 根据SQL分页查询值集
	 *
	 * @param search 参数
	 * @return 结果
	 */
	@Override
	public PaginationGrid<ValueSet> findValuePageBySql(SysProfileValuePageSearch search) {
		PageHelper.startPage(search.getPage(), search.getPageSize());
		List<ValueSet> rows = profileMapper.executeSql(search);
		PageInfo<ValueSet> pageInfo = new PageInfo<>(rows);
		return PaginationGrid.success(pageInfo);
	}

	/**
	 * 根据配置分类查询配置信息
	 *
	 * @param profileType 配置ID
	 * @param language    语言
	 * @return 结果
	 */
	@Override
	public List<SysProfile> findByType(String profileType, String language) {
		return profileMapper.findByType(profileType, language);
	}

	/**
	 * 根据配置文件ID查询配文件信息(批量)
	 *
	 * @param profileIdList 配置文件ID集合
	 * @param language      语言
	 * @return 结果
	 */
	@Override
	public List<SysProfile> findByIds(List<Long> profileIdList, String language) {
		return profileMapper.findByIds(profileIdList, language);
	}
}

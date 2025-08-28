package com.yunling.sys.mapper;

import com.yunling.sys.entity.profile.SysProfile;
import com.yunling.sys.entity.profile.SysProfileTl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-12-19
 */
public interface SysProfileTlMapper {

	/**
	 * 配置文件-国际化新增
	 *
	 * @param tlList 配置信息-国际化
	 */
	void insertList(@Param("list") List<SysProfileTl> tlList);

	/**
	 * 配置文件-国际化修改
	 *
	 * @param profile 配置信息
	 */
	void update(SysProfile profile);

	/**
	 * 根据配置ID删除
	 *
	 * @param profileIdList 配置ID
	 */
	void delete(@Param("profileIdList") List<Long> profileIdList);
}

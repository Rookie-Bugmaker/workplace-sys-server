package com.yunling.sys.mapper;

import com.yunling.sys.entity.org.person.SysOrgPerson;
import com.yunling.sys.entity.org.person.SysOrgPersonTl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-12-09
 */
public interface SysOrgPersonTlMapper {

	/**
	 * 人员-国际化新增
	 * @param personTlList 人员信息
	 */
	void insertList(@Param("list") List<SysOrgPersonTl> personTlList);

	/**
	 * 人员-国际化修改
	 * @param person 人员信息
	 */
	void update(SysOrgPerson person);

	/**
	 * 根据人员ID删除
	 * @param personIdList 人员ID
	 */
	void delete(@Param("personIdList") List<Long> personIdList);
}

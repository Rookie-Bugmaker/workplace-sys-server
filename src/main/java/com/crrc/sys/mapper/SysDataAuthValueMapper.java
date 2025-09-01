package com.crrc.sys.mapper;

import com.crrc.sys.entity.auth.SysDataAuthValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2023-03-22
 */
public interface SysDataAuthValueMapper {

	/**
	 * 新增数据权限值(批量)
	 *
	 * @param dataAuthValueList 参数
	 */
	void insertList(@Param("list") List<SysDataAuthValue> dataAuthValueList);

	/**
	 * 根据对象ID删除数据权限值
	 *
	 * @param objectId 对象ID(用户ID或角色ID)
	 */
	void delete(@Param("objectId") long objectId);

	/**
	 * 根据对象ID删除数据权限值(批量)
	 *
	 * @param objectIds 对象ID(用户ID或角色ID)
	 */
	void deleteByObjectIds(@Param("objectIds") List<Long> objectIds);

	/**
	 * 根据数据权限ID查询数据权限值
	 *
	 * @param authIdList 数据权限ID
	 * @return 结果
	 */
	List<SysDataAuthValue> findByAuthIds(@Param("list") List<Long> authIdList);
}

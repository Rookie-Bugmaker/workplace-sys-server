package com.crrc.sys.mapper;

import com.crrc.sys.entity.auth.SysDataAuth;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2023-03-22
 */
public interface SysDataAuthMapper {

	/**
	 * 添加数据权限(批量)
	 *
	 * @param dataAuthList 数据权限
	 */
	void insertList(@Param("list") List<SysDataAuth> dataAuthList);

	/**
	 * 根据对象ID删除数据权限
	 *
	 * @param objectId 对象ID
	 */
	void delete(@Param("objectId") long objectId);

	/**
	 * 根据对象ID删除数据权限(批量)
	 *
	 * @param objectIds 对象ID
	 */
	void deleteByObjectIds(@Param("objectIds") List<Long> objectIds);

	/**
	 * 根据对象ID删除数据权限
	 *
	 * @param objectId 对象ID
	 * @return 结果
	 */
	List<SysDataAuth> findByObjectId(@Param("objectId") long objectId);
}

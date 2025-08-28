package com.yunling.sys.mapper;

import com.yunling.sys.entity.homepage.HomePageParam;
import com.yunling.sys.entity.homepage.SysHomePageComponent;
import com.yunling.sys.entity.homepage.SysHomePageComponentUser;

import java.util.List;

/**
 * <p>
 * 主页组件定义表 Mapper 接口
 * </p>
 *
 * @author fucheng
 * @since 2023-05-19
 */
public interface SysHomePageComponentMapper {

	/**
	 * 新增主页组件
	 */
	Integer add(SysHomePageComponent param);

	/**
	 * 查询组件列表
	 */
	List<SysHomePageComponent> findList(HomePageParam param);

	/**
	 * 删除组件
	 */
	Integer deleteById(Long componentId);

	/**
	 * 按id更新组件
	 */
	Integer updateById(SysHomePageComponent param);

	/**
	 * 新增组件用户
	 */
	Integer addUser(List<SysHomePageComponentUser> list);

	/**
	 * 查询分配的用户列表
	 */
	List<Long> findUserByComponentId(Long componentId);

	/**
	 * 根据用户组件列表
	 */
	List<SysHomePageComponent> findComponentByUserId(Long userId);

	/**
	 * 根据用户类型查询组件
	 */
	List<SysHomePageComponent> findComponentByUserType(String userType);

	/**
	 * 删除组件已分配用户
	 */
	Integer deleteUserByComponentId(Long componentId);
}

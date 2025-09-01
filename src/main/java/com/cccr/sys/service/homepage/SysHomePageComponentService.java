package com.cccr.sys.service.homepage;

import com.cccr.sys.common.PaginationGrid;
import com.cccr.sys.entity.homepage.HomePageParam;
import com.cccr.sys.entity.homepage.SysHomePageComponent;
import com.cccr.sys.entity.homepage.SysHomePageComponentUserParam;

import java.util.List;

/**
 * <p>
 * 主页组件定义表 服务类
 * </p>
 *
 * @author fucheng
 * @since 2023-05-19
 */
public interface SysHomePageComponentService  {

	/**
	 * 新增主页组件
	 */
	Integer add(SysHomePageComponent param);

	/**
	 * 查询组件列表
	 */
	PaginationGrid<SysHomePageComponent> pageList(HomePageParam param);

	/**
	 * 删除组件
	 */
	Integer deleteById(Long componentId);

	/**
	 * 按id更新组件
	 */
	Integer updateById(SysHomePageComponent param);

	/**
	 * 组件分配用户
	 */
	Integer addUser(SysHomePageComponentUserParam param);

	/**
	 * 查询已分配的用户列表
	 */
	List<Long> findUserByComponentId(Long componentId);

	/**
	 * 根据用户id查询组件列表
	 */
	List<SysHomePageComponent> findComponentByUserId(Long userId);
}

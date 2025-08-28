package com.yunling.sys.mapper;

import com.yunling.sys.entity.homepage.SysHomePageUserConfig;

/**
 * 1 * @Author: fucheng
 * 2 * @Description:
 * 3 * @Date: 2023/5/22
 */
public interface SysHomePageUserConfigMapper {
	/**
	 * 保存用户配置
	 */
	Integer add(SysHomePageUserConfig param);

	/**
	 * 查询用户组件配置
	 */
	SysHomePageUserConfig findByUserId(Long userId);

	/**
	 * 根据用户id删除配置
	 */
	Integer deleteByUserId(Long userId);
}

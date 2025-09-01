package com.crrc.sys.service.homepage;

import com.crrc.sys.entity.homepage.SysHomePageUserConfig;

/**
 * 1 * @Author: fucheng
 * 2 * @Description:
 * 3 * @Date: 2023/5/22
 */
public interface SysHomePageUserConfigService {
	/**
	 * 新增用户配置
	 */
	Integer add(SysHomePageUserConfig param);

	/**
	 * 查询用户配置
	 */
	SysHomePageUserConfig findByUserId(Long userId);
}

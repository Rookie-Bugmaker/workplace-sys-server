package com.crrc.sys.service.homepage.imp;

import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.entity.homepage.SysHomePageUserConfig;
import com.crrc.sys.mapper.mapperImpl.SysHomePageUserConfigMapperImpl;
import com.crrc.sys.service.homepage.SysHomePageUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 1 * @Author: fucheng
 * 2 * @Description:
 * 3 * @Date: 2023/5/22
 */
@Service
public class SysHomePageUserConfigServiceImp implements SysHomePageUserConfigService {
	@Autowired
	private SysHomePageUserConfigMapperImpl sysHomePageUserConfigMapper;
	@Resource
	private IdKeyGenerator idKeyGenerator;

	/**
	 * 保存用户配置
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer add(SysHomePageUserConfig param) {
		//先删除原来的配置
		sysHomePageUserConfigMapper.deleteByUserId(param.getUserId());
		param.setConfigId(idKeyGenerator.getId());
		return sysHomePageUserConfigMapper.add(param);
	}

	/**
	 * 查询用户配置
	 * @param userId 用户id
	 */
	@Override
	public SysHomePageUserConfig findByUserId(Long userId) {
		return sysHomePageUserConfigMapper.findByUserId(userId);
	}
}

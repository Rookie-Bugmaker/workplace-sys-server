package com.yunling.sys.service;

import com.yunling.sys.entity.auth.SysDataAuth;

import java.util.List;

/**
 * @author 谭永强
 * @date 2023-03-23
 */
public interface SysDataAuthService {

	/**
	 * 根据对象ID删除数据权限
	 *
	 * @param objectId 对象ID
	 * @return 结果
	 */
	List<SysDataAuth> findByObjectId(long objectId);
}

package com.crrc.sys.service;

import com.crrc.sys.entity.auth.SysDataAuth;

import java.util.List;
import java.util.Map;

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

    Map<String, List<String>> findDataAuthsByObjectId(Long userId, String value);

	Map<String, List<String>> getDataAuth();
}

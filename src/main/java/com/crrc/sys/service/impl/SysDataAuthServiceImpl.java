package com.crrc.sys.service.impl;

import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.mapper.mapperImpl.SysDataAuthMapperImpl;
import com.crrc.sys.mapper.mapperImpl.SysDataAuthValueMapperImpl;
import com.crrc.sys.service.SysDataAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2023-03-23
 */
@Service
public class SysDataAuthServiceImpl implements SysDataAuthService {

	@Autowired
	private SysDataAuthMapperImpl dataAuthMapper;
	@Autowired
	private SysDataAuthValueMapperImpl dataAuthValueMapper;

	/**
	 * 根据对象ID删除数据权限
	 *
	 * @param objectId 对象ID
	 * @return 结果
	 */
	@Override
	public List<SysDataAuth> findByObjectId(long objectId) {
		//根据objectId查询数据权限
		List<SysDataAuth> dataAuthList = dataAuthMapper.findByObjectId(objectId);
		if (!CollectionUtils.isEmpty(dataAuthList)) {
			List<Long> authIdList = dataAuthList.stream().map(SysDataAuth::getAuthId).collect(Collectors.toList());
			List<SysDataAuthValue> dataAuthValueList = dataAuthValueMapper.findByAuthIds(authIdList);
			dataAuthList.forEach(o -> {
				o.setDataAuthValueList(dataAuthValueList.stream().filter(i -> i.getAuthId().equals(o.getAuthId())).collect(Collectors.toList()));
			});
		}
		return dataAuthList;
	}
}

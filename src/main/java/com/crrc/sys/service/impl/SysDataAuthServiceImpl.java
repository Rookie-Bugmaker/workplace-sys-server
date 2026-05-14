package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthDBEntity;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.auth.SysDataAuthValueDBEntity;
import com.crrc.sys.entity.profile.SysProfileBDBEntity;
import com.crrc.sys.enums.DataAuthTypeEnum;
import com.crrc.sys.mapper.mapperImpl.SysDataAuthMapperImpl;
import com.crrc.sys.mapper.mapperImpl.SysDataAuthValueMapperImpl;
import com.crrc.sys.mapper.mapperImpl.SysProfileMapperImpl;
import com.crrc.sys.service.SysDataAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	@Autowired
	private SysProfileMapperImpl profileMapper;

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

	@Override
	public Map<String, List<String>> findDataAuthsByObjectId(Long objectId, String authType) {
		// 查询对应对象（用户/角色）的权限
		LambdaQueryWrapper<SysDataAuthDBEntity> dataAuthWrapper = new LambdaQueryWrapper<>();
		dataAuthWrapper.select(SysDataAuthDBEntity::getAuthId, SysDataAuthDBEntity::getProfileId, SysDataAuthDBEntity::getObjectId)
				.eq(SysDataAuthDBEntity::getObjectId, objectId)
				.eq(SysDataAuthDBEntity::getAuthType, authType);
		List<SysDataAuthDBEntity> dataAuthList = dataAuthMapper.list(dataAuthWrapper);
		if(CollectionUtils.isEmpty(dataAuthList)){
			return Collections.emptyMap();
		}
		Map<Long, Long> authIdToProfileIdMap = dataAuthList.stream()
				.collect(Collectors.toMap(SysDataAuthDBEntity::getAuthId, SysDataAuthDBEntity::getProfileId,
						(existing, replacement) -> existing));
		// 查询对象的所有权限值
		LambdaQueryWrapper<SysDataAuthValueDBEntity> dataAuthValueWrapper = new LambdaQueryWrapper<>();
		dataAuthValueWrapper.select(SysDataAuthValueDBEntity::getAuthId, SysDataAuthValueDBEntity::getAuthValue)
				.in(SysDataAuthValueDBEntity::getAuthId, authIdToProfileIdMap.keySet());
		List<SysDataAuthValueDBEntity> dataAuthValueList = dataAuthValueMapper.list(dataAuthValueWrapper);
		if(CollectionUtils.isEmpty(dataAuthValueList)){
			return Collections.emptyMap();
		}
		Map<Long, List<String>> dataAuthIdToValueMap = dataAuthValueList.stream()
				.collect(Collectors.groupingBy(
						SysDataAuthValueDBEntity::getAuthId,
						Collectors.mapping(
								SysDataAuthValueDBEntity::getAuthValue,
								Collectors.toList()
						)
				));
		// 查询每个权限的配置编码
		LambdaQueryWrapper<SysProfileBDBEntity> profileWrapper = new LambdaQueryWrapper<>();
		profileWrapper.select(SysProfileBDBEntity::getProfileCode, SysProfileBDBEntity::getProfileId)
				.in(SysProfileBDBEntity::getProfileId, authIdToProfileIdMap.values());
		List<SysProfileBDBEntity> profileList = profileMapper.list(profileWrapper);
		Map<Long, String> profileIdToDataAuthCodeMap = profileList.stream()
				.collect(Collectors.toMap(SysProfileBDBEntity::getProfileId, SysProfileBDBEntity::getProfileCode,
						(existing, replacement) -> existing));
		// 重新建立映射
		Map<String, List<String>> dataAuthCodeToValueMapInDB = authIdToProfileIdMap.entrySet().stream()
				.collect(Collectors.toMap(
						// Key映射：通过authId找到profileId，再通过profileId找到dataAuthCode
						entry -> profileIdToDataAuthCodeMap.get(entry.getValue()),
						// Value映射：直接从dataAuthIdToValueMap获取对应的value列表
						entry -> dataAuthIdToValueMap.get(entry.getKey()),
						// 合并策略：如果遇到重复key（相同的dataAuthCode），保留已存在的value
						(existing, replacement) -> existing,
						// 指定返回的Map实现类
						HashMap::new
				));
		return dataAuthCodeToValueMapInDB;
	}
}

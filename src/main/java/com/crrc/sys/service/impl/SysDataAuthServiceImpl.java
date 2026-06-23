package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthDBEntity;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.auth.SysDataAuthValueDBEntity;
import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.entity.profile.SysProfileBDBEntity;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.DataAuthTypeEnum;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.mapper.mapperImpl.SysDataAuthMapperImpl;
import com.crrc.sys.mapper.mapperImpl.SysDataAuthValueMapperImpl;
import com.crrc.sys.mapper.mapperImpl.SysProfileMapperImpl;
import com.crrc.sys.service.SysDataAuthService;
import com.crrc.sys.service.SysProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2023-03-23
 */
@Service
public class SysDataAuthServiceImpl implements SysDataAuthService {

	@Resource
	private AuthClient authClient;

	@Resource
	private SysProfileService profileService;


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
        if (CollectionUtils.isEmpty(dataAuthList)) {
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
        if (CollectionUtils.isEmpty(dataAuthValueList)) {
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

    public Map<String, List<String>> getDataAuth() {
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        SysUser sysUser = userResultData.getResult();
        //用户数据权限
        List<SysDataAuth> userDataAuthList = findByObjectId(sysUser.getUserId());
        //角色数据权限
        List<SysDataAuth> roleDataAuthList = findByObjectId(sysUser.getCurrentRoleId());
        //最终数据权限，
        //1.如果用户权限不存在，则取角色权限.
        //2.如果角色权限不存在，则取用户权限.
        //3.如果用户权限和角色权限都存在，则取并集。这里的并集这是并不是对相同的数据权限的值去并集，只是对数据权限的key去并集，例如:用户上设置了org_id=[1,2]，
        //角色上设置了org_id=[1,2,3,4]，最终的数据权限org_id=[1,2]。
        List<SysDataAuth> dataAuthList = new ArrayList<>();
        if (CollectionUtils.isEmpty(userDataAuthList)) {
            dataAuthList.addAll(roleDataAuthList);
        }
        if (CollectionUtils.isEmpty(roleDataAuthList)) {
            dataAuthList.addAll(userDataAuthList);
        }
        if (!CollectionUtils.isEmpty(userDataAuthList) && !CollectionUtils.isEmpty(roleDataAuthList)) {
            dataAuthList.addAll(userDataAuthList);
            for (SysDataAuth roleDataAuth : roleDataAuthList) {
                //查询该数据权限是否在用户中存在
                SysDataAuth sysDataAuth = userDataAuthList.stream().filter(o -> o.getProfileId().equals(roleDataAuth.getProfileId())).findFirst().orElse(null);
                if (ObjectUtils.isEmpty(sysDataAuth)) {
                    //不存在的情况
                    dataAuthList.add(roleDataAuth);
                }
            }
        }
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            List<Long> profileIdList = dataAuthList.stream().map(SysDataAuth::getProfileId).collect(Collectors.toList());
            //根据配置文件ID查询配置文件信息
            List<SysProfile> profileList = profileService.findByIds(profileIdList, LanguageEnum.ZH_CN.getValue());
            //如果配置文件都不存在，则说明数据权限失效了
            if (CollectionUtils.isEmpty(profileList)) {
                return null;
            }
            for (SysDataAuth sysDataAuth : dataAuthList) {
                //查找与数据权限ID相匹配的配置文件
                SysProfile sysProfile = profileList.stream().filter(o -> o.getProfileId().equals(sysDataAuth.getProfileId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(sysProfile)) {
                    String authCode = sysProfile.getProfileCode();
                    //判断该数据权限是否存在别名
                    if (!ObjectUtils.isEmpty(sysProfile.getFieldAlias())) {
                        authCode = (authCode + "," + sysProfile.getFieldAlias());
                    }
                    sysDataAuth.setAuthCode(authCode);
                }
            }
        }
        if (CollectionUtils.isEmpty(dataAuthList)) {
            return null;
        }
        Map<String, List<String>> dataAuthMap = new HashMap<>(dataAuthList.size());
        for (SysDataAuth dataAuth : dataAuthList) {
            //数据权限编码(该编码可能存在以(,)分隔的多个别名)，所以需要把每个别名都提取出来
            String authCode = dataAuth.getAuthCode();
            if (ObjectUtils.isEmpty(authCode)) {
                continue;
            }
            //数据权限值集
            List<String> valueList = dataAuth.getDataAuthValueList().stream().map(SysDataAuthValue::getAuthValue).collect(Collectors.toList());
            for (String code : authCode.split(",")) {
                if (ObjectUtils.isEmpty(code)) {
                    continue;
                }
                dataAuthMap.put(code.trim(), valueList);
            }
        }
        return dataAuthMap;


    }
}

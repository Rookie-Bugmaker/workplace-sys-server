package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.MyBatisUtil;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
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
import com.crrc.sys.model.profile.AuthValueItem;
import com.crrc.sys.model.profile.ProfileBindObjectDTO;
import com.crrc.sys.model.profile.ProfileBindObjectQueryDTO;
import com.crrc.sys.model.profile.ProfileBindObjectRowDTO;
import com.crrc.sys.model.profile.ProfileBindObjectVO;
import com.crrc.sys.service.SysDataAuthService;
import com.crrc.sys.service.SysProfileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 绑定对象分页查询默认每页条数
     */
    private static final int DEFAULT_PAGE_SIZE = 50;

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

    @Resource
    private IdKeyGenerator idKeyGenerator;

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
                return new HashMap<>();
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
            return new HashMap<>();
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

    /**
     * 分页查询配置文件已绑定的角色/用户(配置文件维度)
     * 支持按对象名称/编码模糊查询(objectSearchParam)与权限值过滤(authValue)：
     * 命中的对象仍内嵌其在当前配置文件下的全部权限值集合。
     */
    @Override
    public PaginationGrid<ProfileBindObjectVO> findBindObjects(ProfileBindObjectQueryDTO query) {
        if (query == null || ObjectUtils.isEmpty(query.getProfileId())) {
            throw new ParamValidateException("profileId不能为空");
        }
        String authType = query.getAuthType();
        if (!DataAuthTypeEnum.contains(authType)) {
            throw new ParamValidateException("对象类型参数错误，只能为角色或用户");
        }
        String language = query.getLanguage();
        if (StringUtils.isBlank(language)) {
            language = LanguageEnum.ZH_CN.getValue();
        } else if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        //分页参数默认值
        int page = (query.getPage() != null && query.getPage() > 0) ? query.getPage() : 1;
        int pageSize = (query.getPageSize() != null && query.getPageSize() > 0) ? query.getPageSize() : DEFAULT_PAGE_SIZE;
        Page<ProfileBindObjectRowDTO> pageObj = MyBatisUtil.makePage(page, pageSize);
        //1.按 authType 分派分页查询：DB 侧完成模糊过滤与权限值过滤
        IPage<ProfileBindObjectRowDTO> rowPage;
        if (DataAuthTypeEnum.ROLE.getValue().equals(authType)) {
            rowPage = dataAuthMapper.findBindRolePage(pageObj, query.getProfileId(), language,
                    query.getObjectSearchParam(), query.getAuthValue());
        } else {
            rowPage = dataAuthMapper.findBindUserPage(pageObj, query.getProfileId(),
                    query.getObjectSearchParam(), query.getAuthValue());
        }
        //2.回填当前页对象的权限值集合(按 authId 分组)
        List<ProfileBindObjectRowDTO> rows = rowPage.getRecords();
        List<ProfileBindObjectVO> voList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rows)) {
            List<Long> authIds = rows.stream().map(ProfileBindObjectRowDTO::getAuthId).collect(Collectors.toList());
            List<SysDataAuthValue> valueList = dataAuthValueMapper.findByAuthIds(authIds);
            Map<Long, List<SysDataAuthValue>> authIdToValuesMap = CollectionUtils.isEmpty(valueList)
                    ? new HashMap<>()
                    : valueList.stream().collect(Collectors.groupingBy(SysDataAuthValue::getAuthId));
            //3.组装 VO：name/code 随 authType 取值，authValues 内嵌该对象全部权限值
            for (ProfileBindObjectRowDTO row : rows) {
                ProfileBindObjectVO vo = new ProfileBindObjectVO();
                vo.setObjectId(row.getObjectId());
                vo.setAuthType(authType);
                vo.setAuthValues(toValueItems(authIdToValuesMap.getOrDefault(row.getAuthId(), new ArrayList<>())));
                if (DataAuthTypeEnum.ROLE.getValue().equals(authType)) {
                    //角色：name=roleName、code=roleCode
                    vo.setName(row.getRoleName());
                    vo.setCode(row.getRoleCode());
                } else {
                    //用户：name=用户描述(姓名)优先，否则取用户名；code=用户名
                    if (StringUtils.isNotBlank(row.getFullName())) {
                        vo.setName(row.getFullName());
                    } else {
                        vo.setName(row.getUserName());
                    }
                    vo.setCode(row.getUserName());
                }
                voList.add(vo);
            }
        }
        //4.转换为 PaginationGrid<ProfileBindObjectVO>
        Page<ProfileBindObjectVO> voPage = new Page<>(rowPage.getCurrent(), rowPage.getSize(), rowPage.getTotal());
        voPage.setRecords(voList);
        return PaginationGrid.success(voPage);
    }

    /**
     * 将权限值实体集合转换为接口返回的权限值项集合(携带权限值描述)
     */
    private List<AuthValueItem> toValueItems(List<SysDataAuthValue> valueList) {
        if (CollectionUtils.isEmpty(valueList)) {
            return new ArrayList<>();
        }
        List<AuthValueItem> items = new ArrayList<>(valueList.size());
        for (SysDataAuthValue value : valueList) {
            AuthValueItem item = new AuthValueItem();
            item.setAuthValue(value.getAuthValue());
            item.setAuthValueDesc(value.getAuthValueDesc());
            items.add(item);
        }
        return items;
    }

    /**
     * 保存配置文件绑定的角色/用户(配置文件维度，增量语义：仅处理前端传递的对象)
     * 入参按对象维度传递{objectId, authValues}集合，后端仅对 bindList 中出现的对象做处理，
     * 不删除前端未传递的对象绑定。对每个对象的权限值按 authValue 唯一键做多增少删：
     * 新对象(库中不存在)且带有权限值时插入主表+权限值；已存在对象做权限值增量增删
     * (新值中多出的新增、旧值中不在新值中的删除)，并对已存在值行的描述做更新。
     * 特别地：已存在对象经多增少删后若权限值变为空，则级联删除该对象此配置文件对应的主表记录
     * 及其权限值，避免出现有数据权限但权限值为空的脏数据。新对象未带权限值时不插入。
     * bindList 为空或其中的 BindItem 均无可处理对象时直接返回，不做任何变更。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBindObjects(ProfileBindObjectDTO dto, long createBy) {
        if (dto == null || ObjectUtils.isEmpty(dto.getProfileId())) {
            throw new ParamValidateException("profileId不能为空");
        }
        String authType = dto.getAuthType();
        if (!DataAuthTypeEnum.contains(authType)) {
            throw new ParamValidateException("authType参数错误");
        }
        //校验配置文件存在
        SysProfile sysProfile = profileService.findById(dto.getProfileId(), LanguageEnum.ZH_CN.getValue());
        if (ObjectUtils.isEmpty(sysProfile)) {
            throw new BizException("配置文件不存在!");
        }
        long profileId = dto.getProfileId();
        //1.解析前端目标状态：按 objectId 聚合(合并重复 objectId 的权限值、过滤空白 authValue)
        //   以 authValue 为唯一键去重：同一 objectId 在多个 BindItem 中重复出现、或同一 authValue 在
        //   authValues 中重复出现时合并为一条；authValueDesc 优先保留首个非空白描述，避免因首次出现空白
        //   描述而丢弃后续有效描述。
        Map<Long, Map<String, String>> newValuesByObjectId = new LinkedHashMap<>();
        List<ProfileBindObjectDTO.BindItem> bindList = dto.getBindList();
        if (bindList != null) {
            for (ProfileBindObjectDTO.BindItem item : bindList) {
                if (item == null || ObjectUtils.isEmpty(item.getObjectId())) {
                    continue;
                }
                Map<String, String> values = newValuesByObjectId.computeIfAbsent(item.getObjectId(), k -> new LinkedHashMap<>());
                if (item.getAuthValues() != null) {
                    for (AuthValueItem valueItem : item.getAuthValues()) {
                        if (valueItem == null || StringUtils.isBlank(valueItem.getAuthValue())) {
                            continue;
                        }
                        String authValue = valueItem.getAuthValue().trim();
                        String desc = valueItem.getAuthValueDesc();
                        //已存在该 authValue 时去重，但若既有描述为空而本次带非空描述则补齐(合并)
                        String existingDesc = values.get(authValue);
                        if (existingDesc == null || (StringUtils.isBlank(existingDesc) && StringUtils.isNotBlank(desc))) {
                            values.put(authValue, desc);
                        }
                    }
                }
            }
        }
        Set<Long> newObjectIds = newValuesByObjectId.keySet();
        //前端未传递任何有效对象时不做任何变更(不查询、不删除、不新增)，直接返回
        if (newObjectIds.isEmpty()) {
            return;
        }
        //2.读取 DB 当前状态
        List<SysDataAuth> oldList = dataAuthMapper.findByProfileIdAndAuthType(profileId, authType);
        Map<Long, Long> objectIdToAuthIdMap = new HashMap<>();
        for (SysDataAuth dataAuth : oldList) {
            objectIdToAuthIdMap.put(dataAuth.getObjectId(), dataAuth.getAuthId());
        }
        Set<Long> oldObjectIds = objectIdToAuthIdMap.keySet();
        //3.已存在对象(保留对象)的权限值按 authId 分组，记录每个权限值当前的描述，用于增量增删与描述更新判断
        Set<Long> retainedObjectIds = newObjectIds.stream().filter(oldObjectIds::contains).collect(Collectors.toSet());
        Map<Long, Map<String, String>> oldValuesByAuthIdMap = new HashMap<>();
        if (!retainedObjectIds.isEmpty()) {
            List<Long> retainedAuthIds = retainedObjectIds.stream()
                    .map(objectIdToAuthIdMap::get)
                    .collect(Collectors.toList());
            List<SysDataAuthValue> oldValueList = dataAuthValueMapper.findByAuthIds(retainedAuthIds);
            if (!CollectionUtils.isEmpty(oldValueList)) {
                oldValuesByAuthIdMap = oldValueList.stream().collect(Collectors.groupingBy(
                        SysDataAuthValue::getAuthId,
                        Collectors.toMap(SysDataAuthValue::getAuthValue,
                                v -> v.getAuthValueDesc() == null ? "" : v.getAuthValueDesc(),
                                (existing, replacement) -> existing)
                ));
            }
        }
        java.util.Date now = java.util.Calendar.getInstance().getTime();
        //4.新对象(库中不存在)：仅当带有权限值时插入主表 + 权限值；未带权限值的不插入(避免产生空权限值的主表记录)
        List<Long> toAdd = newObjectIds.stream()
                .filter(id -> !oldObjectIds.contains(id) && !newValuesByObjectId.get(id).isEmpty())
                .collect(Collectors.toList());
        if (!toAdd.isEmpty()) {
            List<SysDataAuth> addAuthList = new ArrayList<>(toAdd.size());
            List<SysDataAuthValue> addValueList = new ArrayList<>();
            for (Long objectId : toAdd) {
                long authId = idKeyGenerator.getId();
                SysDataAuth dataAuth = new SysDataAuth();
                dataAuth.setAuthId(authId);
                dataAuth.setAuthType(authType);
                dataAuth.setObjectId(objectId);
                dataAuth.setProfileId(profileId);
                dataAuth.setCreateBy(createBy);
                dataAuth.setCreateDate(now);
                addAuthList.add(dataAuth);
                Map<String, String> valueDescMap = newValuesByObjectId.get(objectId);
                for (Map.Entry<String, String> entry : valueDescMap.entrySet()) {
                    SysDataAuthValue value = new SysDataAuthValue();
                    value.setAuthId(authId);
                    value.setAuthValue(entry.getKey());
                    value.setAuthValueDesc(entry.getValue());
                    value.setCreateBy(createBy);
                    value.setCreateDate(now);
                    addValueList.add(value);
                }
            }
            dataAuthMapper.insertList(addAuthList);
            if (!addValueList.isEmpty()) {
                dataAuthValueMapper.insertList(addValueList);
            }
        }
        //5.已存在对象：权限值多增少删 + 描述更新；若前端传空权限值则级联删除该对象此配置文件的主表记录及权限值，
        //   避免出现"有数据权限但权限值为空"的脏数据
        for (Long objectId : retainedObjectIds) {
            long authId = objectIdToAuthIdMap.get(objectId);
            Map<String, String> oldVals = oldValuesByAuthIdMap.getOrDefault(authId, Collections.emptyMap());
            Map<String, String> newVals = newValuesByObjectId.get(objectId);
            //前端传空权限值：级联删除主表 + 全部权限值
            if (newVals.isEmpty()) {
                List<Long> deletedAuthIds = dataAuthMapper.deleteByProfileIdAndAuthType(profileId, authType, Collections.singletonList(objectId));
                dataAuthValueMapper.deleteByAuthIds(deletedAuthIds);
                continue;
            }
            //待删除 = 旧值中不在新值中的
            List<String> valsToDelete = oldVals.keySet().stream().filter(v -> !newVals.containsKey(v)).collect(Collectors.toList());
            if (!valsToDelete.isEmpty()) {
                dataAuthValueMapper.deleteByAuthIdAndValues(authId, valsToDelete);
            }
            //待新增 = 新值中不在旧值中的
            List<SysDataAuthValue> valsToAdd = newVals.entrySet().stream()
                    .filter(e -> !oldVals.containsKey(e.getKey()))
                    .map(e -> {
                        SysDataAuthValue value = new SysDataAuthValue();
                        value.setAuthId(authId);
                        value.setAuthValue(e.getKey());
                        value.setAuthValueDesc(e.getValue());
                        value.setCreateBy(createBy);
                        value.setCreateDate(now);
                        return value;
                    })
                    .collect(Collectors.toList());
            if (!valsToAdd.isEmpty()) {
                dataAuthValueMapper.insertList(valsToAdd);
            }
            //描述更新 = 新旧都存在的值行，前端传了 desc 且与库中不同时更新(空 desc 不覆盖原值)
            for (Map.Entry<String, String> entry : newVals.entrySet()) {
                String authValue = entry.getKey();
                String newDesc = entry.getValue();
                if (StringUtils.isBlank(newDesc) || !oldVals.containsKey(authValue)) {
                    continue;
                }
                String oldDesc = oldVals.get(authValue);
                if (!newDesc.equals(oldDesc)) {
                    dataAuthValueMapper.updateDescByAuthIdAndValue(authId, authValue, newDesc);
                }
            }
        }
    }
}

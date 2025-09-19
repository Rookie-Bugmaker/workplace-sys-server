package com.crrc.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResponseResult;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.entity.profile.SysProfileFindByPageParam;
import com.crrc.sys.entity.profile.SysProfileValuePageSearch;
import com.crrc.sys.entity.profile.ValueSet;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.enums.ProfileSourceTypeEnum;
import com.crrc.sys.service.SysProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-12-19
 */
@RestController
@RequestMapping("/profile/")
public class SysProfileController {

    private final static Logger LOGGER = LoggerFactory.getLogger(SysProfileController.class);
    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysProfileService profileService;
    @Resource
    private RestTemplate restTemplate;

    /**
     * 配置文件分页查询
     *
     * @param param 参数
     * @return 结果
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysProfile> findByPage(SysProfileFindByPageParam param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(param.getPage()) || param.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(param.getPageSize()) || param.getPageSize() < 0) {
            throw new ParamValidateException("pageSize不能为空且必须>=0");
        }
        if (ObjectUtils.isEmpty(param.getLanguage())) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(param.getLanguage())) {
            throw new ParamValidateException("language参数错误");
        }
        return profileService.findByPage(param);
    }

    /**
     * 配置文件新增
     *
     * @param profile 配置信息
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(SysProfile profile) {
        verification(OperationTypeEnum.ADD.getValue(), profile);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        profile.setProfileId(idKeyGenerator.getId());
        //设置创建人
        profile.setCreateBy(currentUser.getUserId());
        //设置创建时间
        profile.setCreateDate(Calendar.getInstance().getTime());
        int result = profileService.add(profile);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 配置文件修改
     *
     * @param profile 配置信息
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysProfile profile) {
        verification(OperationTypeEnum.UPDATE.getValue(), profile);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //修改人
        profile.setLastUpdateBy(currentUser.getUserId());
        //修改时间
        profile.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改的登录信息ID
        profile.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        int result = profileService.update(profile);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 根据配置ID查询配置信息
     *
     * @param profileId 配置ID
     * @param language  语言
     * @return 结果
     */
    @PostMapping("findById")
    public SysProfile findById(Long profileId, String language) {
        if (ObjectUtils.isEmpty(profileId)) {
            throw new ParamValidateException("profileId不能为空!");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        return profileService.findById(profileId, language);
    }

    /**
     * 删除
     *
     * @param profileIds 配置ID,多个以逗号","分隔
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String profileIds) {
        if (ObjectUtils.isEmpty(profileIds)) {
            throw new ParamValidateException("roleIds不能为空!");
        }
        List<Long> ids = Arrays.stream(profileIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        int result = profileService.delete(ids);
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 根据配置文件编码查询值集(分页查询)
     *
     * @param search 参数
     * @return 值集
     */
    @PostMapping("getValuePageBySql")
    public PaginationGrid<ValueSet> getValuePageBySql(SysProfileValuePageSearch search) {
        if (ObjectUtils.isEmpty(search)) {
            throw new ParamValidateException("参数错误!");
        }
        if (ObjectUtils.isEmpty(search.getPage()) || search.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(search.getPageSize()) || search.getPageSize() < 0) {
            throw new ParamValidateException("pageSize不能为空且必须>=0");
        }
        if (ObjectUtils.isEmpty(search.getProfileCode())) {
            throw new ParamValidateException("profileCode不能为空");
        }
        //根据配置编码查询配置文件信息
        SysProfile sysProfile = profileService.findByCode(search.getProfileCode());
        if (ObjectUtils.isEmpty(sysProfile)) {
            throw new BizException("配置文件不存在！");
        }
        //值列
        search.setProfileValueColumn(sysProfile.getProfileValueColumn());
        //编码列
        search.setProfileCodeColumn(sysProfile.getProfileCodeColumn());
        //描述列
        search.setProfileDescColumn(sysProfile.getProfileDescColumn());
        //父级列
        search.setProfileParentColumn(sysProfile.getProfileParentColumn());
        //值来源
        String sql = sysProfile.getProfileSource();
        if (ObjectUtils.isEmpty(sql)) {
            throw new BizException("数据错误，请检查数据!");
        }
        //动态参数处理
        sql = dynamicParamHandle(sql, search.getParamMapStr());
        search.setSql(sql);
        PaginationGrid<ValueSet> paginationGrid = new PaginationGrid<>(search.getPage(), search.getPageSize());
        //值来源类型： sql:SQL、fixed:固定值、api：接口
        switch (sysProfile.getProfileSourceType()) {
            case "sql":
                //根据SQL查询值集(分页查询)
                paginationGrid = profileService.findValuePageBySql(search);
            case "api":
                //远程请求api接口
                //请求案例：http://sys-server/user/test/findUserPage?usernames=18875012385&page=1&pageSize=10
                //响应案例:{total:100,rows:[{值列:"1",编码列:"qqq",描述列:"描述",父级列:""}]}
                String url = search.getSql();
                if (url.contains("?")) {
                    url += "&page=1&pageSize=10";
                } else {
                    url += "?page=1&pageSize=10";
                }
                url = url.replace("http://workplace-termdual-contract", "http://127.0.0.1:5568");
                LOGGER.debug("---> GET {}", url);
                ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
                LOGGER.debug("<--- 响应 {}", JSON.toJSONString(exchange));
                String responseResultStr = exchange.getBody();
                if (!ObjectUtils.isEmpty(responseResultStr)) {
                    ResponseResult<String> responseResult = JSON.parseObject(responseResultStr, new TypeReference<ResponseResult<String>>() {
                    });
                    if (!responseResult.getSuccess()) {
                        throw new BizException(ObjectUtils.isEmpty(responseResult.getMsg()) ? "请求失败:" + url : responseResult.getMsg());
                    }
                    paginationGrid = bodyHandler(search, responseResult.getData());
                }
                break;
            default:
                throw new BizException("该接口不查询[值来源类型=固定值]的配置文件!");
        }
        return paginationGrid;
    }

    /**
     * 处理响应数据
     *
     * @param body 响应案例:{total:100,rows:[{值列:"1",编码列:"qqq",描述列:"描述",父级列:""}]}
     * @return
     */
    private PaginationGrid<ValueSet> bodyHandler(SysProfileValuePageSearch search, String body) {
        PaginationGrid<ValueSet> paginationGrid = new PaginationGrid<>(search.getPage(), search.getPageSize());
        Map<String, String> resultMap = JSON.parseObject(body, new TypeReference<Map<String, String>>() {
        });
        String totalStr = resultMap.get("total");
        if (ObjectUtils.isEmpty(totalStr)) {
            return paginationGrid;
        }
        paginationGrid.setTotal(Long.parseLong(totalStr));
        if (paginationGrid.getTotal() <= 0) {
            return paginationGrid;
        }
        String rowsStr = resultMap.get("rows");
        List<Map<String, String>> rows = JSON.parseObject(rowsStr, new TypeReference<List<Map<String, String>>>() {
        });
        if (CollectionUtils.isEmpty(rows)) {
            return paginationGrid;
        }
        List<ValueSet> valueSetList = new ArrayList<>();
        for (Map<String, String> row : rows) {
            //值列
            String value = row.get(search.getProfileValueColumn());
            String code = row.get(search.getProfileCodeColumn());
            String desc = row.get(search.getProfileDescColumn());
            String parent = row.get(search.getProfileParentColumn());
            ValueSet valueSet = new ValueSet();
            valueSet.setId(value);
            valueSet.setCode(code);
            valueSet.setDesc(desc);
            valueSet.setParentId(parent);
            valueSetList.add(valueSet);
        }
        paginationGrid.setRows(valueSetList);
        return paginationGrid;
    }

    /**
     * 根据配置文件编码查询值集(不分页)
     *
     * @param profileCode 配置编码
     * @param paramMapStr 动态参数
     * @param fuzzyQuery  模糊查询
     * @return 值集
     */
    @PostMapping(value = "getValueListBySql")
    public List<ValueSet> getValueListBySql(String profileCode, String paramMapStr, String fuzzyQuery) {
        if (ObjectUtils.isEmpty(profileCode)) {
            throw new ParamValidateException("profileCode不能为空!");
        }
        SysProfileValuePageSearch search = new SysProfileValuePageSearch();
        search.setPage(1);
        search.setPageSize(0);
        search.setProfileCode(profileCode);
        search.setFuzzyQuery(fuzzyQuery);
        search.setParamMapStr(paramMapStr);
        PaginationGrid<ValueSet> page = getValuePageBySql(search);
        return page.getRows();
    }

    /**
     * 根据配置文件ID查询值集(不分页)
     *
     * @param profileId   配置ID
     * @param paramMapStr 动态参数
     * @param fuzzyQuery  模糊查询
     * @return 值集
     */
    @PostMapping(value = "getValueSetListAccordingToProfileId")
    public List<ValueSet> getValueSetListAccordingToProfileId(Long profileId, String paramMapStr, String fuzzyQuery) {
        if (ObjectUtils.isEmpty(profileId)) {
            throw new ParamValidateException("profileId不能为空!");
        }
        SysProfile sysProfile = profileService.findById(profileId, LanguageEnum.ZH_CN.getValue());
        if (ObjectUtils.isEmpty(sysProfile)) {
            throw new BizException("配置文件不存在!");
        }
        SysProfileValuePageSearch search = new SysProfileValuePageSearch();
        search.setPage(1);
        search.setPageSize(0);
        search.setProfileCode(sysProfile.getProfileCode());
        search.setFuzzyQuery(fuzzyQuery);
        search.setParamMapStr(paramMapStr);
        PaginationGrid<ValueSet> page = getValuePageBySql(search);
        return page.getRows();
    }

    /**
     * 根据配置文件编码查询固定值
     *
     * @param profileCode 配置编码
     * @return 结果
     */
    @PostMapping(value = "getFixedValue", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getFixedValue(String profileCode) {
        if (ObjectUtils.isEmpty(profileCode)) {
            throw new ParamValidateException("profileCode不能为空");
        }
        //根据配置编码查询配置文件信息
        SysProfile sysProfile = profileService.findByCode(profileCode);
        //值来源类型： sql:SQL、fixed:固定值
        if (!ProfileSourceTypeEnum.FIXED.getValue().equals(sysProfile.getProfileSourceType())) {
            throw new BizException("该接口只查询[值来源类型=fixed]的配置文件!");
        }
        return sysProfile.getProfileSource();
    }

    /**
     * SQL上的动态参数处理
     *
     * @param sql         SQL
     * @param paramMapStr 动态参数值列表
     * @return 已进行参数替换的SQL
     */
    private String dynamicParamHandle(String sql, String paramMapStr) {
        //判断该SQL是否存在动态参数
        String regexp = "\\$\\{[a-zA-Z_.]+}";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(sql);
        Set<String> paramSet = new HashSet<>();
        while (matcher.find()) {
            paramSet.add(matcher.group(0));
        }
        if (!CollectionUtils.isEmpty(paramSet)) {
            //说明该SQL中存在动态参数，则需要手动替换动态参数的值
            //先判断动态参数有没有固定参数：${user.userId}、${user.roleId}、${user.companyId}、${user.deptId}
            if (paramSet.contains("${user.userId}") || paramSet.contains("${user.roleId}") || paramSet.contains("${user.companyId}") || paramSet.contains("${user.deptId}")) {
                //获取当前用户
                ResultData<SysUser> userResultData = authClient.currentUser();
                if (!userResultData.getSuccess()) {
                    throw new BizException(userResultData.getCode(), userResultData.getMessage());
                }
                //当前用户
                SysUser currentUser = userResultData.getResult();
                sql = sql.replaceAll("\\$\\{user.userId}", String.valueOf(currentUser.getUserId()));
                sql = sql.replaceAll("\\$\\{user.roleId}", String.valueOf(currentUser.getCurrentRoleId()));
                sql = sql.replaceAll("\\$\\{user.companyId}", String.valueOf(currentUser.getCurrentCompanyId()));
                sql = sql.replaceAll("\\$\\{user.deptId}", String.valueOf(currentUser.getCurrentDeptId()));
                paramSet.remove("${user.userId}");
                paramSet.remove("${user.roleId}");
                paramSet.remove("${user.companyId}");
                paramSet.remove("${user.deptId}");
                //判断是否还有需要处理的参数，如果没有就直接返回
                if (CollectionUtils.isEmpty(paramSet)) {
                    return sql;
                }
            }
            if (ObjectUtils.isEmpty(paramMapStr)) {
                throw new ParamValidateException("动态参数值必填!");
            }
            Map<String, String> paramMap = JSONObject.parseObject(paramMapStr, new TypeReference<Map<String, String>>() {
            });
            if (ObjectUtils.isEmpty(paramMap)) {
                throw new ParamValidateException("动态参数值不能为空!");
            }
            Map<String, String> newParamMap = new HashMap<>(paramMap.size());
            //将map中的key值添加${}
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                newParamMap.put("${" + entry.getKey() + "}", entry.getValue());
            }
            Iterator<String> it = paramSet.iterator();
            while (it.hasNext()) {
                String key = it.next();
                sql = sql.replaceAll(key.replace("${", "\\$\\{"), newParamMap.get(key));
                it.remove();
            }
            if (!CollectionUtils.isEmpty(paramSet)) {
                throw new ParamValidateException(JSON.toJSONString(paramSet) + "缺少参数值!");
            }
        }
        return sql;
    }


    /**
     * 根据配置分类查询配置信息
     *
     * @param profileType 配置ID
     * @param language    语言
     * @return 结果
     */
    @PostMapping("findByType")
    public List<SysProfile> findByProfileType(String profileType, String language) {
        if (ObjectUtils.isEmpty(profileType)) {
            throw new ParamValidateException("profileType不能为空!");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        return profileService.findByType(profileType, language);
    }

    /**
     * 根据配置编码查询配置文件信息
     *
     * @param profileCode 配置编码
     * @return 结果
     */
    @PostMapping("findByCode")
    public SysProfile findByCode(String profileCode) {
        return profileService.findByCode(profileCode);
    }

    /**
     * 根据配置文件ID查询配文件信息(批量)
     *
     * @param profileIdList 配置文件ID集合
     * @param language      语言
     * @return 结果
     */
    @PostMapping("findByIds")
    public List<SysProfile> findByIds(@RequestBody List<Long> profileIdList, String language) {
        return profileService.findByIds(profileIdList, language);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param profile   配置信息
     */
    private void verification(String operation, SysProfile profile) {
        if (ObjectUtils.isEmpty(profile)) {
            throw new ParamValidateException("参数错误");
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(profile.getProfileId())) {
                throw new ParamValidateException("profileId不能为空!");
            }
            if (ObjectUtils.isEmpty(profile.getLanguage())) {
                throw new ParamValidateException("language不能为空");
            }
            if (!LanguageEnum.contains(profile.getLanguage())) {
                throw new ParamValidateException("language参数错误");
            }
        }
        if (ObjectUtils.isEmpty(profile.getProfileName())) {
            throw new ParamValidateException("配置名称不能为空!");
        }
        if (profile.getProfileName().length() > 50) {
            throw new ParamValidateException("配置名称超长(最大30)");
        }
        if (ObjectUtils.isEmpty(profile.getProfileCode())) {
            throw new ParamValidateException("配置编码不能为空!");
        }
        if (profile.getProfileCode().length() > 50) {
            throw new ParamValidateException("配置编码超长(最大50)");
        }
        //根据配置编码查询配置文件信息
        SysProfile sysProfile = profileService.findByCode(profile.getProfileCode());
        if (!ObjectUtils.isEmpty(sysProfile)) {
            if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                throw new ParamValidateException("配置编码已重复!");
            }
            if (OperationTypeEnum.UPDATE.getValue().equals(operation) && !sysProfile.getProfileId().equals(profile.getProfileId())) {
                throw new ParamValidateException("配置编码已重复!");
            }
        }
        if (ObjectUtils.isEmpty(profile.getProfileSourceType())) {
            throw new ParamValidateException("值来源类型不能为空!");
        }
        if (!ProfileSourceTypeEnum.contains(profile.getProfileSourceType())) {
            throw new ParamValidateException("值来源类型错误!");
        }
        if (ObjectUtils.isEmpty(profile.getProfileSource())) {
            throw new ParamValidateException("值来源不能为空!");
        }
        if (profile.getProfileSource().length() > 2000) {
            throw new ParamValidateException("值来源超长(最大2000)");
        }
        if (ProfileSourceTypeEnum.SQL.getValue().equals(profile.getProfileSourceType()) || ProfileSourceTypeEnum.API.getValue().equals(profile.getProfileSourceType())) {
            if (ObjectUtils.isEmpty(profile.getProfileValueColumn())) {
                throw new ParamValidateException("值列不能为空!");
            }
            if (profile.getProfileValueColumn().length() > 30) {
                throw new ParamValidateException("值列超长(最大30)");
            }
            if (ObjectUtils.isEmpty(profile.getProfileCodeColumn()) && ObjectUtils.isEmpty(profile.getProfileDescColumn())) {
                throw new ParamValidateException("编码列或描述列不能为空!");
            }
            if (!ObjectUtils.isEmpty(profile.getProfileCodeColumn()) && profile.getProfileCodeColumn().length() > 40) {
                throw new ParamValidateException("编码列超长(最大30)");
            }
            if (!ObjectUtils.isEmpty(profile.getProfileDescColumn()) && profile.getProfileDescColumn().length() > 40) {
                throw new ParamValidateException("描述列超长(最大30)");
            }
            if (!ObjectUtils.isEmpty(profile.getProfileParentColumn()) && profile.getProfileParentColumn().length() > 40) {
                throw new ParamValidateException("父级列超长(最大30)");
            }
        }
        if (ObjectUtils.isEmpty(profile.getProfileType())) {
            throw new ParamValidateException("配置分类不能为空!");
        }
        if (!ObjectUtils.isEmpty(profile.getFieldAlias()) && profile.getFieldAlias().length() > 90) {
            throw new ParamValidateException("别名超长(最大90)!");
        }
    }
}

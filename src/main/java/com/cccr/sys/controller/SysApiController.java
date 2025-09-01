package com.cccr.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cccr.sys.client.AuthClient;
import com.cccr.sys.common.*;
import com.cccr.sys.enums.OperationTypeEnum;
import com.cccr.sys.enums.RequestMethodEnum;
import com.cccr.sys.annotation.AutoIdempotent;
import com.cccr.sys.config.exception.BizException;
import com.cccr.sys.config.exception.ParamValidateException;
import com.cccr.sys.entity.api.SysApi;
import com.cccr.sys.entity.api.SysApiFindByPageParam;
import com.cccr.sys.entity.api.SysApiVO;
import com.cccr.sys.entity.user.SysUser;
import com.cccr.sys.service.SysApiService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * API管理-控制器
 *
 * @author 谭永强
 * @date 2022-09-14
 */
@RestController
@RequestMapping("api")
public class SysApiController {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysApiService apiService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * API信息分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysApi> findByPage(SysApiFindByPageParam param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(param.getPage()) || param.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(param.getPageSize()) || param.getPageSize() < 0) {
            throw new ParamValidateException("pageSize不能为空且必须>=0");
        }
        return apiService.findByPage(param);
    }

    /**
     * API新增
     *
     * @param api API信息
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(SysApi api) {
        verification(OperationTypeEnum.ADD.getValue(), api);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        api.setId(idKeyGenerator.getId());
        //设置创建人
        api.setCreateBy(currentUser.getUserId());
        //设置创建时间
        api.setCreateDate(Calendar.getInstance().getTime());
        int result = apiService.add(api);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * API修改
     *
     * @param api API信息
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysApi api) {
        verification(OperationTypeEnum.UPDATE.getValue(), api);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //修改人
        api.setLastUpdateBy(currentUser.getUserId());
        //修改时间
        api.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改的登录信息ID
        api.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        int result = apiService.update(api);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * API删除
     *
     * @param ids API信息ID，多个以逗号","拼接
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String ids) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new ParamValidateException("apiIds不能为空");
        }
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        int result = apiService.delete(idList);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 根据ID查询API信息
     *
     * @param id API信息ID
     * @return API信息
     */
    @PostMapping("findById")
    public SysApi findById(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ParamValidateException("id不能为空");
        }
        return apiService.findById(id);
    }

    /**
     * 根据ID查询API信息(批量)
     *
     * @param ids API信息ID
     * @return 结果
     */
    @PostMapping("findByIds")
    public List<SysApi> findByIds(@RequestBody List<Long> ids) {
        return apiService.findByIds(ids);
    }

    /**
     * 根据请求类型+请求地址查询API信息
     *
     * @param apiMethod 请求类型
     * @param apiUri    请求地址
     * @return API信息
     */
    @PostMapping("findByMethodAndUri")
    public SysApi findByMethodAndUri(String apiMethod, String apiUri) {
        return apiService.findByMethodAndUri(apiMethod, apiUri);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param api       API信息
     */
    private void verification(String operation, SysApi api) {
        if (ObjectUtils.isEmpty(api)) {
            throw new ParamValidateException("参数错误");
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation) && ObjectUtils.isEmpty(api.getId())) {
            throw new ParamValidateException("ID不能为空");
        }
        if (ObjectUtils.isEmpty(api.getApiName())) {
            throw new ParamValidateException("API名称不能为空!");
        }
        if (api.getApiName().length() > 60) {
            throw new ParamValidateException("API名称超长(最大60)!");
        }
        if (ObjectUtils.isEmpty(api.getApiUri())) {
            throw new ParamValidateException("API地址不能为空!");
        }
        if (api.getApiUri().length() > 100) {
            throw new ParamValidateException("API地址超长(最大100)!");
        }
        if (ObjectUtils.isEmpty(api.getApiMethod())) {
            throw new ParamValidateException("API请求方式不能为空!");
        }
        if (!RequestMethodEnum.contains(api.getApiMethod())) {
            throw new ParamValidateException("API请求方式错误!");
        }
        //根据[请求方式+请求地址]查询API信息
        SysApi sysApi = apiService.findByMethodAndUri(api.getApiMethod(), api.getApiUri());
        if (!ObjectUtils.isEmpty(sysApi)) {
            if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                throw new ParamValidateException("[请求方式+请求地址]已存在!");
            } else {
                //根据API ID查询角色信息
                SysApi sysApiId = apiService.findById(api.getId());
                if (!ObjectUtils.isEmpty(sysApiId) && !sysApi.getId().equals(sysApiId.getId())) {
                    throw new ParamValidateException("[请求方式+请求地址]已存在!");
                }
            }
        }
        if (!ObjectUtils.isEmpty(api.getApiDesc()) && api.getApiDesc().length() > 160) {
            throw new ParamValidateException("API描述超长(最大160)!");
        }
    }

    /**
     * 校验当前用户是否拥有该API权限
     *
     * @param method 请求方式
     * @param uri    URI
     * @return true:拥有权限，false:没有权限
     */
    @PostMapping("checkApiAuth")
    public boolean checkApiAuth(String method, String uri) {
        if (ObjectUtils.isEmpty(method)) {
            throw new ParamValidateException("method不能为空!");
        }
        if (ObjectUtils.isEmpty(uri)) {
            throw new ParamValidateException("uri不能为空!");
        }
        ResultData<SysUser> userResultData = authClient.currentUser();
        Map<String, List<String>> methodUriMap = new HashMap<>();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        SysUser currentUser = userResultData.getResult();
        String key = "api_auth:" + currentUser.getCurrentRoleId();
        //此处从缓存中获取的API权限集合来源于首页菜单查询
        Object value = redisUtils.get(key);
        if (ObjectUtils.isEmpty(value)) {
            return false;
        }
        methodUriMap = (Map<String, List<String>>) value;
        List<String> uriList = methodUriMap.get(method.toLowerCase());
        if (CollectionUtils.isEmpty(uriList)) {
            return false;
        }
        return uriList.stream().map(o -> antPathMatcher.match(o, uri)).filter(o -> o).findFirst().orElse(false);
    }

    /**
     * 导出
     *
     * @param response  响应
     * @param apiIdList ID集合
     * @throws Exception 异常
     */
    @PostMapping("export")
    public void export(HttpServletResponse response, @RequestBody List<Long> apiIdList) throws Exception {
        if (CollectionUtils.isEmpty(apiIdList)) {
            throw new ParamValidateException("请选择需要导出的数据!");
        }
        List<SysApi> apiList = apiService.findByIds(apiIdList);
        if (CollectionUtils.isEmpty(apiList)) {
            throw new BizException("没有需要导出的数据!");
        }
        List<SysApiVO> exportList = apiList.stream().map(o -> {
            SysApiVO sysApiVO = new SysApiVO();
            sysApiVO.setId(o.getId());
            sysApiVO.setApiName(o.getApiName());
            sysApiVO.setApiUri(o.getApiUri());
            sysApiVO.setApiMethod(o.getApiMethod());
            sysApiVO.setApiDesc(o.getApiDesc());
            return sysApiVO;
        }).collect(Collectors.toList());
        byte[] bytes = JSON.toJSONString(exportList).getBytes(StandardCharsets.UTF_8);
        //构建下载文件名称
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "API列表导出-" + sdf.format(Calendar.getInstance().getTime()) + ".json";
        //文件下载
        CustomFileUtils.download(response, fileName, bytes);
    }

    /**
     * 导入
     * 1.首先判断methos+uri是否存在，如果存在则不允许导入，如果不存在则进行导入
     * 2.判断ID是否存在，如果存在则做修改操作。
     *
     * @param multipartRequest 文件
     */
    @AutoIdempotent(seconds = 30)
    @PostMapping("import")
    public void importTable(MultipartHttpServletRequest multipartRequest) {
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        MultipartFile multipartFile = null;
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
            multipartFile = entry.getValue();
            if (!ObjectUtils.isEmpty(multipartFile)) {
                break;
            }
        }
        if (ObjectUtils.isEmpty(multipartFile)) {
            throw new BizException("请上传需要导入的数据!");
        }
        String body = null;
        try {
            byte[] bytes = multipartFile.getBytes();
            body = new String(bytes, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //导入的数据集合
        List<SysApi> apiListImport = JSONObject.parseObject(body, new TypeReference<List<SysApi>>() {
        });
        if (CollectionUtils.isEmpty(apiListImport)) {
            throw new BizException("没有需要导入的数据!");
        }
        //查询所有数据，用以校验导入的数据吃饭存在相同的URI规则
        List<SysApi> apiAll = apiService.findAll();
        if (!CollectionUtils.isEmpty(apiAll)) {
            Iterator<SysApi> iterator = apiListImport.iterator();
            while (iterator.hasNext()) {
                SysApi api = iterator.next();
                SysApi sysApi = apiAll.stream()
                        //保证类型一致
                        .filter(o -> o.getApiMethod().equals(api.getApiMethod()))
                        //判断URI是否一致
                        .filter(o -> o.getApiUri().equals(api.getApiUri()))
                        .findFirst()
                        .orElse(null);
                if (!ObjectUtils.isEmpty(sysApi)) {
                    iterator.remove();
                }
            }
        }
        if (!CollectionUtils.isEmpty(apiListImport)) {
            //获取当前用户
            ResultData<SysUser> resultData = authClient.currentUser();
            if (!resultData.getSuccess()) {
                throw new BizException(resultData.getMessage());
            }
            SysUser sysUser = resultData.getResult();
            for (SysApi sysApi : apiListImport) {
                sysApi.setCreateBy(sysUser.getUserId());
                sysApi.setCreateDate(Calendar.getInstance().getTime());
                sysApi.setLastUpdateBy(sysUser.getUserId());
                sysApi.setLastUpdateDate(sysApi.getCreateDate());
                sysApi.setLastUpdateLogin(sysUser.getLastUpdateLogin());
            }
            int result = apiService.importList(apiListImport);
            if (result <= 0) {
                throw new BizException("导入失败!");
            }
        }
    }

    /**
     * 根据URI查询API信息(批量)
     *
     * @param uriList 参数
     * @return 结果
     */
    @PostMapping("findByUris")
    public List<SysApi> findByUris(@RequestBody List<String> uriList) {
        if (CollectionUtils.isEmpty(uriList)) {
            throw new ParamValidateException("参数错误!");
        }
        return apiService.findByUris(uriList);
    }
}
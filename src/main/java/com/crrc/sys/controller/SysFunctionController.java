package com.crrc.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.CustomFileUtils;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.api.SysApi;
import com.crrc.sys.entity.function.*;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.FunctionTypeEnum;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.enums.OpenMethodEnum;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.service.ProjectService;
import com.crrc.sys.service.SysApiService;
import com.crrc.sys.service.SysFunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-09-15
 */
@RestController
@RequestMapping("function")
public class SysFunctionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private SysFunctionService functionService;
    @Resource
    private SysApiService apiService;
    @Resource
    private ProjectService projectService;

    /**
     * 初始化所有低代码功能按钮权限
     */
    @PostMapping("init")
    public void init() {
        functionService.init();
    }

    /**
     * 初始化所有工作流功能按钮权限
     */
    @PostMapping("initWorkflow")
    public void initWorkflow() {
        functionService.initWorkflow();
    }

    /**
     * 功能分页查询
     *
     * @param param 查询条件
     * @return 分页数据
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysFunction> findByPage(SysFunctionFindByPageParam param) {
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
        return functionService.findByPage(param);
    }

    /**
     * 新增
     *
     * @param function 功能信息
     * @return 功能ID
     */
    @AutoIdempotent
    @PostMapping("add")
    public long add(SysFunction function) {
        verification(OperationTypeEnum.ADD.getValue(), function);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //主键
        if (ObjectUtils.isEmpty(function.getPageId())) {
            function.setFunctionId(idKeyGenerator.getId());
        }
        //设置创建人
        function.setCreateBy(currentUser.getUserId());
        //设置创建时间
        function.setCreateDate(Calendar.getInstance().getTime());
        int result = functionService.add(function);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
        return function.getFunctionId();
    }

    /**
     * 功能修改
     *
     * @param function 角色信息
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysFunction function) {
        if (ObjectUtils.isEmpty(function.getLanguage())) {
            function.setLanguage(LanguageEnum.ZH_CN.getValue());
        }
        verification(OperationTypeEnum.UPDATE.getValue(), function);
        //获取当前用户
        ResultData<SysUser> userResultData = authClient.currentUser();
        if (!userResultData.getSuccess()) {
            throw new BizException(userResultData.getCode(), userResultData.getMessage());
        }
        //当前用户
        SysUser currentUser = userResultData.getResult();
        //修改人
        function.setLastUpdateBy(currentUser.getUserId());
        //修改时间
        function.setLastUpdateDate(Calendar.getInstance().getTime());
        //最后修改的登录信息ID
        function.setLastUpdateLogin(currentUser.getLastLoginInfoId());
        int result = functionService.update(function);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 删除
     *
     * @param functionIds 功能ID,多个以逗号","分隔
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(String functionIds) {
        if (ObjectUtils.isEmpty(functionIds)) {
            throw new ParamValidateException("functionIds不能为空!");
        }
        List<Long> ids = Arrays.stream(functionIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        int result = functionService.delete(ids);
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 根据功能ID查询功能信息
     *
     * @param functionId 功能ID
     * @param language   语言
     * @return 功能信息
     */
    @PostMapping("findById")
    public SysFunction findById(Long functionId, String language) {
        if (ObjectUtils.isEmpty(functionId)) {
            throw new ParamValidateException("functionId不能为空");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        SysFunction function = functionService.findById(functionId, language);
        Long projectId = function.getProjectId();
        ProjectEntity project = projectService.getById(projectId);
        if (!ObjectUtils.isEmpty(project)) {
            function.setProject(project);
        }
        return function;
    }


    /**
     * 根据功能ID查询api接口(批量)
     *
     * @param functionIds 功能ID集合
     * @return uri接口
     */
    @PostMapping("getApiUris")
    public List<FunctionApiUri> getApiUris(@RequestBody List<Long> functionIds) {
        if (CollectionUtils.isEmpty(functionIds)) {
            throw new ParamValidateException("functionIds不能为空");
        }
        return functionService.getApiUris(functionIds);
    }

    /**
     * 功能接口绑定
     *
     * @param functionId 功能
     * @param apiIds     接口ID,多个以逗号","分隔
     */
    @AutoIdempotent
    @PostMapping("bindingApi")
    public void bindingApi(Long functionId, String apiIds) {
        if (ObjectUtils.isEmpty(functionId)) {
            throw new ParamValidateException("functionId参数错误!");
        }
        SysFunction sysFunction = functionService.findById(functionId, LanguageEnum.ZH_CN.getValue());
        if (ObjectUtils.isEmpty(sysFunction)) {
            throw new ParamValidateException("功能不存在!");
        }
        //待绑定的API权限列表
        List<SysFunctionApiPO> functionApiList = null;
        if (!ObjectUtils.isEmpty(apiIds)) {
            List<Long> idList = Arrays.stream(apiIds.split(",")).map(Long::parseLong).distinct().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(idList)) {
                throw new ParamValidateException("apiIds参数错误!");
            }
            List<SysApi> apiList = apiService.findByIds(idList);
            if (CollectionUtils.isEmpty(apiList)) {
                throw new ParamValidateException("接口不存在!");
            }
            //获取当前用户
            ResultData<SysUser> userResultData = authClient.currentUser();
            if (!userResultData.getSuccess()) {
                throw new BizException(userResultData.getCode(), userResultData.getMessage());
            }
            //当前用户
            SysUser currentUser = userResultData.getResult();
            functionApiList = apiList.stream().map(o -> {
                SysFunctionApiPO functionApi = new SysFunctionApiPO();
                functionApi.setFunctionId(functionId);
                functionApi.setApiId(o.getId());
                functionApi.setCreateBy(currentUser.getUserId());
                functionApi.setCreateDate(Calendar.getInstance().getTime());
                return functionApi;
            }).collect(Collectors.toList());
        }
        int result = functionService.bindingApi(functionId, functionApiList);
        if (result <= 0) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 新增
     * 此接口用于其他服务调用，参数使用@RequestBody接收
     *
     * @param function 功能信息
     * @return 功能ID
     */
    @AutoIdempotent
    @PostMapping("addJson")
    public long addJson(@RequestBody SysFunction function) {
        return this.add(function);
    }

    /**
     * 修改
     * 此接口用于其他服务调用，参数使用@RequestBody接收
     *
     * @param function 功能信息
     */
    @AutoIdempotent
    @PostMapping("updateJson")
    public void updateJson(@RequestBody SysFunction function) {
        this.update(function);
    }

    /**
     * 根据功能编码查询功能信息
     *
     * @param functionCode 功能编码
     * @param language     语言
     * @return 结果
     */
    @PostMapping("findByCode")
    public SysFunction findByCode(String functionCode, String language) {
        List<SysFunction> functionList = functionService.findByCode(functionCode, language);
        if (CollectionUtils.isEmpty(functionList)) {
            return null;
        }
        return functionList.get(0);
    }

    /**
     * 根据页面ID查询功能信息
     *
     * @param pageId   页面ID
     * @param language 语言
     * @return 结果
     */
    @PostMapping("findByPageId")
    public SysFunction findByPageId(Long pageId, String language) {
        if (ObjectUtils.isEmpty(pageId)) {
            throw new ParamValidateException("pageId不能为空!");
        }
        if (ObjectUtils.isEmpty(language)) {
            throw new ParamValidateException("language不能为空!");
        }
        if (!LanguageEnum.contains(language)) {
            throw new ParamValidateException("language参数错误");
        }
        return functionService.findByPageId(pageId, language);
    }

    /**
     * 根据功能ID查询按钮权限列表
     *
     * @param functionId 功能ID
     * @return 按钮权限集合
     */
    @PostMapping("findButtonById")
    public List<SysFunction> findButtonById(long functionId) {
        return functionService.findButtonById(functionId);
    }

    /**
     * 根据路由名称查询功能信息
     *
     * @param routeName 路由名称
     * @return 结果
     */
    @PostMapping("findByRouteName")
    public SysFunction findByRouteName(String routeName) {
        return functionService.findByRouteName(routeName);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param function  功能信息
     */
    private void verification(String operation, SysFunction function) {
        if (ObjectUtils.isEmpty(function)) {
            throw new ParamValidateException("参数错误");
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation) && ObjectUtils.isEmpty(function.getFunctionId())) {
            throw new ParamValidateException("functionId不能为空");
        }
        if (ObjectUtils.isEmpty(function.getFunctionCode())) {
            throw new ParamValidateException("功能编码不能为空");
        }
        if (function.getFunctionCode().length() > 80) {
            throw new ParamValidateException("功能编码超长(最大80))");
        }
        if (ObjectUtils.isEmpty(function.getFunctionName())) {
            throw new ParamValidateException("名称不能为空");
        }
        if (function.getFunctionName().length() > 50) {
            throw new ParamValidateException("名称超长(最大50))");
        }
        if (ObjectUtils.isEmpty(function.getFunctionType())) {
            throw new ParamValidateException("功能类型不能为空");
        }
        if (!FunctionTypeEnum.contains(function.getFunctionType())) {
            throw new ParamValidateException("功能类型错误");
        }
        //功能编码唯一性校验：1.功能类型=A时，必须保证全局唯一；2.功能类型=B时，保证在此功能下唯一即可。
        if (FunctionTypeEnum.A.getValue().equals(function.getFunctionType())) {
            if (ObjectUtils.isEmpty(function.getName())) {
                throw new ParamValidateException("路由名称不能为空!");
            }
            //根据功能编码查询功能信息
            List<SysFunction> functionList = functionService.findByCode(function.getFunctionCode(), LanguageEnum.ZH_CN.getValue());
            if (!CollectionUtils.isEmpty(functionList)) {
                //获取功能类型=A的数据
                SysFunction functionOld = functionList.stream().filter(o -> FunctionTypeEnum.A.getValue().equals(o.getFunctionType())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(functionOld)) {
                    if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                        throw new ParamValidateException("功能编码已存在!");
                    } else {
                        //根据功能ID查询功能信息
                        SysFunction resultIdData = functionService.findById(function.getFunctionId(), function.getLanguage());
                        if (!ObjectUtils.isEmpty(functionOld) && !functionOld.getFunctionId().equals(resultIdData.getFunctionId())) {
                            throw new ParamValidateException("功能编码已存在!");
                        }
                    }
                }
            }
            //根据路由名称查询功能信息
            SysFunction resultData = functionService.findByRouteName(function.getName());
            if (!ObjectUtils.isEmpty(resultData) && resultData.getFunctionType().equals(function.getFunctionType())) {
                if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                    throw new ParamValidateException("路由名称已存在!");
                } else {
                    //根据功能ID查询功能信息
                    SysFunction resultIdData = functionService.findById(function.getFunctionId(), function.getLanguage());
                    if (!ObjectUtils.isEmpty(resultData) && !resultData.getFunctionId().equals(resultIdData.getFunctionId())) {
                        throw new ParamValidateException("路由名称已存在!");
                    }
                }
            }
        } else if (FunctionTypeEnum.B.getValue().equals(function.getFunctionType())) {
            //根据功能ID查询按钮权限列表
            List<SysFunction> btnList = functionService.findButtonById(function.getParentId());
            if (CollectionUtils.isEmpty(btnList)) {
                return;
            }
            //判断是否存在该编码的数据
            SysFunction checkRs = btnList.stream().filter(o -> o.getFunctionCode().equals(function.getFunctionCode())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(checkRs)) {
                if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                    throw new ParamValidateException("编码已存在!");
                }
                if (OperationTypeEnum.UPDATE.getValue().equals(operation) && !checkRs.getFunctionId().equals(function.getFunctionId())) {
                    throw new ParamValidateException("编码已存在!");
                }
            }
            //设置按钮的项目ID与父级项目ID一致
            SysFunction parentFunction = functionService.findMinById(function.getParentId(), LanguageEnum.ZH_CN.getValue());
            if (ObjectUtils.isEmpty(parentFunction)) {
                throw new ParamValidateException("父级功能不存在!");
            }
            function.setProjectId(parentFunction.getProjectId());
        }
        if (!ObjectUtils.isEmpty(function.getFunctionDescription()) && function.getFunctionDescription().length() > 80) {
            throw new ParamValidateException("描述超长(最大80)!");
        }
        if (!FunctionTypeEnum.A.getValue().equals(function.getFunctionType())) {
            //功能类型!=A时，后面的校验就没必要做了
            return;
        }
        if (ObjectUtils.isEmpty(function.getOpenMethod())) {
            throw new ParamValidateException("打开方式不能为空");
        }
        if (!OpenMethodEnum.contains(function.getOpenMethod())) {
            throw new ParamValidateException("打开方式参数错误");
        }
        if (ObjectUtils.isEmpty(function.getKeepAlive())) {
            throw new ParamValidateException("缓存不能为空!");
        }
        if (!ObjectUtils.isEmpty(function.getComponent()) && function.getComponent().length() > 200) {
            throw new ParamValidateException("前端组件超长(最大200)!");
        }
        if (!ObjectUtils.isEmpty(function.getRedirect()) && function.getRedirect().length() > 200) {
            throw new ParamValidateException("重定向地址超长(最大200)!");
        }
    }

    /**
     * 导出
     *
     * @param response       响应
     * @param functionIdList 功能ID集合
     */
    @PostMapping("export")
    public void export(HttpServletResponse response, @RequestBody List<Long> functionIdList) {
        if (CollectionUtils.isEmpty(functionIdList)) {
            throw new ParamValidateException("请选择需要导出的数据!");
        }
        //查询功能导出数据
        List<SysFunctionBO> functionListExport = functionService.exportData(functionIdList);
        byte[] bytes = JSON.toJSONString(functionListExport).getBytes(StandardCharsets.UTF_8);
        //构建下载文件名称
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "功能导出-" + sdf.format(Calendar.getInstance().getTime()) + ".json";
        //文件下载
        CustomFileUtils.download(response, fileName, bytes);
    }

    /**
     * 导入
     * 1.首先判断method+uri是否存在，如果存在则不允许导入，如果不存在则进行导入
     * 2.判断ID是否存在，如果存在则做修改操作。
     *
     * @param multipartRequest 文件
     */
    @AutoIdempotent(seconds = 5)
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
        String body;
        try {
            byte[] bytes = multipartFile.getBytes();
            body = new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("数据转换异常：", e);
            throw new BizException("导入数据失败!");
        }
        //导入的数据集合
        List<SysFunctionBO> sysFunctionBoListImport = JSONObject.parseObject(body, new TypeReference<List<SysFunctionBO>>() {
        });
        if (CollectionUtils.isEmpty(sysFunctionBoListImport)) {
            throw new BizException("没有需要导入的数据!");
        }
        ResultData<SysUser> resultData = authClient.currentUser();
        if (!resultData.getSuccess() || ObjectUtils.isEmpty(resultData.getResult())) {
            throw new BizException("获取到当前用户失败!");
        }
        SysUser sysUser = resultData.getResult();
        Date nowDate = Calendar.getInstance().getTime();
        for (SysFunctionBO sysFunctionBO : sysFunctionBoListImport) {
            sysFunctionBO.setCreateBy(sysUser.getUserId());
            sysFunctionBO.setCreateDate(nowDate);
            List<SysFunctionTl> functionTlList = sysFunctionBO.getFunctionTlList();
            if (!CollectionUtils.isEmpty(functionTlList)) {
                functionTlList.forEach(o -> {
                    o.setCreateBy(sysUser.getUserId());
                    o.setCreateDate(nowDate);
                });
            }
            List<SysApi> apiList = sysFunctionBO.getApiList();
            if (!CollectionUtils.isEmpty(apiList)) {
                apiList.forEach(o -> {
                    o.setCreateBy(sysUser.getUserId());
                    o.setCreateDate(nowDate);
                });
            }
        }
        int result = functionService.importData(sysFunctionBoListImport);
        if (result <= 0) {
            throw new BizException("导入失败!");
        }
    }

    /**
     * 根据项目编码查询项目下绑定的功能ID集合
     *
     * @param projectCode 项目编码(必填)
     * @return 功能ID集合
     */
    @GetMapping("findFunctionIdByProjectCode")
    public List<Long> findFunctionIdByProjectCode(String projectCode) {
        if (ObjectUtils.isEmpty(projectCode)) {
            throw new ParamValidateException("projectCode不能为空!");
        }
        ProjectEntity project = projectService.findByCode(projectCode);
        if (ObjectUtils.isEmpty(project)) {
            throw new BizException("项目编码不存在");
        }
        List<SysFunction> functionList = functionService.findByProjectId(project.getId());
        if (!CollectionUtils.isEmpty(functionList)) {
            return functionList.stream().map(SysFunction::getFunctionId).collect(Collectors.toList());
        }
        return null;
    }
}
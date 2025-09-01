package com.crrc.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.crrc.sys.annotation.AutoIdempotent;
import com.crrc.sys.client.AuthClient;
import com.crrc.sys.common.*;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.entity.api.SysApiAuth;
import com.crrc.sys.entity.api.SysApiAuthCache;
import com.crrc.sys.entity.api.SysApiAuthPageDTO;
import com.crrc.sys.entity.api.SysApiAuthVO;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.enums.ApiAuthTypeEnum;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.service.SysApiAuthService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 权限(白/黑)名单-控制器
 *
 * @author 谭永强
 * @date 2024-01-03
 */
@RestController
@RequestMapping("/api/auth")
public class SysApiAuthController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AuthClient authClient;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private SysApiAuthService apiAuthService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /**
     * 全局权限(白/黑)名单key
     */
    private final String GLOBAL_API_AUTH_KEY = "global_api_auth_key";

    /**
     * 获取权限(白/黑)名单缓存
     *
     * @return 权限(白 / 黑)名单缓存
     */
    @GetMapping("getApiAuthCache")
    public SysApiAuthCache getApiAuthCache() {
        Object obj = redisUtils.get(GLOBAL_API_AUTH_KEY);
        if (ObjectUtils.isEmpty(obj)) {
            //更新缓存数据
            setApiAuthCache();
            obj = redisUtils.get(GLOBAL_API_AUTH_KEY);
        }
        return (SysApiAuthCache) obj;
    }

    /**
     * 权限(白/黑)名单分页查询
     *
     * @param param 参数
     * @return 分页结果
     */
    @PostMapping("findByPage")
    public PaginationGrid<SysApiAuth> findByPage(SysApiAuthPageDTO param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new ParamValidateException("参数错误");
        }
        if (ObjectUtils.isEmpty(param.getPage()) || param.getPage() <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(param.getPageSize()) || param.getPageSize() < 0) {
            throw new ParamValidateException("pageSize不能为空且必须>=0");
        }
        return apiAuthService.findByPage(param);
    }

    /**
     * 权限(白/黑)名单新增
     *
     * @param apiAuth 参数
     */
    @AutoIdempotent
    @PostMapping("add")
    public void add(SysApiAuth apiAuth) {
        verification(OperationTypeEnum.ADD.getValue(), apiAuth);
        //获取当前用户
        ResultData<SysUser> resultData = authClient.currentUser();
        if (!resultData.getSuccess()) {
            throw new BizException(resultData.getMessage());
        }
        apiAuth.setAuthId(idKeyGenerator.getId());
        apiAuth.setCreateBy(resultData.getResult().getUserId());
        apiAuth.setCreateDate(Calendar.getInstance().getTime());
        int result = apiAuthService.add(apiAuth);
        if (result <= 0) {
            throw new BizException("新增失败!");
        }
        //更新缓存数据
        setApiAuthCache();
    }

    /**
     * 权限(白/黑)名单修改
     *
     * @param apiAuth 参数
     */
    @AutoIdempotent
    @PostMapping("update")
    public void update(SysApiAuth apiAuth) {
        verification(OperationTypeEnum.UPDATE.getValue(), apiAuth);
        //获取当前用户
        ResultData<SysUser> resultData = authClient.currentUser();
        if (!resultData.getSuccess()) {
            throw new BizException(resultData.getMessage());
        }
        apiAuth.setLastUpdateBy(resultData.getResult().getUserId());
        apiAuth.setLastUpdateDate(Calendar.getInstance().getTime());
        apiAuth.setLastUpdateLogin(resultData.getResult().getLastLoginInfoId());
        int result = apiAuthService.update(apiAuth);
        if (result <= 0) {
            throw new BizException("修改失败!");
        }
        //更新缓存数据
        setApiAuthCache();
    }

    /**
     * 根据名单ID查询名单信息
     *
     * @param authId 主键
     * @return 结果
     */
    @PostMapping("findById")
    public SysApiAuth findById(Long authId) {
        if (ObjectUtils.isEmpty(authId)) {
            throw new ParamValidateException("authId参数不能为空!");
        }
        return apiAuthService.findById(authId);
    }

    /**
     * 删除
     *
     * @param ids 主键
     */
    @AutoIdempotent
    @PostMapping("delete")
    public void delete(@RequestBody List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new ParamValidateException("参数不能为空!");
        }
        int result = apiAuthService.delete(ids);
        if (result <= 0) {
            throw new BizException("删除失败!");
        }
        //更新缓存数据
        setApiAuthCache();
    }

    /**
     * 导出
     *
     * @param response 响应
     * @param ids      主键，多个使用(,)分隔
     * @throws Exception 异常
     */
    @GetMapping("export")
    public void export(HttpServletResponse response, String ids) throws Exception {
        List<Long> authIdList = null;
        if (!ObjectUtils.isEmpty(ids)) {
            authIdList = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        }
        List<SysApiAuth> apiAuthList;
        if (CollectionUtils.isEmpty(authIdList)) {
            //如果没有参数，则直接导出全部数据
            apiAuthList = apiAuthService.findByAuthType(null);
        } else {
            //有参数，则只导出选择的数据
            apiAuthList = apiAuthService.findByAuthIdList(authIdList);
        }
        if (CollectionUtils.isEmpty(apiAuthList)) {
            throw new BizException("没有需要导出的数据!");
        }
        List<SysApiAuthVO> exportList = apiAuthList.stream().map(o -> new SysApiAuthVO(o.getAuthId(), o.getAuthUri(), o.getAuthType(), o.getAuthDesc())).collect(Collectors.toList());
        byte[] bytes = JSON.toJSONString(exportList).getBytes(StandardCharsets.UTF_8);
        //构建下载文件名称
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "(白/黑)名单导出-" + sdf.format(Calendar.getInstance().getTime()) + ".json";
        //文件下载
        CustomFileUtils.download(response, fileName, bytes);
    }

    /**
     * 导入
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
        List<SysApiAuth> apiAuthList = JSONObject.parseObject(body, new TypeReference<List<SysApiAuth>>() {
        });
        if (CollectionUtils.isEmpty(apiAuthList)) {
            throw new BizException("没有需要导入的数据!");
        }
        //查询所有数据，用以校验导入的数据吃饭存在相同的URI规则
        List<SysApiAuth> apiAuthAll = apiAuthService.findByAuthType(null);
        if (!CollectionUtils.isEmpty(apiAuthAll)) {
            Iterator<SysApiAuth> iterator = apiAuthList.iterator();
            while (iterator.hasNext()) {
                SysApiAuth apiAuth = iterator.next();
                SysApiAuth sysApiAuth = apiAuthAll.stream()
                        //保证类型一致
                        .filter(o -> o.getAuthType().equals(apiAuth.getAuthType()))
                        //判断URI规则是否一致
                        .filter(o -> antPathMatcher.match(o.getAuthUri(), apiAuth.getAuthUri()))
                        .findFirst()
                        .orElse(null);
                if (!ObjectUtils.isEmpty(sysApiAuth)) {
                    //说明现有数据存在与该URI相匹配的情况，则不允许插入数据库
                    iterator.remove();
                }
            }
        }
        if (!CollectionUtils.isEmpty(apiAuthList)) {
            //获取当前用户
            ResultData<SysUser> resultData = authClient.currentUser();
            if (!resultData.getSuccess()) {
                throw new BizException(resultData.getMessage());
            }
            for (SysApiAuth sysApiAuth : apiAuthList) {
                sysApiAuth.setCreateBy(resultData.getResult().getUserId());
                sysApiAuth.setCreateDate(Calendar.getInstance().getTime());
            }
            //保存到数据库
            int result = apiAuthService.insertList(apiAuthList);
            if (result <= 0) {
                throw new BizException("导入失败!");
            }
            //更新缓存数据
            setApiAuthCache();
        }
    }

    /**
     * 参数校验
     *
     * @param operation 操作类型
     * @param apiAuth   参数
     */
    private void verification(String operation, SysApiAuth apiAuth) {
        if (ObjectUtils.isEmpty(apiAuth)) {
            throw new ParamValidateException("参数错误!");
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation) && ObjectUtils.isEmpty(apiAuth.getAuthId())) {
            throw new ParamValidateException("authId不能为空");
        }
        if (ObjectUtils.isEmpty(apiAuth.getAuthUri())) {
            throw new ParamValidateException("authUri不能为空!");
        }
        if (ObjectUtils.isEmpty(apiAuth.getAuthType())) {
            throw new ParamValidateException("authType不能为空!");
        }
        if (!ApiAuthTypeEnum.contains(apiAuth.getAuthType())) {
            throw new ParamValidateException("authType值错误!");
        }
        //校验该URI匹配规则是否已经存在
        List<SysApiAuth> apiAuthList = apiAuthService.findByAuthType(apiAuth.getAuthType());
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            //移除旧数据
            apiAuthList.removeIf(o -> o.getAuthId().equals(apiAuth.getAuthId()));
        }
        if (!CollectionUtils.isEmpty(apiAuthList)) {
            SysApiAuth sysApiAuth = apiAuthList.stream().filter(o -> antPathMatcher.match(o.getAuthUri(), apiAuth.getAuthUri())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(sysApiAuth)) {
                throw new BizException("当前URI已存在相匹配的数据，无需重复添加!");
            }
        }
    }

    /**
     * 设置权限(白/黑)名单缓存
     */
    private void setApiAuthCache() {
        SysApiAuthCache apiAuthCache = new SysApiAuthCache();
        //查询所有权限(白/黑)名单
        List<SysApiAuth> apiAuthList = apiAuthService.findByAuthType(null);
        if (CollectionUtils.isEmpty(apiAuthList)) {
            redisUtils.set(GLOBAL_API_AUTH_KEY, apiAuthCache);
            return;
        }
        Map<String, List<SysApiAuth>> collect = apiAuthList.stream().collect(Collectors.groupingBy(SysApiAuth::getAuthType));
        for (Map.Entry<String, List<SysApiAuth>> entry : collect.entrySet()) {
            List<String> list = entry.getValue().stream().map(SysApiAuth::getAuthUri).collect(Collectors.toList());
            if (entry.getKey().equals(ApiAuthTypeEnum.A.getValue())) {
                apiAuthCache.setWhiteList(list);
            } else if (entry.getKey().equals(ApiAuthTypeEnum.B.getValue())) {
                apiAuthCache.setBlackList(list);
            } else {
                System.out.println("未知类型");
            }
        }
        //缓存(白/黑)名单数据
        redisUtils.set(GLOBAL_API_AUTH_KEY, apiAuthCache);
    }
}

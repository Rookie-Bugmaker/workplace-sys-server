package com.yunling.sys.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunling.sys.common.Arith;
import com.yunling.sys.common.IdKeyGenerator;
import com.yunling.sys.common.MyThreadPoolExecutor;
import com.yunling.sys.common.PaginationGrid;
import com.yunling.sys.config.exception.BizException;
import com.yunling.sys.convert.SysFunctionConvert;
import com.yunling.sys.entity.api.SysApi;
import com.yunling.sys.entity.function.*;
import com.yunling.sys.enums.LanguageEnum;
import com.yunling.sys.mapper.SysApiMapper;
import com.yunling.sys.mapper.SysFunctionApiMapper;
import com.yunling.sys.mapper.SysFunctionMapper;
import com.yunling.sys.mapper.SysFunctionTlMapper;
import com.yunling.sys.service.SysFunctionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-09-15
 */
@Service
public class SysFunctionServiceImpl implements SysFunctionService {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private SysFunctionMapper functionMapper;
    @Resource
    private SysFunctionTlMapper functionTlMapper;
    @Resource
    private SysFunctionApiMapper functionApiMapper;
    @Resource
    private SysApiMapper apiMapper;

    /**
     * 功能分页查询
     *
     * @param param 参数
     * @return 结果
     */
    @Override
    public PaginationGrid<SysFunction> findByPage(SysFunctionFindByPageParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<SysFunction> rows = functionMapper.findByFuzzy(param);
        if (!ObjectUtils.isEmpty(rows)) {
            //查询所有子功能数据
            List<SysFunction> childrenAll = functionMapper.findByParentIds(null, param.getLanguage());
            //接口权限查询
            List<SysFunctionApiBO> functionApiList = functionApiMapper.findByFunctionIds(null);
            //递归查找子功能，并设置接口权限
            setChildren(rows, childrenAll, functionApiList);
        }
        PageInfo<SysFunction> pageInfo = new PageInfo<>(rows);
        return PaginationGrid.success(pageInfo);
    }

    /**
     * 递归查找子功能
     *
     * @param parentList     上级功能
     * @param childrenAll    所有子功能
     * @param functionApiAll 所有子功能
     */
    private void setChildren(List<SysFunction> parentList, List<SysFunction> childrenAll, List<SysFunctionApiBO> functionApiAll) {
        for (SysFunction parent : parentList) {
            if (!CollectionUtils.isEmpty(functionApiAll)) {
                //查询接口权限
                List<SysApi> apiList = functionApiAll.stream().filter(o -> o.getFunctionId().equals(parent.getFunctionId())).map(SysFunctionApiBO::getApiList).flatMap(Collection::stream).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(apiList)) {
                    List<SysApi> sortApiList = apiList.stream().sorted(Comparator.comparing(SysApi::getApiUri)).collect(Collectors.toList());
                    parent.setApiList(sortApiList);
                }
            }
            //查询子功能
            List<SysFunction> children = childrenAll.stream().filter(o -> o.getParentId().equals(parent.getFunctionId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(children)) {
                setChildren(children, childrenAll, functionApiAll);
                parent.setChildren(children);
            }
        }
    }

    /**
     * 初始化低代码功能API权限
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void init() {
        SysFunctionFindByPageParam param = new SysFunctionFindByPageParam();
        param.setPage(1);
        param.setPageSize(0);
        param.setLanguage("zh-CN");
        PaginationGrid<SysFunction> grid = this.findByPage(param);
        List<SysFunction> rows = grid.getRows();
        for (SysFunction row : rows) {
            if (ObjectUtils.isEmpty(row.getPageId())) {
                continue;
            }
            List<SysFunction> btnList = row.getChildren();
            if (!CollectionUtils.isEmpty(btnList)) {
                saveFunBtn(row, btnList);
            }
        }
    }

    /**
     * 初始化工作流功能API权限
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initWorkflow() {
        //查询所有使用工作流的功能
        List<SysFunction> functionList = functionMapper.findByWorkflow();
        //添加按钮
        Map<String, BtnApiBO> btnMap = new HashMap<>();
        List<String> workflowApiList = new ArrayList<>();
        workflowApiList.add("/workflow/processCtl/startProcessInstance/*");
        workflowApiList.add("/workflow/processCtl/sequenceGraph");
        workflowApiList.add("/workflow/processCtl/runningSequenceGraph/*");
        workflowApiList.add("/workflow/processCtl/bizInfo/*");
        workflowApiList.add("/workflow/processCtl/done");
        workflowApiList.add("/workflow/processCtl/deliver");
        workflowApiList.add("/workflow/processCtl/reject");
        workflowApiList.add("/workflow/processCtl/refuse");
        workflowApiList.add("/workflow/processCtl/rejectToPromoter");
        workflowApiList.add("/workflow/processCtl/addSign");
        workflowApiList.add("/workflow/processCtl/resolve");
        workflowApiList.add("/workflow/processCtl/processStatus");
        workflowApiList.add("/workflow/processCtl/shutdown");
        workflowApiList.add("/workflow/processCtl/findSequenceGraphByBizId");
        workflowApiList.add("/sys/function/findById");
        workflowApiList.add("/sys/org/person/findById");
        workflowApiList.add("/sys/org/person/findByIds");
        workflowApiList.add("/sys/org/person/findByPage");
        btnMap.put("workflow", new BtnApiBO("工作流", workflowApiList));
        Date nowDate = Calendar.getInstance().getTime();
        for (SysFunction function : functionList) {
            if (function.getFunctionId() == 157080186649886720L) {
                continue;
            }
            List<SysFunction> btnList = new ArrayList<>(btnMap.size());
            for (Map.Entry<String, BtnApiBO> entry : btnMap.entrySet()) {
                //基础信息
                SysFunction btnFunction = new SysFunction();
                btnFunction.setFunctionId(idKeyGenerator.getId());
                btnFunction.setParentId(function.getFunctionId());
                btnFunction.setFunctionCode(entry.getKey());
                btnFunction.setFunctionType("B");
                btnFunction.setCreateBy(function.getCreateBy());
                btnFunction.setCreateDate(nowDate);
                btnFunction.setLastUpdateLogin(function.getLastUpdateLogin());
                List<SysFunctionTl> btnTlList = new ArrayList<>();
                //多语言
                SysFunctionTl btnTl = new SysFunctionTl();
                btnTl.setFunctionId(btnFunction.getFunctionId());
                btnTl.setFunctionName(entry.getValue().getName());
                btnTl.setLanguage(LanguageEnum.ZH_CN.getValue());
                btnTl.setCreateBy(function.getCreateBy());
                btnTl.setCreateDate(function.getCreateDate());
                btnTl.setLastUpdateLogin(function.getLastUpdateLogin());
                btnTlList.add(btnTl);
                btnTl = new SysFunctionTl();
                btnTl.setFunctionId(btnFunction.getFunctionId());
                btnTl.setFunctionName(entry.getValue().getName());
                btnTl.setLanguage(LanguageEnum.EN_US.getValue());
                btnTl.setCreateBy(function.getCreateBy());
                btnTl.setCreateDate(function.getCreateDate());
                btnTl.setLastUpdateLogin(function.getLastUpdateLogin());
                btnTlList.add(btnTl);
                int result = functionTlMapper.insertTl(btnTlList);
                if (result <= 0) {
                    throw new BizException("按钮权限(多语言)新增失败!");
                }
                result = functionMapper.insert(btnFunction);
                if (result <= 0) {
                    throw new BizException("按钮权限新增失败!");
                }
                btnList.add(btnFunction);
            }
            //给工作流的按钮绑定API权限
            for (SysFunction sysFunction : btnList) {
                BtnApiBO btnApiBO = btnMap.get(sysFunction.getFunctionCode());
                //该按钮需要绑定的API列表
                List<String> apiUriList = btnApiBO.getApiUriList();
                //根据URI查询API信息
                List<SysApi> apiList = apiMapper.findByUris(apiUriList);
                //待查询的URI个数与已查询到的URI个数不匹配
                if (CollectionUtils.isEmpty(apiList) || apiUriList.size() != apiList.size()) {
                    throw new BizException("存在API权限与数据库不匹配!");
                }
                List<SysFunctionApiPO> functionApiList = new ArrayList<>(apiList.size());
                for (SysApi sysApi : apiList) {
                    SysFunctionApiPO functionApi = new SysFunctionApiPO();
                    functionApi.setFunctionId(sysFunction.getFunctionId());
                    functionApi.setApiId(sysApi.getId());
                    functionApi.setCreateBy(function.getCreateBy());
                    functionApi.setCreateDate(function.getCreateDate());
                    functionApiList.add(functionApi);
                }
                //当前按钮已有权限
                SysFunctionApiBO sysFunctionApi = functionApiMapper.findByFunctionId(sysFunction.getFunctionId());
                if (!ObjectUtils.isEmpty(sysFunctionApi)) {
                    //判断新添加的权限是否在存在，不存在才添加
                    for (SysApi api : sysFunctionApi.getApiList()) {
                        SysFunctionApiPO functionApi = functionApiList.stream()
                                .filter(o -> o.getFunctionId().equals(sysFunctionApi.getFunctionId()))
                                .filter(o -> api.getId().equals(o.getApiId()))
                                .findFirst()
                                .orElse(null);
                        if (!ObjectUtils.isEmpty(functionApi)) {
                            functionApiList.remove(functionApi);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(functionApiList)) {
                    int insert = functionApiMapper.insert(functionApiList);
                    if (insert != functionApiList.size()) {
                        throw new BizException("功能绑定API权限失败!");
                    }
                }
            }
        }
    }

    /**
     * 根据功能ID查询功能信息(最小查询)
     *
     * @param functionId 功能ID
     * @param language   语言
     * @return 功能信息
     */
    @Override
    public SysFunction findMinById(long functionId, String language) {
        return functionMapper.findById(functionId, language);
    }

    /**
     * 功能按钮处理
     *
     * @param function   功能信息
     * @param oldBtnList 按钮集合，传null时说明是功能新增
     */
    private void saveFunBtn(SysFunction function, List<SysFunction> oldBtnList) {
        //初始化低代码功能默认的按钮集合
        Map<String, BtnApiBO> map = new HashMap<>();
        //初始化低代码功能默认的按钮权限
        //新增-API集合
        String[] addApiList = {"/lowcode/insert"};
        //编辑-API集合
        String[] editApiList = {"/lowcode/update"};
        //删除-API集合
        String[] removeApiList = {"/lowcode/delete"};
        //查询-API集合
        String[] searchApiList = {"/lowcode/query", "/lowcode/page/findByPageId", "/lowcode/page/config/findByPageId", "/lowcode/queryByPagination"};
        //导出-API集合
        String[] exportApiList = {"/lowcode/export/file"};
        map.put("add", new BtnApiBO("新增", Arrays.asList(addApiList)));
        map.put("edit", new BtnApiBO("修改", Arrays.asList(editApiList)));
        map.put("remove", new BtnApiBO("删除", Arrays.asList(removeApiList)));
        map.put("search", new BtnApiBO("查询", Arrays.asList(searchApiList)));
        map.put("export", new BtnApiBO("导出", Arrays.asList(exportApiList)));
        List<SysFunction> btnList = new ArrayList<>(map.size());
        for (Map.Entry<String, BtnApiBO> entry : map.entrySet()) {
            //基础信息
            SysFunction btnFunction = new SysFunction();
            if (!CollectionUtils.isEmpty(oldBtnList)) {
                SysFunction btn = oldBtnList.stream().filter(o -> o.getFunctionCode().equals(entry.getKey())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(btn)) {
                    //按钮存在，就直接给按钮绑定API权限就行了
                    btnList.add(btn);
                    continue;
                }
            }
            btnFunction.setFunctionId(idKeyGenerator.getId());
            btnFunction.setParentId(function.getFunctionId());
            btnFunction.setFunctionCode(entry.getKey());
            btnFunction.setFunctionType("B");
            btnFunction.setCreateBy(function.getCreateBy());
            btnFunction.setCreateDate(function.getCreateDate());
            btnFunction.setLastUpdateLogin(function.getLastUpdateLogin());
            List<SysFunctionTl> btnTlList = new ArrayList<>();
            //多语言
            SysFunctionTl btnTl = new SysFunctionTl();
            btnTl.setFunctionId(btnFunction.getFunctionId());
            btnTl.setFunctionName(entry.getValue().getName());
            btnTl.setLanguage(LanguageEnum.ZH_CN.getValue());
            btnTl.setCreateBy(function.getCreateBy());
            btnTl.setCreateDate(function.getCreateDate());
            btnTl.setLastUpdateLogin(function.getLastUpdateLogin());
            btnTlList.add(btnTl);
            btnTl = new SysFunctionTl();
            btnTl.setFunctionId(btnFunction.getFunctionId());
            btnTl.setFunctionName(entry.getValue().getName());
            btnTl.setLanguage(LanguageEnum.EN_US.getValue());
            btnTl.setCreateBy(function.getCreateBy());
            btnTl.setCreateDate(function.getCreateDate());
            btnTl.setLastUpdateLogin(function.getLastUpdateLogin());
            btnTlList.add(btnTl);
            int result = functionTlMapper.insertTl(btnTlList);
            if (result <= 0) {
                throw new BizException("按钮权限(多语言)新增失败!");
            }
            result = functionMapper.insert(btnFunction);
            if (result <= 0) {
                throw new BizException("按钮权限新增失败!");
            }
            btnList.add(btnFunction);
        }
        //给低代码的按钮绑定API权限
        for (SysFunction sysFunction : btnList) {
            BtnApiBO btnApiBO = map.get(sysFunction.getFunctionCode());
            //该按钮需要绑定的API列表
            List<String> apiUriList = btnApiBO.getApiUriList();
            List<SysApi> apiList = apiMapper.findByUris(apiUriList);
            //待查询的URI个数与已查询到的URI个数不匹配
            if (CollectionUtils.isEmpty(apiList) || apiUriList.size() != apiList.size()) {
                throw new BizException("存在API权限与数据库不匹配!");
            }
            List<SysFunctionApiPO> functionApiList = new ArrayList<>(apiList.size());
            for (SysApi sysApi : apiList) {
                SysFunctionApiPO functionApi = new SysFunctionApiPO();
                functionApi.setFunctionId(sysFunction.getFunctionId());
                functionApi.setApiId(sysApi.getId());
                functionApi.setCreateBy(function.getCreateBy());
                functionApi.setCreateDate(function.getCreateDate());
                functionApiList.add(functionApi);
            }
            //当前按钮已有权限
            SysFunctionApiBO sysFunctionApi = functionApiMapper.findByFunctionId(sysFunction.getFunctionId());
            if (!ObjectUtils.isEmpty(sysFunctionApi)) {
                //判断新添加的权限是否在存在，不存在才添加
                for (SysApi api : sysFunctionApi.getApiList()) {
                    SysFunctionApiPO functionApi = functionApiList.stream()
                            .filter(o -> o.getFunctionId().equals(sysFunctionApi.getFunctionId()))
                            .filter(o -> api.getId().equals(o.getApiId()))
                            .findFirst()
                            .orElse(null);
                    if (!ObjectUtils.isEmpty(functionApi)) {
                        functionApiList.remove(functionApi);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(functionApiList)) {
                int insert = functionApiMapper.insert(functionApiList);
                if (insert != functionApiList.size()) {
                    throw new BizException("功能绑定API权限失败!");
                }
            }
        }
    }

    /**
     * 功能新增
     *
     * @param function 功能信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int add(SysFunction function) {
        List<SysFunctionTl> functionTlList = new ArrayList<>();
        SysFunctionTl functionTl = new SysFunctionTl();
        BeanUtils.copyProperties(function, functionTl);
        functionTl.setLanguage(LanguageEnum.ZH_CN.getValue());
        functionTlList.add(functionTl);
        functionTl = new SysFunctionTl();
        BeanUtils.copyProperties(function, functionTl);
        functionTl.setLanguage(LanguageEnum.EN_US.getValue());
        functionTlList.add(functionTl);
        //1.功能多语言新增
        int result = functionTlMapper.insertTl(functionTlList);
        if (result <= 0) {
            throw new BizException("功能(多语言)新增失败!");
        }
        //2.功能新增
        result = functionMapper.insert(function);
        if (result <= 0) {
            throw new BizException("功能新增失败!");
        }
        //pageId!=null，说明当前操作是低代码页面的发布操作，需要创建add、edit、remove、search按钮权限
        if (!ObjectUtils.isEmpty(function.getPageId())) {
            saveFunBtn(function, null);
        }
        return result;
    }

    /**
     * 功能修改
     *
     * @param function 功能信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SysFunction function) {
        SysFunctionTl functionTl = new SysFunctionTl();
        functionTl.setFunctionId(function.getFunctionId());
        functionTl.setLanguage(function.getLanguage());
        functionTl.setFunctionName(function.getFunctionName());
        functionTl.setFunctionDescription(function.getFunctionDescription());
        functionTl.setLastUpdateBy(function.getLastUpdateBy());
        functionTl.setLastUpdateDate(function.getLastUpdateDate());
        functionTl.setLastUpdateLogin(function.getLastUpdateLogin());
        //1.修改功能语言信息
        int result = functionMapper.updateTl(functionTl);
        if (result <= 0) {
            throw new BizException("功能(多语言)修改失败!");
        }
        //2.修改功能
        result = functionMapper.update(function);
        if (result <= 0) {
            throw new BizException("功能修改失败!");
        }
        return result;
    }

    /**
     * 功能删除
     *
     * @param functionIds 功能ID集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(List<Long> functionIds) {
        //查询所有子功能
        List<SysFunction> childrenList = functionMapper.findByParentIds(functionIds, LanguageEnum.ZH_CN.getValue());
        if (!ObjectUtils.isEmpty(childrenList)) {
            functionIds.addAll(childrenList.stream().map(SysFunction::getFunctionId).collect(Collectors.toList()));
        }
        //根据功能ID删除功能API数据
        functionApiMapper.deleteByFunctionIds(functionIds);
        //功能(多语言)删除
        functionTlMapper.deleteTl(functionIds);
        //功能删除
        return functionMapper.delete(functionIds);
    }

    /**
     * 根据功能ID查询功能信息
     *
     * @param functionId 功能ID
     * @param language   语言
     * @return 功能信息
     */
    @Override
    public SysFunction findById(long functionId, String language) {
        //查询功能信息
        SysFunction function = functionMapper.findById(functionId, language);
        if (!ObjectUtils.isEmpty(function)) {
            //查询所有子功能数据
            List<SysFunction> childrenAll = findNotParent(language);
            //接口权限查询
            List<SysFunctionApiBO> functionApiList = findAllFunctionApi();
            //递归查找子功能，并设置接口权限
            setChildren(Collections.singletonList(function), childrenAll, functionApiList);
        }
        return function;
    }

    /**
     * 查询所有子功能(优化)
     * 使用多线程优化查询效率
     *
     * @param language 语言
     * @return 菜单集合
     */
    private List<SysFunction> findNotParent(String language) {
        //查询所有子功能总条数
        int count = functionMapper.findNotParent(language);
        if (count <= 0) {
            return null;
        }
        //每页查询数据条数
        int pageSize = 200;
        //最大页码
        int page = (int) Math.ceil(Arith.div(count, pageSize, 2));
        List<SysFunction> functionListAll = new LinkedList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>(page);
        //自定义线程池
        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(page);
        for (int i = 1; i <= page; i++) {
            final PaginationGrid<SysFunction> pg = new PaginationGrid<>(i, pageSize);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                List<SysFunction> functionList = functionMapper.findPageByNotParent(language, pg.getOffset(), pg.getEndset());
                synchronized (functionListAll) {
                    functionListAll.addAll(functionList);
                }
            });
            futureList.add(completableFuture);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        myThreadPool.shutdown();
        return functionListAll;
    }

    /**
     * 查询所有接口权限(优化)
     * 使用多线程优化查询效率
     *
     * @return 菜单集合
     */
    private List<SysFunctionApiBO> findAllFunctionApi() {
        //查询所有接口权限总条数
        int count = functionApiMapper.findAllFunctionApi();
        System.out.println("接口权限查询(优化):" + count);
        if (count <= 0) {
            return null;
        }
        //每页查询数据条数
        int pageSize = 200;
        //最大页码
        int page = (int) Math.ceil(Arith.div(count, pageSize, 2));
        List<SysFunctionApi> functionApiListAll = new LinkedList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>(page);
        //自定义线程池
        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(page);
        for (int i = 1; i <= page; i++) {
            final PaginationGrid<SysFunctionApi> pg = new PaginationGrid<>(i, pageSize);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                List<SysFunctionApi> functionApiList = functionApiMapper.findPageFunctionApi(pg.getOffset(), pg.getEndset());
                synchronized (functionApiListAll) {
                    functionApiListAll.addAll(functionApiList);
                }
            });
            futureList.add(completableFuture);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        myThreadPool.shutdown();
        Map<Long, List<SysFunctionApi>> functionMap = functionApiListAll.stream().collect(Collectors.groupingBy(SysFunctionApi::getFunctionId));
        List<SysFunctionApiBO> functionApiBOList = new ArrayList<>();
        for (Map.Entry<Long, List<SysFunctionApi>> entry : functionMap.entrySet()) {
            Long functionId = entry.getKey();
            List<SysFunctionApi> functionApiList = entry.getValue();
            SysFunctionApiBO bo = new SysFunctionApiBO();
            bo.setFunctionId(functionId);
            List<SysApi> apiList = new ArrayList<>();
            for (SysFunctionApi functionApi : functionApiList) {
                if (!CollectionUtils.isEmpty(apiList)) {
                    SysApi sysApi = apiList.stream().filter(o -> o.getId().equals(functionApi.getApiId())).findFirst().orElse(null);
                    if (!ObjectUtils.isEmpty(sysApi)) {
                        //如果接口存在，说明该接口已存在了，不需要重复添加了
                        continue;
                    }
                }
                SysApi sysApi = new SysApi();
                sysApi.setId(functionApi.getApiId());
                sysApi.setApiName(functionApi.getApiName());
                sysApi.setApiDesc(functionApi.getApiDesc());
                sysApi.setApiMethod(functionApi.getApiMethod());
                sysApi.setApiUri(functionApi.getApiUri());
                apiList.add(sysApi);
            }
            bo.setApiList(apiList);
            functionApiBOList.add(bo);
        }
        return functionApiBOList;
    }

    /**
     * 功能接口绑定
     *
     * @param functionId      功能ID
     * @param functionApiList 功能接口信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int bindingApi(long functionId, List<SysFunctionApiPO> functionApiList) {
        //1.删除功能接口
        functionApiMapper.deleteByFunctionId(functionId);
        if (CollectionUtils.isEmpty(functionApiList)) {
            return 1;
        }
        //2.功能接口保存
        return functionApiMapper.insert(functionApiList);
    }

    /**
     * 根据功能编码查询功能信息
     *
     * @param functionCode 功能编码
     * @param language     语言
     * @return 结果
     */
    @Override
    public SysFunction findByCode(String functionCode, String language) {
        return functionMapper.findByCode(functionCode, language);
    }

    /**
     * 根据页面ID查询功能信息
     *
     * @param pageId   页面ID
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysFunction findByPageId(long pageId, String language) {
        return functionMapper.findByPageId(pageId, language);
    }

    /**
     * 根据功能ID查询按钮权限列表
     *
     * @param functionId 功能ID
     * @return 按钮权限集合
     */
    @Override
    public List<SysFunction> findButtonById(long functionId) {
        return functionMapper.findButtonById(functionId);
    }

    /**
     * 根据路由名称查询功能信息
     *
     * @param routeName 路由名称
     * @return 结果
     */
    @Override
    public SysFunction findByRouteName(String routeName) {
        return functionMapper.findByRouteName(routeName);
    }

    /**
     * 导出数据查询
     *
     * @param functionIdList 功能ID集合
     * @return 结果
     */
    @Override
    public List<SysFunctionBO> exportData(List<Long> functionIdList) {
        //功能主信息
        List<SysFunction> functionList = functionMapper.findByIds(functionIdList, LanguageEnum.ZH_CN.getValue());
        if (CollectionUtils.isEmpty(functionList)) {
            return Collections.emptyList();
        }
        //查询子功能
        List<SysFunction> childrenList = functionMapper.findByParentIds(functionIdList, LanguageEnum.ZH_CN.getValue());
        if (!CollectionUtils.isEmpty(childrenList)) {
            functionList.addAll(childrenList);
            functionIdList.addAll(childrenList.stream().map(SysFunction::getFunctionId).collect(Collectors.toList()));
        }
        //将SysFunction对象转成SysFunctionBO对象
        List<SysFunctionBO> functionBOList = functionList.stream().map(o -> SysFunctionConvert.INSTANCE.functionToBo(o)).collect(Collectors.toList());
        //功能多语言信息
        List<SysFunctionTl> functionTlList = functionTlMapper.findByFunctionIds(functionIdList);
        for (SysFunctionBO sysFunctionBO : functionBOList) {
            //查找功能多语言信息
            List<SysFunctionTl> tlList = functionTlList.stream().filter(o -> o.getFunctionId().equals(sysFunctionBO.getFunctionId())).collect(Collectors.toList());
            sysFunctionBO.setFunctionTlList(tlList);
        }
        //查询功能与接口权限集合
        List<SysFunctionApiBO> functionApiList = functionApiMapper.findByFunctionIds(functionIdList);
        if (!CollectionUtils.isEmpty(functionApiList)) {
            for (SysFunctionBO sysFunctionBO : functionBOList) {
                //查找功能绑定的接口信息
                SysFunctionApiBO sysFunctionApi = functionApiList.stream().filter(o -> o.getFunctionId().equals(sysFunctionBO.getFunctionId())).findFirst().orElse(null);
                if (ObjectUtils.isEmpty(sysFunctionApi)) {
                    continue;
                }
                //功能绑定的接口列表
                List<SysApi> apiList = sysFunctionApi.getApiList();
                if (!CollectionUtils.isEmpty(apiList)) {
                    sysFunctionBO.setApiList(apiList);
                }
            }
        }
        return functionBOList;
    }

    /**
     * 导入
     *
     * @param sysFunctionBoListImport 参数
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int importData(List<SysFunctionBO> sysFunctionBoListImport) {
        List<Long> functionIdList = sysFunctionBoListImport.stream().map(SysFunctionBO::getFunctionId).distinct().collect(Collectors.toList());
        //删除功能
        functionMapper.delete(functionIdList);
        //删除功能-多语言
        functionTlMapper.deleteTl(functionIdList);
        //删除功能接口中间表
        functionApiMapper.deleteByFunctionIds(functionIdList);
        for (SysFunctionBO sysFunctionBO : sysFunctionBoListImport) {
            //当前功能绑定的接口列表
            List<SysFunctionApiPO> functionApiList = new ArrayList<>();
            List<SysApi> apiList = sysFunctionBO.getApiList();
            if (!CollectionUtils.isEmpty(apiList)) {
                List<Long> apiIdList = apiList.stream().map(SysApi::getId).collect(Collectors.toList());
                //删除API
                apiMapper.delete(apiIdList);
                //===>>>处理地址一致，但ID不一致的API(开始)<<<===
                //根据method+uri查询接口信息(批量)，查询已存在的是API
                List<SysApi> isApiList = apiMapper.findByMethodAndUris(apiList);
                if (!CollectionUtils.isEmpty(isApiList)) {
                    //构建功能绑定的API列表
                    isApiList.forEach(o -> {
                        SysFunctionApiPO functionApi = new SysFunctionApiPO();
                        functionApi.setFunctionId(sysFunctionBO.getFunctionId());
                        functionApi.setApiId(o.getId());
                        functionApi.setCreateBy(sysFunctionBO.getCreateBy());
                        functionApi.setCreateDate(sysFunctionBO.getCreateDate());
                        functionApiList.add(functionApi);
                    });
                    List<String> methodUriList = isApiList.stream().map(o -> o.getApiMethod() + "_" + o.getApiUri()).collect(Collectors.toList());
                    //删除已存在的API
                    apiList.removeIf(o -> methodUriList.contains(o.getApiMethod() + "_" + o.getApiUri()));
                }
                //===>>>处理地址一致，但ID不一致的API(结束)<<<===
                if (!CollectionUtils.isEmpty(apiList)) {
                    //保存API
                    for (SysApi sysApi : apiList) {
                        int result = apiMapper.insert(sysApi);
                        if (result <= 0) {
                            throw new BizException("导入失败(API导入失败)");
                        }
                    }
                    //构建功能绑定的API列表
                    apiList.forEach(o -> {
                        SysFunctionApiPO functionApi = new SysFunctionApiPO();
                        functionApi.setFunctionId(sysFunctionBO.getFunctionId());
                        functionApi.setApiId(o.getId());
                        functionApi.setCreateBy(sysFunctionBO.getCreateBy());
                        functionApi.setCreateDate(sysFunctionBO.getCreateDate());
                        functionApiList.add(functionApi);
                    });
                }
            }
            SysFunction function = SysFunctionConvert.INSTANCE.boToFunction(sysFunctionBO);
            //功能主数据
            int result = functionMapper.insert(function);
            if (result <= 0) {
                throw new BizException("导入失败(功能主数据导入失败)");
            }
            //功能多语言数据
            result = functionTlMapper.insertTl(sysFunctionBO.getFunctionTlList());
            if (result <= 0) {
                throw new BizException("导入失败(功能多语言数据导入失败)");
            }
            if (!CollectionUtils.isEmpty(functionApiList)) {
                //功能API数据
                result += functionApiMapper.insert(functionApiList);
                if (result <= 0) {
                    throw new BizException("导入失败(功能API数据导入失败)");
                }
            }
        }
        return 1;
    }

    /**
     * 根据功能ID查询api接口(批量)
     *
     * @param functionIds 功能ID集合
     * @return uri接口
     */
    @Override
    public List<FunctionApiUri> getApiUris(List<Long> functionIds) {
        List<SysFunction> functionList = functionMapper.findByIds(functionIds, LanguageEnum.ZH_CN.getValue());
        if (CollectionUtils.isEmpty(functionList)) {
            return null;
        }
        //查询所有子功能数据
        List<SysFunction> childrenAll = functionMapper.findByParentIds(null, LanguageEnum.ZH_CN.getValue());
        //接口权限查询
        List<SysFunctionApiBO> functionApiList = functionApiMapper.findByFunctionIds(null);
        for (SysFunction function : functionList) {
            //递归查找子功能，并设置接口权限
            setChildren(Collections.singletonList(function), childrenAll, functionApiList);
        }
        List<FunctionApiUri> apiUriList = new ArrayList<>();
        for (SysFunction sysFunction : functionList) {
            //递归查找API
            FunctionApiUri apiUri = getApiUri(sysFunction);
            if (!ObjectUtils.isEmpty(apiUri)) {
                apiUriList.add(apiUri);
            }
        }
        return apiUriList;
    }

    /**
     * 递归查找API
     *
     * @param function 功能信息
     * @return uri集合
     */
    private FunctionApiUri getApiUri(SysFunction function) {
        if (ObjectUtils.isEmpty(function)) {
            return null;
        }
        FunctionApiUri functionApiUri = new FunctionApiUri();
        functionApiUri.setFunctionId(function.getFunctionId());
        List<SysApi> apiList = function.getApiList();
        if (!CollectionUtils.isEmpty(apiList)) {
            for (SysApi sysApi : apiList) {
                //请求方式
                String method = sysApi.getApiMethod().toLowerCase();
                //接口地址
                String uri = sysApi.getApiUri();
                Set<String> setUri = new HashSet<>();
                setUri.add(uri);
                Map<String, Set<String>> uriMap = functionApiUri.getUriMap();
                if (ObjectUtils.isEmpty(uriMap)) {
                    uriMap = new HashMap<>();
                    uriMap.put(method, setUri);
                    functionApiUri.setUriMap(uriMap);
                    continue;
                }
                Set<String> setUri2 = uriMap.get(method);
                if (ObjectUtils.isEmpty(setUri2)) {
                    uriMap.put(method, setUri);
                    continue;
                }
                setUri2.add(uri);
            }
        }
        List<SysFunction> children = function.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            List<FunctionApiUri> childrenApiUri = new ArrayList<>();
            for (SysFunction sysFunction : children) {
                FunctionApiUri apiUri = getApiUri(sysFunction);
                if (!ObjectUtils.isEmpty(apiUri)) {
                    childrenApiUri.add(apiUri);
                }
            }
            if (!CollectionUtils.isEmpty(childrenApiUri)) {
                Map<String, Set<String>> uriMap = functionApiUri.getUriMap();
                if (ObjectUtils.isEmpty(uriMap)) {
                    uriMap = new HashMap<>();
                    functionApiUri.setUriMap(uriMap);
                }
                for (FunctionApiUri apiUri : childrenApiUri) {
                    Map<String, Set<String>> map = apiUri.getUriMap();
                    if (ObjectUtils.isEmpty(map)) {
                        continue;
                    }
                    for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
                        Set<String> value = uriMap.get(entry.getKey());
                        if (ObjectUtils.isEmpty(value)) {
                            uriMap.put(entry.getKey(), entry.getValue());
                            continue;
                        }
                        value.addAll(entry.getValue());
                    }
                }
            }
        }
        return functionApiUri;
    }
}
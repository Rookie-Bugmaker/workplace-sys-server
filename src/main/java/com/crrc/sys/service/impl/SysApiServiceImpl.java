package com.crrc.sys.service.impl;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.api.SysApi;
import com.crrc.sys.entity.api.SysApiFindByPageParam;
import com.crrc.sys.mapper.mapperImpl.SysApiMapperImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.crrc.sys.mapper.SysFunctionApiMapper;
import com.crrc.sys.service.SysApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-09-14
 */
@Service
public class SysApiServiceImpl implements SysApiService {

    @Autowired
    private SysApiMapperImpl apiMapper;
    @Resource
    private SysFunctionApiMapper functionApiMapper;

    /**
     * API分页查询
     *
     * @param param 查询条件
     * @return 结果
     */
    @Override
    public PaginationGrid<SysApi> findByPage(SysApiFindByPageParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<SysApi> rows = apiMapper.findByFuzzy(param);
        PageInfo<SysApi> pageInfo = new PageInfo<>(rows);
        return PaginationGrid.success(pageInfo);
    }

    /**
     * API新增
     *
     * @param api API信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int add(SysApi api) {
        return apiMapper.insert(api);
    }

    /**
     * API修改
     *
     * @param api API信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SysApi api) {
        return apiMapper.update(api);
    }

    /**
     * API删除
     *
     * @param ids API信息ID集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(List<Long> ids) {
        //功能API删除
        functionApiMapper.deleteByApiIds(ids);
        //API删除
        return apiMapper.delete(ids);
    }

    /**
     * 根据ID查询API信息
     *
     * @param id API信息ID
     * @return 结果
     */
    @Override
    public SysApi findById(long id) {
        return apiMapper.findById(id);
    }

    /**
     * 根据ID查询API信息(批量)
     *
     * @param ids API信息ID
     * @return 结果
     */
    @Override
    public List<SysApi> findByIds(List<Long> ids) {
        return apiMapper.findByIds(ids);
    }

    /**
     * 根据URI查询接口信息
     *
     * @param uri 接口地址
     * @return 结果
     */
    @Override
    public SysApi findByUri(String uri) {
        return apiMapper.findByUri(uri);
    }

    /**
     * 根据请求类型+请求地址查询API信息
     *
     * @param apiMethod 请求类型
     * @param apiUri    请求地址
     * @return API信息
     */
    @Override
    public SysApi findByMethodAndUri(String apiMethod, String apiUri) {
        return apiMapper.findByMethodAndUri(apiMethod, apiUri);
    }

    /**
     * 查询所有API信息
     *
     * @return API集合
     */
    @Override
    public List<SysApi> findAll() {
        return apiMapper.findAll();
    }

    /**
     * 导入
     *
     * @param apiListImport API信息列表
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int importList(List<SysApi> apiListImport) {
        List<Long> apiIdList = apiListImport.stream().map(SysApi::getId).collect(Collectors.toList());
        List<SysApi> sysApiList = apiMapper.findByIds(apiIdList);
        //需要做修改的集合
        List<SysApi> updateList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sysApiList)) {
            //数据库已存在的ID集合
            List<Long> collect = sysApiList.stream().map(SysApi::getId).collect(Collectors.toList());
            //需要做修改的API
            SysApi updateApi = apiListImport.stream().filter(o -> collect.contains(o.getId())).findFirst().orElse(null);
            updateList.add(updateApi);
        }
        //删除需要修改的，剩下的就是需要新增的
        apiListImport.removeIf(updateList::contains);
        int result = 0;
        for (SysApi api : updateList) {
            result += apiMapper.update(api);
        }
        for (SysApi api : apiListImport) {
            result += apiMapper.insert(api);
        }
        return result;
    }

    /**
     * 根据URI查询API信息(批量)
     *
     * @param uriList 参数
     * @return 结果
     */
    @Override
    public List<SysApi> findByUris(List<String> uriList) {
        return apiMapper.findByUris(uriList);
    }
}
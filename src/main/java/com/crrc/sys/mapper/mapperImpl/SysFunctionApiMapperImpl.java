package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.MyBatisUtil;
import com.crrc.sys.entity.api.SysApi;
import com.crrc.sys.entity.api.SysApiDBEntity;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.auth.SysDataAuthValueDBEntity;
import com.crrc.sys.entity.function.SysFunctionApi;
import com.crrc.sys.entity.function.SysFunctionApiBO;
import com.crrc.sys.entity.function.SysFunctionApiDBEntity;
import com.crrc.sys.entity.function.SysFunctionApiPO;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.sys.mapper.IdDesignMapper;
import com.crrc.sys.mapper.SysFunctionApiMapper;
import com.crrc.sys.mapper.mapperService.SysApiMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionApiMapperService;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能API关系表-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-17
 */
@Repository
public class SysFunctionApiMapperImpl extends ServiceImpl<SysFunctionApiMapperService, SysFunctionApiDBEntity> {

    @Resource
    private SysFunctionApiMapperService sysFunctionApiService;

    @Resource
    private SysApiMapperService sysApiMapperService;


    public int deleteByFunctionId(Long functionId) {
        LambdaUpdateWrapper<SysFunctionApiDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysFunctionApiDBEntity::getFunctionId, functionId);
        return sysFunctionApiService.delete(wrapper);
    }


    public int deleteByFunctionIds(List<Long> functionIdList) {
        LambdaUpdateWrapper<SysFunctionApiDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionApiDBEntity::getFunctionId, functionIdList);
        return sysFunctionApiService.delete(wrapper);
    }


    public int insert(List<SysFunctionApiPO> functionApiList) {
        List<SysFunctionApiDBEntity> sysFunctionApiDBEntities = convertToDB(functionApiList);
        return super.saveBatch(sysFunctionApiDBEntities) ? sysFunctionApiDBEntities.size() : 0;
    }


    public List<SysFunctionApiBO> findByFunctionIds(List<Long> functionIdList) {
        MPJLambdaWrapper<SysApiDBEntity> wrapper = JoinWrappers.lambda(SysApiDBEntity.class)
                .selectAs(SysFunctionApiDBEntity::getFunctionId, SysFunctionApiBO::getFunctionId)
                .selectAll(SysApiDBEntity.class)
                .selectCollection(SysApiDBEntity.class, SysFunctionApiBO::getApiList,
                        map -> map.result(SysApiDBEntity::getApiId, SysApi::getId)
                                .result(SysApiDBEntity::getApiName, SysApi::getApiName)
                                .result(SysApiDBEntity::getApiUri, SysApi::getApiUri)
                                .result(SysApiDBEntity::getApiMethod, SysApi::getApiMethod)
                                .result(SysApiDBEntity::getApiDesc, SysApi::getApiDesc)
                )
                .innerJoin(SysFunctionApiDBEntity.class, SysFunctionApiDBEntity::getApiId, SysApiDBEntity::getApiId);
        if (functionIdList != null && !functionIdList.isEmpty()) {
            wrapper.in(SysFunctionApiDBEntity::getFunctionId, functionIdList);
        }
        return sysApiMapperService.selectJoinList(SysFunctionApiBO.class, wrapper);
    }


    public SysFunctionApiBO findByFunctionId(Long functionId) {
        MPJLambdaWrapper<SysApiDBEntity> wrapper = JoinWrappers.lambda(SysApiDBEntity.class)
                .selectAs(SysFunctionApiDBEntity::getFunctionId, SysFunctionApiBO::getFunctionId)
                .selectAll(SysApiDBEntity.class)
                .selectCollection(SysApiDBEntity.class, SysFunctionApiBO::getApiList,
                        map -> map.result(SysApiDBEntity::getApiId, SysApi::getId)
                                .result(SysApiDBEntity::getApiName, SysApi::getApiName)
                                .result(SysApiDBEntity::getApiUri, SysApi::getApiUri)
                                .result(SysApiDBEntity::getApiMethod, SysApi::getApiMethod)
                                .result(SysApiDBEntity::getApiDesc, SysApi::getApiDesc)
                )
                .innerJoin(SysFunctionApiDBEntity.class, SysFunctionApiDBEntity::getApiId, SysApiDBEntity::getApiId)
                .eq(SysFunctionApiDBEntity::getFunctionId, functionId);
        return sysApiMapperService.selectJoinOne(SysFunctionApiBO.class, wrapper);
    }


    public int findAllFunctionApi() {
        MPJLambdaWrapper<SysApiDBEntity> wrapper = JoinWrappers.lambda(SysApiDBEntity.class)
                .select(SysFunctionApiDBEntity::getApiId)
                .leftJoin(SysFunctionApiDBEntity.class, SysFunctionApiDBEntity::getApiId, SysApiDBEntity::getApiId);
        return sysApiMapperService.selectCount(wrapper).intValue();
    }


    public List<SysFunctionApi> findPageFunctionApi(Integer offset, Integer endset) {
        Page<SysFunctionApi> page = MyBatisUtil.convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysApiDBEntity> wrapper = JoinWrappers.lambda(SysApiDBEntity.class)
                .selectAs(SysFunctionApiDBEntity::getFunctionId, SysFunctionApiBO::getFunctionId)
                .selectAs(SysApiDBEntity::getApiId, SysFunctionApi::getApiId)
                .select(SysApiDBEntity::getApiName)
                .select(SysApiDBEntity::getApiUri)
                .select(SysApiDBEntity::getApiMethod)
                .select(SysApiDBEntity::getApiDesc)
                .innerJoin(SysFunctionApiDBEntity.class, SysFunctionApiDBEntity::getApiId, SysApiDBEntity::getApiId)
                .orderByAsc(SysApiDBEntity::getApiId);
        Page<SysFunctionApi> sysFunctionApiPage = sysApiMapperService.selectJoinPage(page, SysFunctionApi.class, wrapper);
        return sysFunctionApiPage.getRecords();
    }


    public int deleteByApiIds(List<Long> ids) {
        LambdaUpdateWrapper<SysFunctionApiDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionApiDBEntity::getApiId, ids);
        return sysFunctionApiService.delete(wrapper);
    }

    private List<SysFunctionApiDBEntity> convertToDB(List<SysFunctionApiPO> dataList) {
        List<SysFunctionApiDBEntity> entityList = BeanUtils.toBean(dataList, SysFunctionApiDBEntity.class);
        return entityList;
    }
}
package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
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
 * 功能API关系表-mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-17
 */
@Repository
public class SysFunctionApiMapperImpl extends ServiceImpl<SysFunctionApiMapperService, SysFunctionApiDBEntity> implements SysFunctionApiMapper {

    @Resource
    private SysFunctionApiMapperService sysFunctionApiService;

    @Resource
    private SysApiMapperService sysApiMapperService;

    @Override
    public int deleteByFunctionId(Long functionId) {
        LambdaUpdateWrapper<SysFunctionApiDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysFunctionApiDBEntity::getFunctionId, functionId);
        return sysFunctionApiService.delete(wrapper);
    }

    @Override
    public int deleteByFunctionIds(List<Long> functionIdList) {
        LambdaUpdateWrapper<SysFunctionApiDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionApiDBEntity::getFunctionId, functionIdList);
        return sysFunctionApiService.delete(wrapper);
    }

    @Override
    public int insert(List<SysFunctionApiPO> functionApiList) {
        List<SysFunctionApiDBEntity> sysFunctionApiDBEntities = convertToDB(functionApiList);
        return super.saveBatch(sysFunctionApiDBEntities) ? sysFunctionApiDBEntities.size() : 0;
    }

    @Override
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

    @Override
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

    @Override
    public int findAllFunctionApi() {
        MPJLambdaWrapper<SysApiDBEntity> wrapper = JoinWrappers.lambda(SysApiDBEntity.class)
                .select(SysFunctionApiDBEntity::getApiId)
                .leftJoin(SysFunctionApiDBEntity.class, SysFunctionApiDBEntity::getApiId, SysApiDBEntity::getApiId);
        return sysApiMapperService.selectCount(wrapper).intValue();
    }

    @Override
    public List<SysFunctionApi> findPageFunctionApi(Integer offset, Integer endset) {
        Page<SysFunctionApi> page = convertToPage(offset, endset);
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

    @Override
    public int deleteByApiIds(List<Long> ids) {
        LambdaUpdateWrapper<SysFunctionApiDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionApiDBEntity::getApiId, ids);
        return sysFunctionApiService.delete(wrapper);
    }

    private List<SysFunctionApiDBEntity> convertToDB(List<SysFunctionApiPO> dataList) {
        List<SysFunctionApiDBEntity> entityList = BeanUtils.toBean(dataList, SysFunctionApiDBEntity.class);
        return entityList;
    }

    /**
     * 将 LIMIT offset, size 参数转换为 MyBatis-Plus 的 Page 对象
     * @param offset 偏移量（从0开始）
     * @param size 每页记录数
     * @return MyBatis-Plus 的 Page 对象
     */
    private <T> Page<T> convertToPage(long offset, long size) {
        // 计算当前页码（注意避免除零）
        if (size <= 0) {
            throw new IllegalArgumentException("每页记录数必须大于0");
        }

        long current = (offset / size) + 1;

        // 创建并返回 Page 对象
        return new Page<>(current, size);
    }
}
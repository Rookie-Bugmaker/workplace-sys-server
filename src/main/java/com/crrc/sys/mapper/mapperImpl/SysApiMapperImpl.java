package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.api.*;
import com.crrc.sys.mapper.SysApiAuthMapper;
import com.crrc.sys.mapper.SysApiMapper;
import com.crrc.sys.mapper.mapperService.SysApiAuthMapperService;
import com.crrc.sys.mapper.mapperService.SysApiMapperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-13
 */
@Repository
public class SysApiMapperImpl implements SysApiMapper {

    @Resource
    private SysApiMapperService sysApiMapperService;

    @Override
    public List<SysApi> findByFuzzy(SysApiFindByPageParam param) {
        LambdaQueryWrapper<SysApiDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(param.getApiName()),
                        SysApiDBEntity::getApiName, param.getApiName())
                .like(StringUtils.isNotBlank(param.getApiUri()),
                        SysApiDBEntity::getApiUri, param.getApiUri())
                .like(StringUtils.isNotBlank(param.getApiDesc()),
                        SysApiDBEntity::getApiDesc, param.getApiDesc());
        List<SysApiDBEntity> list = sysApiMapperService.selectList(wrapper);
        return convertFromDB(list);
    }

    @Override
    public int insert(SysApi api) {
        SysApiDBEntity sysApiDBEntity = convertToDB(api);
        return sysApiMapperService.insert(sysApiDBEntity);
    }

    @Override
    public int update(SysApi api) {
        LambdaUpdateWrapper<SysApiDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysApiDBEntity::getApiName, api.getApiName())
                .set(SysApiDBEntity::getApiUri, api.getApiUri())
                .set(SysApiDBEntity::getApiMethod, api.getApiMethod())
                .set(SysApiDBEntity::getApiDesc, api.getApiDesc())
                .set(SysApiDBEntity::getLastUpdateBy, api.getLastUpdateBy())
                .set(SysApiDBEntity::getLastUpdateDate, api.getLastUpdateDate())
                .set(SysApiDBEntity::getLastUpdateLogin, api.getLastUpdateLogin())
                .eq(SysApiDBEntity::getApiId, api.getId());
        return sysApiMapperService.update(wrapper);
    }

    @Override
    public int delete(List<Long> ids) {
        LambdaUpdateWrapper<SysApiDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysApiDBEntity::getApiId, ids);
        return sysApiMapperService.delete(wrapper);
    }

    @Override
    public SysApi findById(long id) {
        SysApiDBEntity entity = sysApiMapperService.selectById(id);
        return convertFromDB(entity);
    }

    @Override
    public List<SysApi> findByIds(List<Long> ids) {
        List<SysApiDBEntity> sysApiDBEntityList = sysApiMapperService.selectBatchIds(ids);
        return convertFromDB(sysApiDBEntityList);
    }

    @Override
    public SysApi findByUri(String uri) {
        LambdaQueryWrapper<SysApiDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysApiDBEntity::getApiUri, uri);
        SysApiDBEntity sysApiDBEntity = sysApiMapperService.selectOne(wrapper);
        return convertFromDB(sysApiDBEntity);
    }

    @Override
    public SysApi findByMethodAndUri(String apiMethod, String apiUri) {
        LambdaQueryWrapper<SysApiDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysApiDBEntity::getApiUri, apiUri)
                .eq(SysApiDBEntity::getApiMethod, apiMethod);
        SysApiDBEntity sysApiDBEntity = sysApiMapperService.selectOne(wrapper);
        return convertFromDB(sysApiDBEntity);
    }

    @Override
    public List<SysApi> findAll() {
        List<SysApiDBEntity> sysApiDBEntityList = sysApiMapperService.selectList(new LambdaQueryWrapper<>());
        return convertFromDB(sysApiDBEntityList);
    }

    @Override
    public List<SysApi> findByMethodAndUris(List<SysApi> apiList) {
        List<SysApi> sysApiList= new ArrayList<>();
        apiList.forEach(sysApi -> {
            LambdaQueryWrapper<SysApiDBEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysApiDBEntity::getApiUri, sysApi.getApiUri())
                    .eq(SysApiDBEntity::getApiMethod, sysApi.getApiMethod());
            List<SysApi> convert = convertFromDB(sysApiMapperService.selectList(wrapper));
            sysApiList.addAll(convert);
        });
        return sysApiList;
    }

    @Override
    public List<SysApi> findByUris(List<String> apiUriList) {
        LambdaQueryWrapper<SysApiDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysApiDBEntity::getApiUri, apiUriList);
        List<SysApiDBEntity> sysApiDBEntityList = sysApiMapperService.selectList(wrapper);
        return convertFromDB(sysApiDBEntityList);
    }

    private SysApi convertFromDB(SysApiDBEntity sysApiDBEntity) {
        SysApi sysApi = BeanUtils.toBean(sysApiDBEntity, SysApi.class);
        sysApi.setId(sysApiDBEntity.getApiId());
        return sysApi;
    }

    private List<SysApiDBEntity> convertToDB(List<SysApi> sysApiList) {
        List<SysApiDBEntity> sysApiDBEntityList = new ArrayList<>();
        sysApiList.forEach(sysApi -> {
            SysApiDBEntity sysApiDBEntity = BeanUtils.toBean(sysApi, SysApiDBEntity.class);
            sysApiDBEntity.setApiId(sysApi.getId());
            sysApiDBEntityList.add(sysApiDBEntity);
        });
        return sysApiDBEntityList;
    }

    private SysApiDBEntity convertToDB(SysApi sysApi) {
        SysApiDBEntity sysApiDBEntity = BeanUtils.toBean(sysApi, SysApiDBEntity.class);
        sysApiDBEntity.setApiId(sysApi.getId());
        return sysApiDBEntity;
    }

    private List<SysApi> convertFromDB(List<SysApiDBEntity> sysApiDBEntityList) {
        List<SysApi> sysApiList = new ArrayList<>();
        sysApiDBEntityList.forEach(sysApiDBEntity -> {
            SysApi sysApi = BeanUtils.toBean(sysApiDBEntity, SysApi.class);
            sysApi.setId(sysApiDBEntity.getApiId());
            sysApiList.add(sysApi);
        });
        return sysApiList;
    }
    
    
}
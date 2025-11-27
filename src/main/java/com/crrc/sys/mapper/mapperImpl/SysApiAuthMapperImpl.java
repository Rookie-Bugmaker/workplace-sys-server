package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.api.SysApiAuth;
import com.crrc.sys.entity.api.SysApiAuthDBEntity;
import com.crrc.sys.entity.api.SysApiAuthPageDTO;
import com.crrc.sys.entity.function.SysFunctionApiDBEntity;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.mapper.SysApiAuthMapper;
import com.crrc.sys.mapper.mapperService.SysApiAuthMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionApiMapperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * mapper实现类，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-13
 */
@Repository
public class SysApiAuthMapperImpl extends ServiceImpl<SysApiAuthMapperService, SysApiAuthDBEntity> {

    @Resource
    private SysApiAuthMapperService sysApiAuthMapperService;


    public List<SysApiAuth> findByFuzzy(SysApiAuthPageDTO param) {
        LambdaQueryWrapper<SysApiAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(param.getAuthUri()),
                SysApiAuthDBEntity::getAuthUri, param.getAuthUri())
                .like(StringUtils.isNotBlank(param.getAuthType()),
                        SysApiAuthDBEntity::getAuthType, param.getAuthType())
                .like(StringUtils.isNotBlank(param.getAuthDesc()),
                        SysApiAuthDBEntity::getAuthDesc, param.getAuthDesc());
        List<SysApiAuthDBEntity> list = sysApiAuthMapperService.selectList(wrapper);
        return BeanUtils.toBean(list, SysApiAuth.class);
    }

    public IPage<SysApiAuth> findByFuzzyPage(SysApiAuthPageDTO param) {
        LambdaQueryWrapper<SysApiAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(param.getAuthUri()),
                        SysApiAuthDBEntity::getAuthUri, param.getAuthUri())
                .like(StringUtils.isNotBlank(param.getAuthType()),
                        SysApiAuthDBEntity::getAuthType, param.getAuthType())
                .like(StringUtils.isNotBlank(param.getAuthDesc()),
                        SysApiAuthDBEntity::getAuthDesc, param.getAuthDesc());
        Page<SysApiAuthDBEntity> midPage = sysApiAuthMapperService.selectPage(
                new Page<>(param.getPage(), param.getPageSize()), wrapper);
        IPage<SysApiAuth> resultPage = new Page<>(midPage.getCurrent(), midPage.getSize(), midPage.getTotal());
        resultPage.setRecords(BeanUtils.toBean(midPage.getRecords(), SysApiAuth.class));
        return resultPage;
    }


    public int add(SysApiAuth apiAuth) {
        SysApiAuthDBEntity entity = BeanUtils.toBean(apiAuth, SysApiAuthDBEntity.class);
        return sysApiAuthMapperService.insert(entity);
    }


    public List<SysApiAuth> findByAuthType(String authType) {
        LambdaQueryWrapper<SysApiAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(authType), SysApiAuthDBEntity::getAuthType, authType);
        List<SysApiAuthDBEntity> list = sysApiAuthMapperService.selectList(wrapper);
        return BeanUtils.toBean(list, SysApiAuth.class);
    }


    public int update(SysApiAuth apiAuth) {
        LambdaUpdateWrapper<SysApiAuthDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysApiAuthDBEntity::getAuthUri, apiAuth.getAuthUri())
                .set(SysApiAuthDBEntity::getAuthType, apiAuth.getAuthType())
                .set(SysApiAuthDBEntity::getAuthDesc, apiAuth.getAuthDesc())
                .set(SysApiAuthDBEntity::getLastUpdateBy, apiAuth.getLastUpdateBy())
                .set(SysApiAuthDBEntity::getLastUpdateDate, apiAuth.getLastUpdateDate())
                .set(SysApiAuthDBEntity::getLastUpdateLogin, apiAuth.getLastUpdateLogin())
                .eq(SysApiAuthDBEntity::getAuthId, apiAuth.getAuthId());
        return sysApiAuthMapperService.update(wrapper);
    }


    public int delete(List<Long> ids) {
        LambdaUpdateWrapper<SysApiAuthDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysApiAuthDBEntity::getAuthId, ids);
        return sysApiAuthMapperService.delete(wrapper);
    }


    public SysApiAuth findById(long authId) {
        SysApiAuthDBEntity entity = sysApiAuthMapperService.selectById(authId);
        return BeanUtils.toBean(entity, SysApiAuth.class);
    }


    public List<SysApiAuth> findByAuthIdList(List<Long> authIdList) {
        LambdaQueryWrapper<SysApiAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysApiAuthDBEntity::getAuthId, authIdList);
        List<SysApiAuthDBEntity> list = sysApiAuthMapperService.selectList(wrapper);
        return BeanUtils.toBean(list, SysApiAuth.class);
    }
}
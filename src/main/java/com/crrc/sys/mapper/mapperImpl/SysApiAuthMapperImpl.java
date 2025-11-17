package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.api.SysApiAuth;
import com.crrc.sys.entity.api.SysApiAuthDBEntity;
import com.crrc.sys.entity.api.SysApiAuthPageDTO;
import com.crrc.sys.mapper.SysApiAuthMapper;
import com.crrc.sys.mapper.mapperService.SysApiAuthMapperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-13
 */
@Repository
public class SysApiAuthMapperImpl implements SysApiAuthMapper {

    @Resource
    private SysApiAuthMapperService sysApiAuthMapperService;

    @Override
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

    @Override
    public int add(SysApiAuth apiAuth) {
        SysApiAuthDBEntity entity = BeanUtils.toBean(apiAuth, SysApiAuthDBEntity.class);
        return sysApiAuthMapperService.insert(entity);
    }

    @Override
    public List<SysApiAuth> findByAuthType(String authType) {
        LambdaQueryWrapper<SysApiAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(authType), SysApiAuthDBEntity::getAuthType, authType);
        List<SysApiAuthDBEntity> list = sysApiAuthMapperService.selectList(wrapper);
        return BeanUtils.toBean(list, SysApiAuth.class);
    }

    @Override
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

    @Override
    public int delete(List<Long> ids) {
        LambdaUpdateWrapper<SysApiAuthDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysApiAuthDBEntity::getAuthId, ids);
        return sysApiAuthMapperService.delete(wrapper);
    }

    @Override
    public SysApiAuth findById(long authId) {
        SysApiAuthDBEntity entity = sysApiAuthMapperService.selectById(authId);
        return BeanUtils.toBean(entity, SysApiAuth.class);
    }

    @Override
    public List<SysApiAuth> findByAuthIdList(List<Long> authIdList) {
        LambdaQueryWrapper<SysApiAuthDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysApiAuthDBEntity::getAuthId, authIdList);
        List<SysApiAuthDBEntity> list = sysApiAuthMapperService.selectList(wrapper);
        return BeanUtils.toBean(list, SysApiAuth.class);
    }
}
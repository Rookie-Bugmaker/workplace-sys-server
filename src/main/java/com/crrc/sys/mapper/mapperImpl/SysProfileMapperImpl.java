package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.profile.*;
import com.crrc.sys.mapper.SysProfileMapper;
import com.crrc.sys.mapper.mapperService.SysProfileBMapperService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 配置文件-mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * 李江涛
 * 2025-11-17
 */
@Repository
public class SysProfileMapperImpl extends ServiceImpl<SysProfileBMapperService, SysProfileBDBEntity> implements SysProfileMapper {

    @Resource
    private SysProfileBMapperService sysProfileBMapperService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<SysProfile> findByFuzzy(SysProfileFindByPageParam param) {
        MPJLambdaWrapper<SysProfileBDBEntity> wrapper = new MPJLambdaWrapper<SysProfileBDBEntity>()
                .selectAll(SysProfileBDBEntity.class, SysProfileBDBEntity::getProfileFieldAlias)
                .selectAs(SysProfileBDBEntity::getProfileFieldAlias, SysProfile::getFieldAlias)
                .select(SysProfileTlDBEntity::getProfileName)
                .innerJoin(SysProfileTlDBEntity.class,
                        fn -> fn.eq(SysProfileTlDBEntity::getProfileId, SysProfileBDBEntity::getProfileId)
                                .eq(SysProfileTlDBEntity::getLanguage, param.getLanguage())
                );
        if (param.getProfileCode() != null && !param.getProfileCode().isEmpty()) {
            wrapper.like(SysProfileBDBEntity::getProfileCode, param.getProfileCode());
        }
        if (param.getProfileName() != null && !param.getProfileName().isEmpty()) {
            wrapper.like(SysProfileTlDBEntity::getProfileName, param.getProfileName());
        }
        if (param.getProfileType() != null && !param.getProfileType().isEmpty()) {
            wrapper.eq(SysProfileBDBEntity::getProfileType, param.getProfileType());
        }
        wrapper.orderByAsc(SysProfileBDBEntity::getProfileId);
        return sysProfileBMapperService.selectJoinList(SysProfile.class, wrapper);
    }

    @Override
    public int insert(SysProfile profile) {
        SysProfileBDBEntity entity = convertToDB(profile);
        return sysProfileBMapperService.insert(entity);
    }

    @Override
    public int update(SysProfile profile) {
        SysProfileBDBEntity entity = convertToDB(profile);
        LambdaUpdateWrapper<SysProfileBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysProfileBDBEntity::getProfileId, profile.getProfileId())
                .set(SysProfileBDBEntity::getProfileCode, entity.getProfileCode())
                .set(SysProfileBDBEntity::getProfileSourceType, entity.getProfileSourceType())
                .set(SysProfileBDBEntity::getProfileSource, entity.getProfileSource())
                .set(SysProfileBDBEntity::getProfileValueColumn, entity.getProfileValueColumn())
                .set(SysProfileBDBEntity::getProfileCodeColumn, entity.getProfileCodeColumn())
                .set(SysProfileBDBEntity::getProfileDescColumn, entity.getProfileDescColumn())
                .set(SysProfileBDBEntity::getProfileParentColumn, entity.getProfileParentColumn())
                .set(SysProfileBDBEntity::getProfileComponentType, entity.getProfileComponentType())
                .set(SysProfileBDBEntity::getProfileType, entity.getProfileType())
                .set(SysProfileBDBEntity::getProfileFieldAlias, entity.getProfileFieldAlias())
                .set(SysProfileBDBEntity::getLastUpdateBy, entity.getLastUpdateBy())
                .set(SysProfileBDBEntity::getLastUpdateDate, entity.getLastUpdateDate())
                .set(SysProfileBDBEntity::getLastUpdateLogin, entity.getLastUpdateLogin());

        return sysProfileBMapperService.update(wrapper);
    }

    @Override
    public SysProfile findById(long profileId, String language) {
        MPJLambdaWrapper<SysProfileBDBEntity> wrapper = new MPJLambdaWrapper<SysProfileBDBEntity>()
                .selectAll(SysProfileBDBEntity.class, SysProfileBDBEntity::getProfileFieldAlias)
                .selectAs(SysProfileBDBEntity::getProfileFieldAlias, SysProfile::getFieldAlias)
                .select(SysProfileTlDBEntity::getProfileName)
                .innerJoin(SysProfileTlDBEntity.class,
                        fn -> fn.eq(SysProfileTlDBEntity::getProfileId, SysProfileBDBEntity::getProfileId)
                                .eq(SysProfileTlDBEntity::getLanguage, language)
                )
                .eq(SysProfileBDBEntity::getProfileId, profileId);

        return sysProfileBMapperService.selectJoinOne(SysProfile.class, wrapper);
    }

    @Override
    public int delete(List<Long> profileIdList) {
        LambdaQueryWrapper<SysProfileBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysProfileBDBEntity::getProfileId, profileIdList);
        return sysProfileBMapperService.delete(wrapper);
    }

    @Override
    public SysProfile findByCode(String profileCode) {
        LambdaQueryWrapper<SysProfileBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysProfileBDBEntity::getProfileCode, profileCode);
        SysProfileBDBEntity entity = sysProfileBMapperService.selectOne(wrapper);
        return entity != null ? convertFromDB(entity) : null;
    }

    @Override
    public List<ValueSet> executeSql(SysProfileValuePageSearch search) {
        // 构建动态列选择
        StringJoiner selectColumns = new StringJoiner(",");
        if (StringUtils.isNotBlank(search.getProfileValueColumn())) {
            selectColumns.add(search.getProfileValueColumn() + " AS ENTITY_ID");
        }
        if (StringUtils.isNotBlank(search.getProfileCodeColumn())) {
            selectColumns.add(search.getProfileCodeColumn() + " AS ENTITY_CODE");
        }
        if (StringUtils.isNotBlank(search.getProfileDescColumn())) {
            selectColumns.add(search.getProfileDescColumn() + " AS ENTITY_DESC");
        }
        if (StringUtils.isNotBlank(search.getProfileParentColumn())) {
            selectColumns.add(search.getProfileParentColumn() + " AS ENTITY_PARENT_ID");
        }

        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        // 构建模糊查询条件
        if (StringUtils.isNotBlank(search.getFuzzyQuery())) {
            if (StringUtils.isNotBlank(search.getProfileDescColumn())
                    && StringUtils.isBlank(search.getProfileCodeColumn())) {
                queryWrapper.like(search.getProfileDescColumn(), search.getFuzzyQuery());
            } else if (StringUtils.isNotBlank(search.getProfileCodeColumn())
                    && StringUtils.isBlank(search.getProfileDescColumn())) {
                queryWrapper.like(search.getProfileCodeColumn(), search.getFuzzyQuery());
            } else if (StringUtils.isNotBlank(search.getProfileCodeColumn())
                    && StringUtils.isNotBlank(search.getProfileDescColumn())) {
                queryWrapper.like(search.getProfileDescColumn(), search.getFuzzyQuery())
                        .or()
                        .like(search.getProfileCodeColumn(), search.getFuzzyQuery());
            }
        }
        AtomicReference<String> sqlSegment = new AtomicReference<>(removeOuterParentheses(queryWrapper.getSqlSegment()));
        queryWrapper.getParamNameValuePairs().entrySet().forEach(kv -> {
            String indexString = "#{ew.paramNameValuePairs." + kv.getKey() + "}";
            sqlSegment.set(StringUtils.replace(sqlSegment.get(), indexString, "'" + kv.getValue().toString() + "'"));
        });
        // 设置查询
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("SELECT ").append(selectColumns).append(" FROM (").append(search.getSql()).append(") tb");
        if (StringUtils.isNotBlank(sqlSegment.get())) {
            selectSql.append(" WHERE ").append(sqlSegment.get());
        }
        // 执行查询（使用动态数据源）
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(selectSql.toString());
        return convertValueSetFromDB(maps);
    }

    @Override
    public List<SysProfile> findByType(String profileType, String language) {
        MPJLambdaWrapper<SysProfileBDBEntity> wrapper = new MPJLambdaWrapper<SysProfileBDBEntity>()
                .selectAll(SysProfileBDBEntity.class, SysProfileBDBEntity::getProfileFieldAlias)
                .selectAs(SysProfileBDBEntity::getProfileFieldAlias, SysProfile::getFieldAlias)
                .select(SysProfileTlDBEntity::getProfileName)
                .innerJoin(SysProfileTlDBEntity.class,
                        fn -> fn.eq(SysProfileTlDBEntity::getProfileId, SysProfileBDBEntity::getProfileId)
                                .eq(SysProfileTlDBEntity::getLanguage, language)
                )
                .eq(SysProfileBDBEntity::getProfileType, profileType);

        return sysProfileBMapperService.selectJoinList(SysProfile.class, wrapper);
    }

    @Override
    public List<SysProfile> findByIds(List<Long> profileIdList, String language) {
        MPJLambdaWrapper<SysProfileBDBEntity> wrapper = new MPJLambdaWrapper<SysProfileBDBEntity>()
                .selectAll(SysProfileBDBEntity.class, SysProfileBDBEntity::getProfileFieldAlias)
                .selectAs(SysProfileBDBEntity::getProfileFieldAlias, SysProfile::getFieldAlias)
                .select(SysProfileTlDBEntity::getProfileName)
                .innerJoin(SysProfileTlDBEntity.class,
                        fn -> fn.eq(SysProfileTlDBEntity::getProfileId, SysProfileBDBEntity::getProfileId)
                                .eq(SysProfileTlDBEntity::getLanguage, language)
                )
                .in(SysProfileBDBEntity::getProfileId, profileIdList);

        return sysProfileBMapperService.selectJoinList(SysProfile.class, wrapper);
    }

    private SysProfileBDBEntity convertToDB(SysProfile data) {
        SysProfileBDBEntity entity = BeanUtils.toBean(data, SysProfileBDBEntity.class);
        if (data != null) {
            entity.setProfileFieldAlias(data.getFieldAlias());
        }
        return entity;
    }

    private SysProfile convertFromDB(SysProfileBDBEntity entity) {
        SysProfile data = BeanUtils.toBean(entity, SysProfile.class);
        if (entity != null) {
            data.setFieldAlias(entity.getProfileFieldAlias());
        }
        return data;
    }

    private List<SysProfileBDBEntity> convertToDB(List<SysProfile> dataList) {
        List<SysProfileBDBEntity> entityList = new ArrayList<>();
        dataList.forEach(data -> {
            SysProfileBDBEntity entity = BeanUtils.toBean(data, SysProfileBDBEntity.class);
            if (data != null) {
                entity.setProfileFieldAlias(data.getFieldAlias());
            }
            entityList.add(entity);
        });
        return entityList;
    }

    private List<SysProfile> convertFromDB(List<SysProfileBDBEntity> entityList) {
        List<SysProfile> dataList = new ArrayList<>();
        entityList.forEach(entity -> {
            SysProfile data = BeanUtils.toBean(entity, SysProfile.class);
            if (entity != null) {
                data.setFieldAlias(entity.getProfileFieldAlias());
            }
            dataList.add(data);
        });
        return dataList;
    }

    private List<ValueSet> convertValueSetFromDB(List<Map<String, Object>> entityList) {
        List<ValueSet> dataList = new ArrayList<>();
        entityList.forEach(entity -> {
            ValueSet data = new ValueSet();
            data.setId(entity.get("ENTITY_ID") == null ? null : entity.get("ENTITY_ID").toString());
            data.setCode(entity.get("ENTITY_CODE") == null ? null : entity.get("ENTITY_CODE").toString());
            data.setDesc(entity.get("ENTITY_DESC") == null ? null : entity.get("ENTITY_DESC").toString());
            data.setParentId(entity.get("ENTITY_PARENT_ID") == null ? null : entity.get("ENTITY_PARENT_ID").toString());
            dataList.add(data);
        });
        return dataList;
    }

    private static String removeOuterParentheses(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // 检查首字符是否是'('
        if (str.charAt(0) == '(') {
            str = str.substring(1);
        }

        // 检查尾字符是否是')'
        if (str.length() > 0 && str.charAt(str.length() - 1) == ')') {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }
}
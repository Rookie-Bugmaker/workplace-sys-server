package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.MyBatisUtil;
import com.crrc.sys.entity.function.*;
import com.crrc.sys.entity.function.column.SysFunctionColumn;
import com.crrc.sys.entity.function.column.SysFunctionColumnBDBEntity;
import com.crrc.sys.entity.function.column.SysFunctionColumnTlDBEntity;
import com.crrc.sys.entity.role.SysRoleMenuDBEntity;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.mapper.SysFunctionMapper;
import com.crrc.sys.mapper.mapperService.SysFunctionBMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionColumnBMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionColumnTlMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionTlMapperService;
import com.crrc.sys.model.user.SysUserInfoDBJoinDTO;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统功能-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-17
 */
@Repository
public class SysFunctionMapperImpl extends ServiceImpl<SysFunctionBMapperService, SysFunctionBDBEntity> {

    @Resource
    private SysFunctionTlMapperService sysFunctionTlMapperService;

    @Resource
    private SysFunctionBMapperService sysFunctionBMapperService;


    public List<SysFunction> findByFuzzy(SysFunctionFindByPageParam param) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, param.getLanguage())
                )
                .isNull(SysFunctionBDBEntity::getParentId)
                .eq(param.getProjectId() != null, SysFunctionBDBEntity::getProjectId, param.getProjectId())
                .like(StringUtils.isNotBlank(param.getFunctionCode()), SysFunctionBDBEntity::getFunctionCode, param.getFunctionCode())
                .like(StringUtils.isNotBlank(param.getFunctionName()), SysFunctionTlDBEntity::getFunctionName, param.getFunctionName())
                .like(StringUtils.isNotBlank(param.getPath()), SysFunctionBDBEntity::getPath, param.getPath())
                .like(StringUtils.isNotBlank(param.getParameters()), SysFunctionBDBEntity::getParameters, param.getParameters())
                .like(StringUtils.isNotBlank(param.getComponent()), SysFunctionBDBEntity::getComponent, param.getComponent())
                .like(StringUtils.isNotBlank(param.getName()), SysFunctionBDBEntity::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getRedirect()), SysFunctionBDBEntity::getRedirect, param.getRedirect())
                .like(StringUtils.isNotBlank(param.getFunctionDescription()), SysFunctionTlDBEntity::getFunctionDescription, param.getFunctionDescription());
        if(param.getColumnValueSetMap() != null) {
            param.getColumnValueSetMap().entrySet().forEach(kv -> {
                List<Object> value = kv.getValue();
                String key = kv.getKey();
                if(value != null) {
                    wrapper.in(key, value);
                }
            });
        }
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }

    public IPage<SysFunction> findByFuzzyPage(SysFunctionFindByPageParam param) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, param.getLanguage())
                )
                .isNull(SysFunctionBDBEntity::getParentId)
                .eq(param.getProjectId() != null, SysFunctionBDBEntity::getProjectId, param.getProjectId())
                .like(StringUtils.isNotBlank(param.getFunctionCode()), SysFunctionBDBEntity::getFunctionCode, param.getFunctionCode())
                .like(StringUtils.isNotBlank(param.getFunctionName()), SysFunctionTlDBEntity::getFunctionName, param.getFunctionName())
                .like(StringUtils.isNotBlank(param.getPath()), SysFunctionBDBEntity::getPath, param.getPath())
                .like(StringUtils.isNotBlank(param.getParameters()), SysFunctionBDBEntity::getParameters, param.getParameters())
                .like(StringUtils.isNotBlank(param.getComponent()), SysFunctionBDBEntity::getComponent, param.getComponent())
                .like(StringUtils.isNotBlank(param.getName()), SysFunctionBDBEntity::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getRedirect()), SysFunctionBDBEntity::getRedirect, param.getRedirect())
                .like(StringUtils.isNotBlank(param.getFunctionDescription()), SysFunctionTlDBEntity::getFunctionDescription, param.getFunctionDescription());
        if(param.getColumnValueSetMap() != null) {
            param.getColumnValueSetMap().entrySet().forEach(kv -> {
                List<Object> value = kv.getValue();
                String key = kv.getKey();
                if(value != null) {
                    wrapper.in(key, value);
                }
            });
        }
        Page<SysFunction> page = sysFunctionBMapperService.selectJoinPage(
                MyBatisUtil.makePage(param.getPage(), param.getPageSize()), SysFunction.class, wrapper);
        return page;
    }


    public int insert(SysFunction function) {
        SysFunctionBDBEntity sysFunctionBDBEntity = convertToDB(function);
        return sysFunctionBMapperService.insert(sysFunctionBDBEntity);
    }


    public List<SysFunction> findByParentIds(List<Long> parentIdList, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .in(parentIdList != null, SysFunctionBDBEntity::getParentId, parentIdList)
                .isNotNull(parentIdList == null, SysFunctionBDBEntity::getParentId);
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }


    public int updateTl(SysFunctionTl functionTl) {
        LambdaUpdateWrapper<SysFunctionTlDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysFunctionTlDBEntity::getFunctionName, functionTl.getFunctionName())
                .set(SysFunctionTlDBEntity::getFunctionDescription, functionTl.getFunctionDescription())
                .set(SysFunctionTlDBEntity::getLastUpdateBy, functionTl.getLastUpdateBy())
                .set(SysFunctionTlDBEntity::getLastUpdateDate, functionTl.getLastUpdateDate())
                .set(SysFunctionTlDBEntity::getLastUpdateLogin, functionTl.getLastUpdateLogin())
                .eq(SysFunctionTlDBEntity::getFunctionId, functionTl.getFunctionId())
                .eq(SysFunctionTlDBEntity::getLanguage, functionTl.getLanguage());
        return sysFunctionTlMapperService.update(wrapper);
    }


    public int update(SysFunction function) {
        LambdaUpdateWrapper<SysFunctionBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysFunctionBDBEntity::getParentId, function.getParentId())
                .set(SysFunctionBDBEntity::getFunctionCode, function.getFunctionCode())
                .set(SysFunctionBDBEntity::getFunctionType, function.getFunctionType())
                .set(SysFunctionBDBEntity::getPath, function.getPath())
                .set(SysFunctionBDBEntity::getParameters, function.getParameters())
                .set(SysFunctionBDBEntity::getComponent, function.getComponent())
                .set(SysFunctionBDBEntity::getName, function.getName())
                .set(SysFunctionBDBEntity::getRedirect, function.getRedirect())
                .set(SysFunctionBDBEntity::getHideChildrenInMenu, function.getHideChildrenInMenu())
                .set(SysFunctionBDBEntity::getHideInMenu, function.getHideInMenu())
                .set(SysFunctionBDBEntity::getLockInTab, function.getLockInTab())
                .set(SysFunctionBDBEntity::getMergeTab, function.getMergeTab())
                .set(SysFunctionBDBEntity::getOpenMethod, function.getOpenMethod())
                .set(SysFunctionBDBEntity::getKeepAlive, function.getKeepAlive())
                .set(SysFunctionBDBEntity::getIcon, function.getIcon())
                .set(SysFunctionBDBEntity::getVsId, function.getVsId())
                .set(SysFunctionBDBEntity::getLastUpdateBy, function.getLastUpdateBy())
                .set(SysFunctionBDBEntity::getLastUpdateDate, function.getLastUpdateDate())
                .set(SysFunctionBDBEntity::getLastUpdateLogin, function.getLastUpdateLogin())
                .set(SysFunctionBDBEntity::getProjectId, function.getProjectId())
                .eq(SysFunctionBDBEntity::getFunctionId, function.getFunctionId());
        return sysFunctionBMapperService.update(wrapper);
    }


    public int delete(List<Long> functionIds) {
        LambdaUpdateWrapper<SysFunctionBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionBDBEntity::getFunctionId, functionIds);
        return sysFunctionBMapperService.delete(wrapper);
    }


    public SysFunction findById(long functionId, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .eq(SysFunctionBDBEntity::getFunctionId, functionId);
        return sysFunctionBMapperService.selectJoinOne(SysFunction.class, wrapper);
    }


    public List<SysFunction> findByCode(String functionCode, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .eq(SysFunctionBDBEntity::getFunctionCode, functionCode);
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }


    public List<SysFunction> findByIds(List<Long> functionIdList, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .in(SysFunctionBDBEntity::getFunctionId, functionIdList);
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }


    public List<SysFunction> findAll(String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                );
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }


    public List<SysFunction> findByRoleId(long roleId, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .innerJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysFunctionBDBEntity::getFunctionId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId);
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }


    public SysFunction findByPageId(long pageId, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .eq(SysFunctionBDBEntity::getPageId, pageId);
        return sysFunctionBMapperService.selectJoinOne(SysFunction.class, wrapper);
    }


    public List<SysFunction> findButtonById(long functionId) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, "zh-CN")
                )
                .eq(SysFunctionBDBEntity::getFunctionType, "B")
                .eq(SysFunctionBDBEntity::getParentId, functionId);
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }


    public SysFunction findByRouteName(String routeName) {
        LambdaQueryWrapper<SysFunctionBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysFunctionBDBEntity::getName, routeName);
        SysFunctionBDBEntity sysFunctionBDBEntity = sysFunctionBMapperService.selectOne(wrapper);
        return convertFromDB(sysFunctionBDBEntity);
    }


    public int findCount() {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionId)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, "zh-CN")
                );
        return sysFunctionBMapperService.selectJoinCount(wrapper).intValue();
    }


    public List<SysFunction> findByPage(String language, int offset, int endset) {
        Page<SysFunction> page = MyBatisUtil.convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                );
        Page<SysFunction> sysFunctionPage = sysFunctionBMapperService.selectJoinPage(page, SysFunction.class, wrapper);
        return sysFunctionPage.getRecords();
    }


    public int findCountByRoleId(long roleId) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionId)
                .innerJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysFunctionBDBEntity::getFunctionId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId);
        return sysFunctionBMapperService.selectJoinCount(wrapper).intValue();
    }


    public List<SysFunction> findPageByRoleId(long roleId, String language, int offset, int endset) {
        Page<SysFunction> page = MyBatisUtil.convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .innerJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysFunctionBDBEntity::getFunctionId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId)
                .orderByAsc(SysFunctionBDBEntity::getFunctionId);
        Page<SysFunction> sysFunctionPage = sysFunctionBMapperService.selectJoinPage(page, SysFunction.class, wrapper);
        return sysFunctionPage.getRecords();
    }


    public int findNotParent(String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionId)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .isNotNull(SysFunctionBDBEntity::getParentId);
        return sysFunctionBMapperService.selectJoinCount(wrapper).intValue();
    }


    public List<SysFunction> findPageByNotParent(String language, Integer offset, Integer endset) {
        Page<SysFunction> page = MyBatisUtil.convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .innerJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .isNotNull(SysFunctionBDBEntity::getParentId)
                .orderByAsc(SysFunctionBDBEntity::getFunctionId);
        Page<SysFunction> sysFunctionPage = sysFunctionBMapperService.selectJoinPage(page, SysFunction.class, wrapper);
        return sysFunctionPage.getRecords();
    }

    /**
     * 弃用
     */
    @Deprecated

    public List<SysFunction> findByWorkflow() {
        return null;
    }


    public void updateProjectId(long functionId, Long projectId) {
        LambdaUpdateWrapper<SysFunctionBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysFunctionBDBEntity::getProjectId, projectId)
                .eq(SysFunctionBDBEntity::getParentId, functionId);
        sysFunctionBMapperService.update(wrapper);
    }

    private SysFunctionBDBEntity convertToDB(SysFunction data) {
        SysFunctionBDBEntity entity = BeanUtils.toBean(data, SysFunctionBDBEntity.class);
        return entity;
    }

    private SysFunction convertFromDB(SysFunctionBDBEntity entity) {
        SysFunction data = BeanUtils.toBean(entity, SysFunction.class);
        return data;
    }
}
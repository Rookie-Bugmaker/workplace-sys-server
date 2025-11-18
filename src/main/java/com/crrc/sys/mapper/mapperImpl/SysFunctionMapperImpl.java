package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.function.*;
import com.crrc.sys.entity.function.column.SysFunctionColumn;
import com.crrc.sys.entity.function.column.SysFunctionColumnBDBEntity;
import com.crrc.sys.entity.function.column.SysFunctionColumnTlDBEntity;
import com.crrc.sys.entity.role.SysRoleMenuDBEntity;
import com.crrc.sys.mapper.SysFunctionMapper;
import com.crrc.sys.mapper.mapperService.SysFunctionBMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionColumnBMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionColumnTlMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionTlMapperService;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统功能-mapper继承类，用于继承mapper的方法，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-17
 */
@Repository
public class SysFunctionMapperImpl extends ServiceImpl<SysFunctionBMapperService, SysFunctionBDBEntity> implements SysFunctionMapper {

    @Resource
    private SysFunctionTlMapperService sysFunctionTlMapperService;

    @Resource
    private SysFunctionBMapperService sysFunctionBMapperService;

    @Override
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

    @Override
    public int insert(SysFunction function) {
        SysFunctionBDBEntity sysFunctionBDBEntity = convertToDB(function);
        return sysFunctionBMapperService.insert(sysFunctionBDBEntity);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public int delete(List<Long> functionIds) {
        LambdaUpdateWrapper<SysFunctionBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionBDBEntity::getFunctionId, functionIds);
        return sysFunctionBMapperService.delete(wrapper);
    }

    @Override
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

    @Override
    public SysFunction findByCode(String functionCode, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .eq(SysFunctionBDBEntity::getFunctionCode, functionCode);
        return sysFunctionBMapperService.selectJoinOne(SysFunction.class, wrapper);
    }

    @Override
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

    @Override
    public List<SysFunction> findAll(String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                );
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }

    @Override
    public List<SysFunction> findByRoleId(long roleId, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .leftJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysFunctionBDBEntity::getFunctionId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId);
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }

    @Override
    public SysFunction findByPageId(long pageId, String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .select(SysFunctionTlDBEntity::getFunctionDescription)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .eq(SysFunctionBDBEntity::getPageId, pageId);
        return sysFunctionBMapperService.selectJoinOne(SysFunction.class, wrapper);
    }

    @Override
    public List<SysFunction> findButtonById(long functionId) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, "zh-CN")
                )
                .eq(SysFunctionBDBEntity::getFunctionType, "B")
                .eq(SysFunctionBDBEntity::getParentId, functionId);
        return sysFunctionBMapperService.selectJoinList(SysFunction.class, wrapper);
    }

    @Override
    public SysFunction findByRouteName(String routeName) {
        LambdaQueryWrapper<SysFunctionBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysFunctionBDBEntity::getName, routeName);
        SysFunctionBDBEntity sysFunctionBDBEntity = sysFunctionBMapperService.selectOne(wrapper);
        return convertFromDB(sysFunctionBDBEntity);
    }

    @Override
    public int findCount() {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionId)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, "zh-CN")
                );
        return sysFunctionBMapperService.selectJoinCount(wrapper).intValue();
    }

    @Override
    public List<SysFunction> findByPage(String language, int offset, int endset) {
        Page<SysFunction> page = convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                );
        Page<SysFunction> sysFunctionPage = sysFunctionBMapperService.selectJoinPage(page, SysFunction.class, wrapper);
        return sysFunctionPage.getRecords();
    }

    @Override
    public int findCountByRoleId(long roleId) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionId)
                .leftJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysFunctionBDBEntity::getFunctionId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId);
        return sysFunctionBMapperService.selectJoinCount(wrapper).intValue();
    }

    @Override
    public List<SysFunction> findPageByRoleId(long roleId, String language, int offset, int endset) {
        Page<SysFunction> page = convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .leftJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysFunctionBDBEntity::getFunctionId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId)
                .orderByAsc(SysFunctionBDBEntity::getFunctionId);
        Page<SysFunction> sysFunctionPage = sysFunctionBMapperService.selectJoinPage(page, SysFunction.class, wrapper);
        return sysFunctionPage.getRecords();
    }

    @Override
    public int findNotParent(String language) {
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionId)
                .leftJoin(SysFunctionTlDBEntity.class,
                        fn -> fn.eq(SysFunctionTlDBEntity::getFunctionId, SysFunctionBDBEntity::getFunctionId)
                                .eq(SysFunctionTlDBEntity::getLanguage, language)
                )
                .isNotNull(SysFunctionBDBEntity::getParentId);
        return sysFunctionBMapperService.selectJoinCount(wrapper).intValue();
    }

    @Override
    public List<SysFunction> findPageByNotParent(String language, Integer offset, Integer endset) {
        Page<SysFunction> page = convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysFunctionBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionBDBEntity.class)
                .selectAll(SysFunctionBDBEntity.class)
                .select(SysFunctionTlDBEntity::getFunctionName)
                .leftJoin(SysFunctionTlDBEntity.class,
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
    @Override
    public List<SysFunction> findByWorkflow() {
        return null;
    }

    @Override
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
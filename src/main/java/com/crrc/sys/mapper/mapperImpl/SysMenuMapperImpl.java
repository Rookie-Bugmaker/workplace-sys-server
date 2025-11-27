package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.common.MyBatisUtil;
import com.crrc.sys.entity.function.SysFunction;
import com.crrc.sys.entity.menu.*;
import com.crrc.sys.entity.role.SysRoleMenuDBEntity;
import com.crrc.sys.mapper.SysMenuMapper;
import com.crrc.sys.mapper.mapperService.SysMenuBMapperService;
import com.crrc.sys.mapper.mapperService.SysMenuTlMapperService;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单多语言-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-18
 */
@Repository
public class SysMenuMapperImpl extends ServiceImpl<SysMenuTlMapperService, SysMenuTlDBEntity> {

    @Resource
    private SysMenuBMapperService sysMenuBMapperService;

    @Resource
    private SysMenuTlMapperService sysMenuTlMapperService;

    @Autowired
    private SysMenuBMapperImpl sysMenuBMapper;


    public List<SysMenu> findByFuzzy(SysMenuFindByPageParam param) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, param.getLanguage())
                )
                .isNull(SysMenuBDBEntity::getParentId)
                .eq(param.getProjectId() != null, SysMenuBDBEntity::getProjectId, param.getProjectId())
                .like(StringUtils.isNotBlank(param.getMenuCode()), SysMenuBDBEntity::getMenuCode, param.getMenuCode())
                .like(StringUtils.isNotBlank(param.getMenuName()), SysMenuTlDBEntity::getMenuName, param.getMenuName())
                .like(StringUtils.isNotBlank(param.getMenuDescription()), SysMenuTlDBEntity::getMenuDescription, param.getMenuDescription())
                .orderByAsc(SysMenuTlDBEntity::getMenuName);
        return sysMenuBMapperService.selectJoinList(SysMenu.class, wrapper);
    }

    public IPage<SysMenu> findByFuzzyPage(SysMenuFindByPageParam param) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, param.getLanguage())
                )
                .isNull(SysMenuBDBEntity::getParentId)
                .eq(param.getProjectId() != null, SysMenuBDBEntity::getProjectId, param.getProjectId())
                .like(StringUtils.isNotBlank(param.getMenuCode()), SysMenuBDBEntity::getMenuCode, param.getMenuCode())
                .like(StringUtils.isNotBlank(param.getMenuName()), SysMenuTlDBEntity::getMenuName, param.getMenuName())
                .like(StringUtils.isNotBlank(param.getMenuDescription()), SysMenuTlDBEntity::getMenuDescription, param.getMenuDescription())
                .orderByAsc(SysMenuTlDBEntity::getMenuName);
        Page<SysMenu> page = sysMenuBMapperService.selectJoinPage(
                new Page<>(param.getPage(), param.getPageSize()), SysMenu.class, wrapper);
        return page;
    }


    public int insertTl(List<SysMenuTl> menuTlList) {
        List<SysMenuTlDBEntity> entities = convertToDBTl(menuTlList);
        return super.saveBatch(entities) ? entities.size() : 0;
    }


    public int insert(SysMenuPO menu) {
        SysMenuBDBEntity entity = convertToDB(menu);
        return sysMenuBMapperService.insert(entity);
    }


    public int insertList(List<SysMenu> menuList) {
        List<SysMenuBDBEntity> entities = convertToDB(menuList);
        return sysMenuBMapper.saveBatch(entities) ? entities.size() : 0;
    }


    public List<SysMenu> findBySubmenu(String language) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .isNotNull(SysMenuBDBEntity::getParentId)
                .orderByAsc(SysMenuBDBEntity::getMenuSort);
        return sysMenuBMapperService.selectJoinList(SysMenu.class, wrapper);
    }


    public int updateTl(SysMenuTl menu) {
        LambdaUpdateWrapper<SysMenuTlDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysMenuTlDBEntity::getMenuId, menu.getMenuId())
                .eq(SysMenuTlDBEntity::getLanguage, menu.getLanguage())
                .set(SysMenuTlDBEntity::getMenuName, menu.getMenuName())
                .set(SysMenuTlDBEntity::getMenuDescription, menu.getMenuDescription())
                .set(SysMenuTlDBEntity::getLastUpdateBy, menu.getLastUpdateBy())
                .set(SysMenuTlDBEntity::getLastUpdateDate, menu.getLastUpdateDate())
                .set(SysMenuTlDBEntity::getLastUpdateLogin, menu.getLastUpdateLogin());
        return sysMenuTlMapperService.update(wrapper);
    }


    public int update(SysMenuPO menu) {
        LambdaUpdateWrapper<SysMenuBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysMenuBDBEntity::getMenuSort, menu.getMenuSort())
                .set(SysMenuBDBEntity::getFunctionId, menu.getFunctionId())
                .set(SysMenuBDBEntity::getParentId, menu.getParentId())
                .set(SysMenuBDBEntity::getChildId, menu.getChildId())
                .set(SysMenuBDBEntity::getMenuIcon, menu.getMenuIcon())
                .set(SysMenuBDBEntity::getProjectId, menu.getProjectId())
                .set(SysMenuBDBEntity::getLastUpdateBy, menu.getLastUpdateBy())
                .set(SysMenuBDBEntity::getLastUpdateDate, menu.getLastUpdateDate())
                .set(SysMenuBDBEntity::getLastUpdateLogin, menu.getLastUpdateLogin())
                .eq(SysMenuBDBEntity::getMenuId, menu.getMenuId());
        return sysMenuBMapperService.update(wrapper);
    }


    public List<SysMenu> findByIds(List<Long> menuIds, String language) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .in(SysMenuBDBEntity::getMenuId, menuIds);
        return sysMenuBMapperService.selectJoinList(SysMenu.class, wrapper);
    }


    public SysMenu findById(long menuId, String language) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .eq(SysMenuBDBEntity::getMenuId, menuId);
        return sysMenuBMapperService.selectJoinOne(SysMenu.class, wrapper);
    }


    public void deleteTl(List<Long> menuIdList) {
        LambdaQueryWrapper<SysMenuTlDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenuTlDBEntity::getMenuId, menuIdList);
        sysMenuTlMapperService.delete(wrapper);
    }


    public int delete(List<Long> menuIdList) {
        LambdaQueryWrapper<SysMenuBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenuBDBEntity::getMenuId, menuIdList);
        return sysMenuBMapperService.delete(wrapper);
    }


    public List<SysMenu> findByParentId(Long parentId, String language) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .eq(parentId != null, SysMenuBDBEntity::getParentId, parentId)
                .isNull(parentId == null, SysMenuBDBEntity::getParentId)
                .orderByAsc(SysMenuBDBEntity::getMenuSort);
        return sysMenuBMapperService.selectJoinList(SysMenu.class, wrapper);
    }


    public List<SysMenu> findAll(String language) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .orderByAsc(SysMenuBDBEntity::getMenuSort);
        return sysMenuBMapperService.selectJoinList(SysMenu.class, wrapper);
    }


    public List<SysMenu> findByCodes(List<String> codeList, String language) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .in(SysMenuBDBEntity::getMenuCode, codeList);
        return sysMenuBMapperService.selectJoinList(SysMenu.class, wrapper);
    }


    public List<SysMenu> findByRoleId(long roleId, String language) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .select(SysMenuBDBEntity::getMenuId)
                .select(SysMenuBDBEntity::getMenuCode)
                .select(SysMenuBDBEntity::getFunctionId)
                .select(SysMenuBDBEntity::getMenuIcon)
                .select(SysMenuBDBEntity::getParentId)
                .select(SysMenuBDBEntity::getChildId)
                .select(SysMenuBDBEntity::getMenuSort)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .select(SysMenuBDBEntity::getProjectId)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuBDBEntity::getMenuId, SysMenuTlDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .innerJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId)
                .orderByAsc(SysMenuBDBEntity::getMenuSort);
        return sysMenuBMapperService.selectJoinList(SysMenu.class, wrapper);
    }


    public List<Long> findBindFunctionId(List<Long> menuIds) {
        LambdaQueryWrapper<SysMenuBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysMenuBDBEntity::getFunctionId)
                .isNotNull(SysMenuBDBEntity::getFunctionId)
                .in(SysMenuBDBEntity::getMenuId, menuIds);
        return sysMenuBMapperService.selectList(wrapper).stream()
                .map(SysMenuBDBEntity::getFunctionId)
                .collect(Collectors.toList());
    }


    public List<Long> findBindChildId(List<Long> menuIds) {
        LambdaQueryWrapper<SysMenuBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysMenuBDBEntity::getChildId)
                .isNotNull(SysMenuBDBEntity::getChildId)
                .in(SysMenuBDBEntity::getMenuId, menuIds);
        return sysMenuBMapperService.selectList(wrapper).stream()
                .map(SysMenuBDBEntity::getChildId)
                .collect(Collectors.toList());
    }


    public List<Long> findByParentIds(List<Long> parentIdList) {
        LambdaQueryWrapper<SysMenuBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysMenuBDBEntity::getMenuId)
                .in(SysMenuBDBEntity::getParentId, parentIdList);
        return sysMenuBMapperService.selectList(wrapper).stream()
                .map(SysMenuBDBEntity::getMenuId)
                .collect(Collectors.toList());
    }


    public int findCount() {
        LambdaQueryWrapper<SysMenuBDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysMenuBDBEntity::getMenuId);
        return sysMenuBMapperService.selectCount(wrapper).intValue();
    }


    public List<SysMenu> findByPage(String language, int offset, int endset) {
        Page<SysMenu> page = MyBatisUtil.convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .orderByAsc(SysMenuBDBEntity::getMenuSort);
        Page<SysMenu> sysFunctionPage = sysMenuBMapperService.selectJoinPage(page, SysMenu.class, wrapper);
        return sysFunctionPage.getRecords();
    }


    public int findCountByRoleId(long roleId) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .select(SysMenuBDBEntity::getMenuId)
                .innerJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId);
        return sysMenuBMapperService.selectJoinCount(wrapper).intValue();
    }


    public List<SysMenu> findPageByRoleId(long roleId, String language, int offset, int endset) {
        Page<SysMenu> page = MyBatisUtil.convertToPage(offset, endset);
        page.setSearchCount(false);
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .select(SysMenuBDBEntity::getMenuId)
                .select(SysMenuBDBEntity::getMenuCode)
                .select(SysMenuBDBEntity::getFunctionId)
                .select(SysMenuBDBEntity::getMenuIcon)
                .select(SysMenuBDBEntity::getParentId)
                .select(SysMenuBDBEntity::getChildId)
                .select(SysMenuBDBEntity::getMenuSort)
                .select(SysMenuBDBEntity::getProjectId)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .innerJoin(SysRoleMenuDBEntity.class, SysRoleMenuDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                .eq(SysRoleMenuDBEntity::getRoleId, roleId)
                .orderByAsc(SysMenuBDBEntity::getMenuSort);
        Page<SysMenu> sysFunctionPage = sysMenuBMapperService.selectJoinPage(page, SysMenu.class, wrapper);
        return sysFunctionPage.getRecords();
    }


    public List<SysMenu> findRootMenu(Long projectId, String language) {
        MPJLambdaWrapper<SysMenuBDBEntity> wrapper = JoinWrappers.lambda(SysMenuBDBEntity.class)
                .selectAll(SysMenuBDBEntity.class)
                .select(SysMenuTlDBEntity::getMenuName)
                .select(SysMenuTlDBEntity::getMenuDescription)
                .innerJoin(SysMenuTlDBEntity.class,
                        fn -> fn.eq(SysMenuTlDBEntity::getMenuId, SysMenuBDBEntity::getMenuId)
                                .eq(SysMenuTlDBEntity::getLanguage, language)
                )
                .isNull(SysMenuBDBEntity::getParentId)
                .eq(projectId != null, SysMenuBDBEntity::getProjectId, projectId)
                .orderByAsc(SysMenuBDBEntity::getMenuSort);
        return sysMenuBMapperService.selectJoinList(SysMenu.class, wrapper);
    }


    public void updateProjectId(Long menuId, Long projectId) {
        LambdaUpdateWrapper<SysMenuBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysMenuBDBEntity::getParentId, menuId)
                .isNull(SysMenuBDBEntity::getChildId)
                .set(SysMenuBDBEntity::getProjectId, projectId);
        sysMenuBMapperService.update(wrapper);
    }

    private SysMenuBDBEntity convertToDB(SysMenu data) {
        SysMenuBDBEntity entity = BeanUtils.toBean(data, SysMenuBDBEntity.class);
        return entity;
    }

    private SysMenu convertFromDB(SysMenuBDBEntity entity) {
        SysMenu data = BeanUtils.toBean(entity, SysMenu.class);
        return data;
    }

    private List<SysMenuBDBEntity> convertToDB(List<SysMenu> dataList) {
        List<SysMenuBDBEntity> entityList = BeanUtils.toBean(dataList, SysMenuBDBEntity.class);
        return entityList;
    }

    private SysMenuBDBEntity convertToDB(SysMenuPO data) {
        SysMenuBDBEntity entity = BeanUtils.toBean(data, SysMenuBDBEntity.class);
        return entity;
    }

    private List<SysMenu> convertFromDB(List<SysMenuBDBEntity> entityList) {
        List<SysMenu> dataList = BeanUtils.toBean(entityList, SysMenu.class);
        return dataList;
    }

    private SysMenuTlDBEntity convertToDBTl(SysMenuTl data) {
        SysMenuTlDBEntity entity = BeanUtils.toBean(data, SysMenuTlDBEntity.class);
        return entity;
    }

    private SysMenuTl convertFromDBTl(SysMenuTlDBEntity entity) {
        SysMenuTl data = BeanUtils.toBean(entity, SysMenuTl.class);
        return data;
    }

    private List<SysMenuTlDBEntity> convertToDBTl(List<SysMenuTl> dataList) {
        List<SysMenuTlDBEntity> entityList = BeanUtils.toBean(dataList, SysMenuTlDBEntity.class);
        return entityList;
    }

    private List<SysMenuTl> convertFromDBTl(List<SysMenuTlDBEntity> entityList) {
        List<SysMenuTl> dataList = BeanUtils.toBean(entityList, SysMenuTl.class);
        return dataList;
    }
}
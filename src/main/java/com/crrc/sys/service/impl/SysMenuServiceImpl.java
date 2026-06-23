package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crrc.sys.common.Arith;
import com.crrc.sys.common.MyThreadPoolExecutor;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.api.SysApi;
import com.crrc.sys.entity.function.SysFunction;
import com.crrc.sys.entity.function.SysFunctionApiBO;
import com.crrc.sys.entity.function.column.SysFunctionColumn;
import com.crrc.sys.entity.menu.*;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.role.SysRoleFunctionColumn;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.mapper.mapperImpl.*;
import com.crrc.sys.service.ProjectService;
import com.crrc.sys.service.SysFunctionColumnService;
import com.crrc.sys.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-09-21
 */
@Service
@Slf4j
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapperImpl menuMapper;
    @Autowired
    private SysRoleMenuMapperImpl roleMenuMapper;
    @Autowired
    private SysFunctionMapperImpl functionMapper;
    @Autowired
    private SysRoleMapperImpl roleMapper;
    @Autowired
    private SysFunctionApiMapperImpl functionApiMapper;
    @Resource
    private SysFunctionColumnService functionColumnService;
    @Autowired
    private SysRoleFunctionColumnMapperImpl roleFunctionColumnMapper;
    @Resource
    private ProjectService projectService;

    /**
     * 菜单信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    @Override
    public PaginationGrid<SysMenu> findByPage(SysMenuFindByPageParam param) {
//        PageHelper.startPage(param.getPage(), param.getPageSize());
        //根菜单查询
        IPage<SysMenu> midPage = menuMapper.findByFuzzyPage(param);
        List<SysMenu> rows = midPage.getRecords();
        if (!CollectionUtils.isEmpty(rows)) {
            List<Long> projectIdList = rows.stream().map(SysMenu::getProjectId).filter(o -> !ObjectUtils.isEmpty(o)).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(projectIdList)) {
                List<ProjectEntity> projectList = projectService.listByIds(projectIdList);
                if (!CollectionUtils.isEmpty(projectList)) {
                    rows.forEach(o -> {
                        ProjectEntity projectEntity = projectList.stream().filter(p -> p.getId().equals(o.getProjectId())).findFirst().orElse(null);
                        o.setProjectName(ObjectUtils.isEmpty(projectEntity) ? null : projectEntity.getNameCn());
                    });
                }
            }
        }
//        PageInfo<SysMenu> pageInfo = new PageInfo<>(rows);
//        return PaginationGrid.success(pageInfo);
        PaginationGrid<SysMenu> page = new PaginationGrid<>(
                Math.toIntExact(midPage.getCurrent()), Math.toIntExact(midPage.getSize()));
        page.setTotal(midPage.getTotal());
        page.setRows(rows);
        return page;
    }

    /**
     * 递归构建子菜单(必须调用此方式)
     * 第1步：完善childId对应的菜单信息
     *
     * @param parentMenu 上级菜单
     * @param menuAll    所有菜单集合
     */
    private void setChildrenMenu(SysMenu parentMenu, List<SysMenu> menuAll) {
        //===============完善childId对应的菜单信息(开始)======================
        //所有子菜单ID集合
        List<Long> childIdList = menuAll.stream().map(SysMenu::getChildId).filter(childId -> !ObjectUtils.isEmpty(childId)).collect(Collectors.toList());
        //所有菜单ID集合
        List<Long> menuIdList = menuAll.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        //判断menuAll中是否已经存在该子菜单ID所对应的菜单信息，如果存在，则移除
        childIdList.removeIf(menuIdList::contains);
        if (!CollectionUtils.isEmpty(childIdList)) {
            //如果子菜单ID集合不为空，则需要去数据库查询这些子菜单ID所对应的菜单信息
            List<SysMenu> childMenuList = menuMapper.findByIds(childIdList, LanguageEnum.ZH_CN.getValue());
            menuAll.addAll(childMenuList);
        }
        //===============完善childId对应的菜单信息(结束)======================
        setChildrenMenuNext(parentMenu, menuAll);
    }

    /**
     * 递归构建子菜单
     * 第2步：构建菜单树结构
     *
     * @param parentMenu 上级菜单
     * @param menuAll    所有菜单集合
     */
    private void setChildrenMenuNext(SysMenu parentMenu, List<SysMenu> menuAll) {
        //根据上级菜单ID查询子菜单集合
        List<SysMenu> children = menuAll.stream().filter(o -> parentMenu.getMenuId().equals(o.getParentId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(children)) {
            for (SysMenu child : children) {
                if (!ObjectUtils.isEmpty(child.getChildId())) {
                    //查找menuId==childId菜单
                    //如果不调用setChildrenMenu()方法完善菜单数据，此处则会找不到menuId==childId菜单
                    SysMenu menu = menuAll.stream().filter(p -> p.getMenuId().equals(child.getChildId())).findFirst().orElse(null);
                    if (!ObjectUtils.isEmpty(menu)) {
                        setChildrenMenuNext(menu, menuAll);
                        child.setChildren(menu.getChildren());
                        child.setChildCode(menu.getMenuCode());
                        child.setChildName(menu.getMenuName());
                    }
                    continue;
                }
                setChildrenMenuNext(child, menuAll);
            }
            parentMenu.setChildren(children);
        }
    }

    /**
     * 菜单新增(新)
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int add(SysMenuDTO menu) {
        List<SysMenuTl> menuTlList = new ArrayList<>();
        SysMenuTl menuTlEn = new SysMenuTl();
        BeanUtils.copyProperties(menu, menuTlEn);
        menuTlEn.setLanguage(LanguageEnum.EN_US.getValue());
        menuTlList.add(menuTlEn);
        SysMenuTl menuTlCn = new SysMenuTl();
        BeanUtils.copyProperties(menu, menuTlCn);
        menuTlCn.setLanguage(LanguageEnum.ZH_CN.getValue());
        menuTlList.add(menuTlCn);
        int result = menuMapper.insertTl(menuTlList);
        if (result != 2) {
            throw new BizException("新增菜单(多语言)失败");
        }
        SysMenuPO menuPO = new SysMenuPO();
        BeanUtils.copyProperties(menu, menuPO);
        result = menuMapper.insert(menuPO);
        if (result != 1) {
            throw new BizException("新增菜单失败");
        }
        return result;
    }

    /**
     * 菜单修改(新)
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SysMenuDTO menu) {
        //修改直属子菜单的项目ID
        menuMapper.updateProjectId(menu.getMenuId(), menu.getProjectId());
        SysMenuTl menuTlEn = new SysMenuTl();
        BeanUtils.copyProperties(menu, menuTlEn);
        int result = menuMapper.updateTl(menuTlEn);
        if (result != 1) {
            throw new BizException("修改菜单(多语言)失败");
        }
        SysMenuPO menuPO = new SysMenuPO();
        BeanUtils.copyProperties(menu, menuPO);
        result = menuMapper.update(menuPO);
        if (result != 1) {
            throw new BizException("修改菜单失败");
        }
        return result;
    }

    /**
     * 菜单删除
     *
     * @param ids 菜单ID集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(List<Long> ids) {
        //根据菜单ID查询菜单信息
        List<SysMenu> rows = menuMapper.findByIds(ids, LanguageEnum.ZH_CN.getValue());
        if (CollectionUtils.isEmpty(rows)) {
            throw new BizException("菜单不存在!");
        }
        List<Long> menuIdList = new ArrayList<>();
        //搜索没有绑定子菜单的菜单ID
        List<Long> menuIdList1 = rows.stream().filter(o -> ObjectUtils.isEmpty(o.getChildId())).map(SysMenu::getMenuId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(menuIdList1)) {
            menuIdList.addAll(menuIdList1);
            //查询下级菜单ID
            List<Long> menuIdList3 = menuMapper.findByParentIds(menuIdList1);
            if (!CollectionUtils.isEmpty(menuIdList3)) {
                menuIdList.addAll(menuIdList3);
            }
        }
        //搜索已绑定子菜单的菜单ID
        List<Long> menuIdList2 = rows.stream().filter(o -> !ObjectUtils.isEmpty(o.getChildId())).map(SysMenu::getMenuId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(menuIdList2)) {
            menuIdList.addAll(menuIdList2);
        }
        //1.角色菜单中间表删除
        roleMenuMapper.deleteByMenuIds(menuIdList);
        //2.菜单(多语言)删除
        menuMapper.deleteTl(menuIdList);
        //2.菜单及子菜单删除
        return menuMapper.delete(menuIdList);
    }

    /**
     * 获取菜单及子菜单的ID
     *
     * @param rows 菜单集合
     * @return 菜单ID集合
     */
    private List<Long> getMenuId(List<SysMenu> rows) {
        List<Long> menuIdList = new ArrayList<>();
        for (SysMenu parent : rows) {
            menuIdList.add(parent.getMenuId());
            if (!CollectionUtils.isEmpty(parent.getChildren())) {
                menuIdList.addAll(getMenuId(parent.getChildren()));
            }
        }
        return menuIdList;
    }

    /**
     * 根据菜单ID查询菜单信息(会递归查询该菜单下的所有子菜单)
     *
     * @param menuId   菜单ID
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysMenu findById(long menuId, String language) {
        SysMenu menu = menuMapper.findById(menuId, language);
        if (ObjectUtils.isEmpty(menu)) {
            return null;
        }
        //查询所有菜单
        List<SysMenu> menuAll = menuMapper.findAll(language);
        setChildrenMenu(menu, menuAll);
        //查询所有功能数据
        List<SysFunction> functionAll = functionMapper.findAll(language);
        //查询并设置功能
        setChildrenFunction(menu, functionAll);
        return menu;
    }

    /**
     * 根据菜单ID查询菜单信息(查询最小数据)
     *
     * @param menuId   菜单ID
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysMenu findMinById(long menuId, String language) {
        return menuMapper.findById(menuId, language);
    }

    /**
     * 根据菜单编码集合查询菜单信息
     *
     * @param codeList 菜单编码集合
     * @param language 语言
     * @return 结果
     */
    @Override
    public List<SysMenu> findByCodes(List<String> codeList, String language) {
        return menuMapper.findByCodes(codeList, language);
    }

    /**
     * 查询根菜单
     *
     * @param menuId 菜单ID
     * @return 根菜单ID
     */
    @Override
    public long findRootId(long menuId) {
        //查询所有菜单
        List<SysMenu> sysMenuList = menuMapper.findAll(LanguageEnum.ZH_CN.getValue());
        //递归查找根菜单
        return getRootId(menuId, sysMenuList);
    }

    /**
     * 查找根菜单ID
     *
     * @param menuId      菜单ID
     * @param sysMenuList 所有的菜单集合
     * @return 根菜单ID
     */
    private long getRootId(long menuId, List<SysMenu> sysMenuList) {
        for (SysMenu menu : sysMenuList) {
            if (menu.getMenuId().equals(menuId)) {
                if (ObjectUtils.isEmpty(menu.getParentId())) {
                    menuId = menu.getMenuId();
                    break;
                }
                menuId = getRootId(menu.getParentId(), sysMenuList);
                break;
            }
        }
        return menuId;
    }

    /**
     * 根据菜单ID查询所有的子菜单ID集合
     *
     * @param menuId 菜单ID
     * @return 子菜单ID集合
     */
    @Override
    public List<Long> findMenuIdList(long menuId) {
        List<Long> menuIdList = new ArrayList<>();
        //查询菜单
        SysMenu rootMenu = menuMapper.findById(menuId, LanguageEnum.ZH_CN.getValue());
        if (!ObjectUtils.isEmpty(rootMenu)) {
            menuIdList.add(menuId);
            //查询所有菜单
            List<SysMenu> menuAll = menuMapper.findAll(LanguageEnum.ZH_CN.getValue());
            menuIdList.addAll(getChildrenId(menuId, menuAll));
        }
        return menuIdList;
    }

    /**
     * 获取所有下级菜单ID
     *
     * @param parentMenuId 父级菜单ID
     * @param menuAll      所有菜单
     * @return 下级菜单ID集合
     */
    private List<Long> getChildrenId(long parentMenuId, List<SysMenu> menuAll) {
        List<Long> menuIdList = new ArrayList<>();
        //查找下级菜单
        List<SysMenu> childrenList = menuAll.stream().filter(o -> !ObjectUtils.isEmpty(o.getParentId())).filter(o -> o.getParentId().equals(parentMenuId)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(childrenList)) {
            //收集下级菜单的ID
            menuIdList.addAll(childrenList.stream().map(SysMenu::getMenuId).collect(Collectors.toList()));
            for (SysMenu menu : childrenList) {
                menuIdList.addAll(getChildrenId(menu.getMenuId(), menuAll));
                if (!ObjectUtils.isEmpty(menu.getChildId())) {
                    menuIdList.add(menu.getChildId());
                    menuIdList.addAll(getChildrenId(menu.getChildId(), menuAll));
                }
            }
        }
        return menuIdList;
    }

    /**
     * 查询根菜单列表
     *
     * @param language 语言
     * @return 结果
     */
    @Override
    public List<SysMenu> findRootMenu(Long projectId, String language) {
        return menuMapper.findRootMenu(projectId, language);
    }

    /**
     * 根据菜单ID查询树结构
     *
     * @param menuId   菜单ID
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysMenu findTreeByMenuId(long menuId, String language) {
        SysMenu rootMenu = menuMapper.findById(menuId, language);
        if (ObjectUtils.isEmpty(rootMenu)) {
            throw new BizException("菜单不存在!");
        }
        //查询所有菜单
        List<SysMenu> menuAll = findMenuAllByThead(language);
        if (CollectionUtils.isEmpty(menuAll)) {
            return rootMenu;
        }
        //查询并设置子菜单
        setChildrenMenu(rootMenu, menuAll);
        //查询所有功能数据
        List<SysFunction> functionAll = findFunctionAllByThead(language);
        //查询并设置功能
        setChildrenFunction(rootMenu, functionAll);
        return rootMenu;
    }

    /**
     * 查询角色拥有的菜单(树结构)
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysMenu findTreeByRoleId(long roleId, String language) {
        //根据角色ID查询角色信息
        final SysRole sysRole = roleMapper.findById(roleId, language);
        if (ObjectUtils.isEmpty(sysRole)) {
            throw new BizException("角色ID不存在[" + roleId + "]");
        }
        CompletableFuture<SysMenu> completableFuture = CompletableFuture.supplyAsync(() -> {
            //查找根菜单
            SysMenu rootMenu = menuMapper.findById(sysRole.getMenuId(), language);
            if (ObjectUtils.isEmpty(rootMenu)) {
                return null;
            }
            //查询角色拥有的菜单集合
            List<SysMenu> menuAll = this.findMenuByRoleId(roleId, language);
            if (CollectionUtils.isEmpty(menuAll)) {
                return null;
            }
            //递归设置子菜单
            setChildrenMenu(rootMenu, menuAll);
            return rootMenu;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            //查询角色拥有的功能
            List<SysFunction> functionAll = this.findFunctionByRoleId(roleId, language);
            if (CollectionUtils.isEmpty(functionAll)) {
                return new ArrayList<SysFunction>();
            }
            //设置功能已绑定的API权限
            functionAll = this.setFunctionApiList(functionAll);
            List<Long> functionIdList = functionAll.stream().map(SysFunction::getFunctionId).distinct().collect(Collectors.toList());
            //查询功能绑定的块权限
            List<SysFunctionColumn> functionColumnList = functionColumnService.findByFunctionIds(functionIdList, language);
            //查询角色拥有的列权限
            List<SysRoleFunctionColumn> rfcList = roleFunctionColumnMapper.findByRoleId(roleId);
            if (!CollectionUtils.isEmpty(rfcList)) {
                functionColumnList.forEach(o -> {
                    //块下面的所有列权限
                    List<SysFunctionColumn> columnList = o.getChildren();
                    if (!CollectionUtils.isEmpty(columnList)) {
                        //遍历所有的列权限
                        for (SysFunctionColumn functionColumn : columnList) {
                            //遍历拥有的列权限
                            for (SysRoleFunctionColumn hasColumn : rfcList) {
                                if (functionColumn.getFcId().equals(hasColumn.getFcId())) {
                                    //设置实际编辑编辑的值
                                    functionColumn.setEditableFlag(hasColumn.getFcEditableFlag());
                                    //修改隐藏列为false
                                    functionColumn.setHideColumn(false);
                                }
                            }
                        }
                    }
                });
            }
            //设置列权限
            for (SysFunction function : functionAll) {
                //搜索功能对应的块权限
                List<SysFunctionColumn> blockList = functionColumnList.stream().filter(o -> o.getFunctionId().equals(function.getFunctionId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(blockList)) {
                    function.setBlockList(blockList);
                }
            }
            return functionAll;
        }), (rootMenu, functionAll) -> {
            //查询并设置功能
            setChildrenFunction(rootMenu, functionAll);
            return rootMenu;
        });
        return completableFuture.join();
    }

    /**
     * 设置菜单及其子菜单的功能
     *
     * @param parentMenu  菜单
     * @param functionAll 所有功能集合
     */
    private void setChildrenFunction(SysMenu parentMenu, List<SysFunction> functionAll) {
        if (!ObjectUtils.isEmpty(parentMenu.getFunctionId())) {
            SysFunction function = functionAll.stream().filter(o -> o.getFunctionId().equals(parentMenu.getFunctionId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(function)) {
                setFunctionChildren(Collections.singletonList(function), functionAll);
                parentMenu.setFunction(function);
            }
        } else {
            List<SysMenu> menuChildren = parentMenu.getChildren();
            if (!CollectionUtils.isEmpty(menuChildren)) {
                for (SysMenu menuChild : menuChildren) {
                    setChildrenFunction(menuChild, functionAll);
                }
            }
        }
    }

    /**
     * 递归查找子功能
     *
     * @param parentList  上级功能
     * @param childrenAll 所有功能
     */
    private void setFunctionChildren(List<SysFunction> parentList, List<SysFunction> childrenAll) {
        for (SysFunction parent : parentList) {
            //查询子功能
            List<SysFunction> children = childrenAll.stream().filter(o -> !ObjectUtils.isEmpty(o.getParentId())).filter(o -> o.getParentId().equals(parent.getFunctionId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(children)) {
                setFunctionChildren(children, childrenAll);
                parent.setChildren(children);
            }
        }
    }

    /**
     * 根据菜单ID查询功能ID集合
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public List<Long> findFunctionIdByMenuId(long menuId) {
        //根据菜单ID查询菜单树
        SysMenu menu = findTreeByMenuId(menuId, LanguageEnum.ZH_CN.getValue());
        if (ObjectUtils.isEmpty(menu)) {
            return null;
        }
        Set<Long> set = getFunctionIdByMenu(menu);
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        return new ArrayList<>(set);
    }

    /**
     * 根据菜单(树结构)查询功能ID集合
     *
     * @param menu 菜单数结构
     * @return 功能ID集合
     */
    private Set<Long> getFunctionIdByMenu(SysMenu menu) {
        Set<Long> functionIdList = new HashSet<>();
        if (ObjectUtils.isEmpty(menu)) {
            return null;
        }
        List<SysMenu> children = menu.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            children.forEach(o -> {
                Set<Long> funIds = getFunctionIdByMenu(o);
                if (!CollectionUtils.isEmpty(funIds)) {
                    functionIdList.addAll(funIds);
                }
            });
        }
        Long functionId = menu.getFunctionId();
        if (!ObjectUtils.isEmpty(functionId)) {
            functionIdList.add(functionId);
            return functionIdList;
        }
        return functionIdList;
    }

    /**
     * 查询所有菜单(优化)
     * 使用多线程优化查询效率
     *
     * @param language 语言
     * @return 菜单集合
     */
    private List<SysMenu> findMenuAllByThead(String language) {
        //查询所有菜单的总数
        int count = menuMapper.findCount();
        if (count <= 0) {
            return null;
        }
        //每页查询数据条数
        int pageSize = 200;
        //最大页码
        int page = (int) Math.ceil(Arith.div(count, pageSize, 2));
        List<SysMenu> menuListAll = new LinkedList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>(page);
        //自定义线程池
        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(page);
        for (int i = 1; i <= page; i++) {
            final PaginationGrid<SysMenu> pg = new PaginationGrid<>(i, pageSize);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                List<SysMenu> menuList = menuMapper.findByPage(language, pg.getOffset(), pg.getEndset());
                synchronized (menuListAll) {
                    menuListAll.addAll(menuList);
                }
            });
            futureList.add(completableFuture);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        myThreadPool.shutdown();
        return menuListAll;
    }

    /**
     * 查询所有功能(优化)
     * 使用多线程优化查询效率
     *
     * @param language 语言
     * @return 功能集合
     */
    private List<SysFunction> findFunctionAllByThead(String language) {
        //查询所有功能的总数
        int count = functionMapper.findCount();
        if (count <= 0) {
            return null;
        }
        //每页查询数据条数
        int pageSize = 200;
        //最大页码
        int page = (int) Math.ceil(Arith.div(count, pageSize, 2));
        List<SysFunction> functionListAll = new LinkedList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>(page);
        //自定义线程池
        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(page);
        for (int i = 1; i <= page; i++) {
            final PaginationGrid<SysFunction> pg = new PaginationGrid<>(i, pageSize);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                List<SysFunction> functionList = functionMapper.findByPage(language, pg.getOffset(), pg.getEndset());
                synchronized (functionListAll) {
                    functionListAll.addAll(functionList);
                }
            });
            futureList.add(completableFuture);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        myThreadPool.shutdown();
        return functionListAll;
    }

    /**
     * 查询角色拥有的菜单集合(优化)
     * 使用多线程优化查询效率
     *
     * @param language 语言
     * @return 菜单集合
     */
    private List<SysMenu> findMenuByRoleId(long roleId, String language) {
        //查询角色拥有的菜单集合总条数
        int count = menuMapper.findCountByRoleId(roleId);
        if (count <= 0) {
            return null;
        }
        //每页查询数据条数
        int pageSize = 200;
        //最大页码
        int page = (int) Math.ceil(Arith.div(count, pageSize, 2));
        List<SysMenu> menuListAll = new LinkedList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>(page);
        //自定义线程池
        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(page);
        for (int i = 1; i <= page; i++) {
            final PaginationGrid<SysMenu> pg = new PaginationGrid<>(i, pageSize);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                List<SysMenu> menuList = menuMapper.findPageByRoleId(roleId, language, pg.getOffset(), pg.getEndset());
                synchronized (menuListAll) {
                    menuListAll.addAll(menuList);
                }
            });
            futureList.add(completableFuture);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        myThreadPool.shutdown();
        //正序，且空值排前面
        menuListAll.sort(Comparator.comparing(SysMenu::getMenuSort, Comparator.nullsLast(Comparator.naturalOrder())));
        return menuListAll;
    }

    /**
     * 查询角色拥有的功能集合(优化)
     * 使用多线程优化查询效率
     *
     * @param language 语言
     * @return 菜单集合
     */
    private List<SysFunction> findFunctionByRoleId(long roleId, String language) {
        //查询角色拥有的功能集合总条数
        int count = functionMapper.findCountByRoleId(roleId);
        if (count <= 0) {
            return null;
        }
        //每页查询数据条数
        int pageSize = 200;
        //最大页码
        int page = (int) Math.ceil(Arith.div(count, pageSize, 2));
        List<SysFunction> functionListAll = new LinkedList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>(page);
        //自定义线程池
        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(page);
        for (int i = 1; i <= page; i++) {
            final PaginationGrid<SysFunction> pg = new PaginationGrid<>(i, pageSize);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                List<SysFunction> functionList = functionMapper.findPageByRoleId(roleId, language, pg.getOffset(), pg.getEndset());
                synchronized (functionListAll) {
                    functionListAll.addAll(functionList);
                }
            });
            futureList.add(completableFuture);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        myThreadPool.shutdown();
        return functionListAll;
    }

    /**
     * 查询功能绑定的API权限集合(优化)
     * 使用多线程优化查询效率
     *
     * @param functionAll 功能集合
     * @return 功能集合(带api集合)
     */
    private List<SysFunction> setFunctionApiList(List<SysFunction> functionAll) {
        //查询角色拥有的功能集合总条数
        int count = functionAll.size();
        //每页查询数据条数
        int pageSize = 200;
        //最大页码
        int page = (int) Math.ceil(Arith.div(count, pageSize, 2));
        List<SysFunction> functionListAll = new LinkedList<>();
        List<CompletableFuture<Void>> futureList = new ArrayList<>(page);
        //自定义线程池
        ThreadPoolExecutor myThreadPool = MyThreadPoolExecutor.getThreadPoolExecutor(page);
        for (long i = 0; i < page; i++) {
            //当前页功能列表
            List<SysFunction> functionPageList = functionAll.stream().skip(i * pageSize).limit(pageSize).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(functionPageList)) {
                break;
            }
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                //当前也功能ID集合
                List<Long> isPageList = functionPageList.stream().map(SysFunction::getFunctionId).distinct().collect(Collectors.toList());
                //查询功能绑定的API权限集合
                List<SysFunctionApiBO> functionApiList = functionApiMapper.findByFunctionIds(isPageList);
                for (SysFunction function : functionPageList) {
                    List<SysApi> sysApis = functionApiList.stream().filter(o -> o.getFunctionId().equals(function.getFunctionId())).findFirst().map(SysFunctionApiBO::getApiList).orElse(null);
                    if (!CollectionUtils.isEmpty(sysApis)) {
                        function.setApiList(sysApis);
                    }
                }
                synchronized (functionListAll) {
                    functionListAll.addAll(functionPageList);
                }
            });
            futureList.add(completableFuture);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        myThreadPool.shutdown();
        return functionListAll;
    }
}
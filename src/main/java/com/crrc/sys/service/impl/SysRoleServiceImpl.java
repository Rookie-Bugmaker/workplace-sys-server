package com.crrc.sys.service.impl;

import com.crrc.sys.common.Arith;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.controller.SysOnlineUserController;
import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.entity.role.*;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.mapper.*;
import com.crrc.sys.model.role.SysRoleCompanyDeptDTO;
import com.crrc.sys.service.ProjectService;
import com.crrc.sys.service.SysRoleCompanyDeptService;
import com.crrc.sys.service.SysRoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-09-07
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysRoleMenuMapper roleMenuMapper;
    @Resource
    private SysRoleColumnValueSetMapper roleColumnValueSetMapper;
    @Resource
    private SysMenuMapper menuMapper;
    @Resource
    private SysRoleFunctionColumnMapper roleFunctionColumnMapper;
    @Resource
    private SysDataAuthMapper dataAuthMapper;
    @Resource
    private SysDataAuthValueMapper dataAuthValueMapper;
    @Resource
    private ProjectService projectService;
    @Resource
    private SysRoleCompanyDeptService roleCompanyDeptService;
    @Resource
    private SysUserRoleMapper userRoleMapper;
    @Resource
    private SysOnlineUserController onlineUserController;
    @Resource
    private ValueOperations<String, Object> valueOperations;

    /**
     * 角色信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    @Override
    public PaginationGrid<SysRole> findByPage(SysRoleFindByPageParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<SysRole> rows = roleMapper.findByFuzzy(param);
        if (!CollectionUtils.isEmpty(rows)) {
            List<Long> projectIdList = rows.stream().map(SysRole::getProjectId).distinct().collect(Collectors.toList());
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
        PageInfo<SysRole> pageInfo = new PageInfo<>(rows);
        return PaginationGrid.success(pageInfo);
    }

    /**
     * 新增
     *
     * @param role 角色信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int add(SysRole role) {
        //角色菜单数据
        List<SysRoleMenu> roleMenuList = role.getRoleMenuList();
        if (!CollectionUtils.isEmpty(roleMenuList)) {
            Set<Long> hasMenuIds = roleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
            Set<Long> otherMenuIds = new HashSet<>();
            //+++++分页查询(开始)+++++++
            long pageSize = 500;
            long page = 1;
            if (hasMenuIds.size() > pageSize) {
                page = (long) Math.ceil(Arith.div(hasMenuIds.size(), pageSize, 2));
            }
            for (long i = 0; i < page; i++) {
                List<Long> isPageIdList = hasMenuIds.stream().skip(pageSize * i).limit(pageSize).collect(Collectors.toList());
                //根据菜单ID查询绑定的子菜单ID
                List<Long> isPageBindChildIdList = menuMapper.findBindChildId(isPageIdList);
                if (CollectionUtils.isEmpty(isPageBindChildIdList)) {
                    otherMenuIds.addAll(isPageBindChildIdList);
                }
                //根据菜单ID查询绑定的功能ID
                List<Long> isPageBindFunctionIds = menuMapper.findBindFunctionId(isPageIdList);
                if (!CollectionUtils.isEmpty(isPageBindFunctionIds)) {
                    otherMenuIds.addAll(isPageBindFunctionIds);
                }
            }
            //+++++分页查询(结束)+++++++
            if (!CollectionUtils.isEmpty(otherMenuIds)) {
                otherMenuIds.removeIf(hasMenuIds::contains);
                for (Long id : otherMenuIds) {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(role.getRoleId());
                    roleMenu.setMenuId(id);
                    roleMenu.setCreateBy(role.getCreateBy());
                    roleMenu.setCreateDate(role.getCreateDate());
                    roleMenu.setChildFlag(true);
                    roleMenuList.add(roleMenu);
                }
            }
        }
        //添加拥有的列权限
        if (!CollectionUtils.isEmpty(role.getCheckedFunctionColumns())) {
            roleFunctionColumnMapper.insert(role.getCheckedFunctionColumns());
        }
        if (!CollectionUtils.isEmpty(role.getRoleColumnValueSets())) {
            //添加角色列权限值集数据
            roleColumnValueSetMapper.insert(role.getRoleColumnValueSets());
        }
        //添加角色菜单数据
        this.insertRoleMenu(role.getRoleMenuList());
        //添加角色部门数据
        insertRoleCompanyDept(role.getRoleCompanyDeptList(), role.getRoleId());
        List<SysDataAuth> dataAuthList = role.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            //添加角色数据权限
            dataAuthMapper.insertList(dataAuthList);
            //添加角色数据权限值
            dataAuthValueMapper.insertList(dataAuthList.stream().map(SysDataAuth::getDataAuthValueList).flatMap(Collection::stream).collect(Collectors.toList()));
        }
        //添加角色国际化数据
        List<SysRoleTl> roleTlList = new ArrayList<>();
        SysRoleTl roleTl = new SysRoleTl();
        BeanUtils.copyProperties(role, roleTl);
        roleTl.setLanguage(LanguageEnum.ZH_CN.getValue());
        roleTlList.add(roleTl);
        roleTl = new SysRoleTl();
        BeanUtils.copyProperties(role, roleTl);
        roleTl.setLanguage(LanguageEnum.EN_US.getValue());
        roleTlList.add(roleTl);
        roleMapper.addTl(roleTlList);
        //添加角色主表数据
        return roleMapper.addB(role);
    }

    /**
     * 角色部门中间表新增
     *
     * @param dtoList 角色部门数据
     */
    private void insertRoleCompanyDept(List<SysRoleCompanyDeptDTO> dtoList, long roleId) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }
        List<SysRoleCompanyDept> rcdList = new ArrayList<>();
        for (SysRoleCompanyDeptDTO dto : dtoList) {
            List<Long> deptIdList = dto.getDeptIdList();
            if (CollectionUtils.isEmpty(deptIdList)) {
                SysRoleCompanyDept rcd = new SysRoleCompanyDept();
                rcd.setId(idKeyGenerator.getId());
                rcd.setRoleId(roleId);
                rcd.setCompanyId(dto.getCompanyId());
                rcdList.add(rcd);
                continue;
            }
            deptIdList.forEach(o -> {
                SysRoleCompanyDept rcd = new SysRoleCompanyDept();
                rcd.setId(idKeyGenerator.getId());
                rcd.setRoleId(roleId);
                rcd.setCompanyId(dto.getCompanyId());
                rcd.setDeptId(o);
                rcdList.add(rcd);
            });
        }
        long pageSize = 100;
        long page = 1;
        if (rcdList.size() > pageSize) {
            page = (long) Math.ceil(Arith.div(rcdList.size(), pageSize, 2));
        }
        for (long i = 0; i < page; i++) {
            List<SysRoleCompanyDept> pageList = rcdList.stream().skip(pageSize * i).limit(pageSize).collect(Collectors.toList());
            boolean result = roleCompanyDeptService.saveBatch(pageList);
            if (!result) {
                throw new BizException("角色部门权限保存失败");
            }
        }
    }

    /**
     * 角色菜单中间表新增
     *
     * @param roleMenuList 角色菜单数据
     */
    private void insertRoleMenu(List<SysRoleMenu> roleMenuList) {
        long pageSize = 100;
        long page = 1;
        if (roleMenuList.size() > pageSize) {
            page = (long) Math.ceil(Arith.div(roleMenuList.size(), pageSize, 2));
        }
        for (long i = 0; i < page; i++) {
            List<SysRoleMenu> pageList = roleMenuList.stream().skip(pageSize * i).limit(pageSize).collect(Collectors.toList());
            roleMenuMapper.insert(pageList);
        }
    }

    /**
     * 修改
     *
     * @param role 用户信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SysRole role) {
        //角色菜单数据
        List<SysRoleMenu> roleMenuList = role.getRoleMenuList();
        //分页条数，超过此条数则进行分页处理
        long pageSize = 500;
        if (!CollectionUtils.isEmpty(roleMenuList)) {
            int page = 1;
            if (roleMenuList.size() > pageSize) {
                //计算最大分页数
                page = (int) Math.ceil(Arith.div(roleMenuList.size(), pageSize));
            }
            List<Long> otherMenuIds = new ArrayList<>();
            for (int i = 0; i < page; i++) {
                List<Long> hasMenuIds = roleMenuList.stream().skip(i * pageSize).limit(pageSize).map(SysRoleMenu::getMenuId).collect(Collectors.toList());
                //根据菜单ID查询绑定的子菜单ID
                List<Long> bindChildIds = menuMapper.findBindChildId(hasMenuIds);
                if (!CollectionUtils.isEmpty(bindChildIds)) {
                    otherMenuIds.addAll(bindChildIds);
                }
                //根据菜单ID查询绑定的功能ID
                List<Long> bindFunctionIds = menuMapper.findBindFunctionId(hasMenuIds);
                if (!CollectionUtils.isEmpty(bindFunctionIds)) {
                    otherMenuIds.addAll(bindFunctionIds);
                }
                //删除已存在的菜单ID
                if (!CollectionUtils.isEmpty(otherMenuIds)) {
                    otherMenuIds.removeIf(hasMenuIds::contains);
                }
            }
            if (!CollectionUtils.isEmpty(otherMenuIds)) {
                otherMenuIds = otherMenuIds.stream().distinct().collect(Collectors.toList());
                for (Long id : otherMenuIds) {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(role.getRoleId());
                    roleMenu.setMenuId(id);
                    roleMenu.setCreateBy(role.getLastUpdateBy());
                    roleMenu.setCreateDate(role.getLastUpdateDate());
                    roleMenu.setChildFlag(true);
                    roleMenuList.add(roleMenu);
                }
            }
        }
        //删除列权限
        roleFunctionColumnMapper.deleteByRoleIds(Collections.singletonList(role.getRoleId()));
        if (!CollectionUtils.isEmpty(role.getCheckedFunctionColumns())) {
            //添加拥有的列权限
            roleFunctionColumnMapper.insert(role.getCheckedFunctionColumns());
        }
        //删除角色列权限值集
        roleColumnValueSetMapper.deleteByRoleIds(Collections.singletonList(role.getRoleId()));
        if (!CollectionUtils.isEmpty(role.getRoleColumnValueSets())) {
            //添加角色菜单列值集
            roleColumnValueSetMapper.insert(role.getRoleColumnValueSets());
        }
        //删除角色菜单数据
        roleMenuMapper.deleteByRoleId(role.getRoleId());
        //添加角色菜单数据
        this.insertRoleMenu(role.getRoleMenuList());
        //删除角色部门数据
        roleCompanyDeptService.removeByRoleId(role.getRoleId());
        //添加角色部门数据
        insertRoleCompanyDept(role.getRoleCompanyDeptList(), role.getRoleId());
        //删除角色数据权限值
        dataAuthValueMapper.delete(role.getRoleId());
        //删除角色数据权限
        dataAuthMapper.delete(role.getRoleId());
        List<SysDataAuth> dataAuthList = role.getDataAuthList();
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            //添加角色数据权限
            dataAuthMapper.insertList(dataAuthList);
            //添加角色数据权限值
            dataAuthValueMapper.insertList(dataAuthList.stream().map(SysDataAuth::getDataAuthValueList).flatMap(Collection::stream).collect(Collectors.toList()));
        }
        //角色国际化修改
        roleMapper.updateTl(role);
        //角色主表修改
        return roleMapper.updateB(role);
    }

    /**
     * 删除
     *
     * @param ids 角色ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BizException("参数错误");
        }
        //删除角色列权限值集
        roleColumnValueSetMapper.deleteByRoleIds(ids);
        //删除列权限
        roleFunctionColumnMapper.deleteByRoleIds(ids);
        //角色菜单中间表删除
        roleMenuMapper.deleteByRoleIds(ids);
        //用户角色中间表删除
        userRoleMapper.deleteByRoleIds(ids);
        //删除角色数据权限值
        dataAuthValueMapper.deleteByObjectIds(ids);
        //删除角色数据权限
        dataAuthMapper.deleteByObjectIds(ids);
        //角色(多语言)删除
        roleMapper.deleteTl(ids);
        //角色主表删除
        int result = roleMapper.deleteB(ids);
        if (result > 0) {
            //当前角色ID=ids的缓存
            //缓存当前角色的key前缀
            String currentRoleIdPrefixKey = "current_role_id:*";
            Set<String> scanKeys = onlineUserController.scanKeys(currentRoleIdPrefixKey);
            for (String scanKey : scanKeys) {
                Object roleIdObj = valueOperations.get(scanKey);
                if (!ObjectUtils.isEmpty(roleIdObj) && ids.contains(Long.parseLong(roleIdObj.toString()))) {
                    //重置当前角色ID为空字符串
                    valueOperations.set(currentRoleIdPrefixKey, "");
                }
            }
        }
        return result;
    }

    /**
     * 根据角色ID查询角色信息(全量信息)
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 角色信息
     */
    @Override
    public SysRole findById(long roleId, String language) {
        //查询角色信息
        SysRole role = roleMapper.findById(roleId, language);
        if (ObjectUtils.isEmpty(role)) {
            throw new BizException("角色不存在!");
        }
        //根据角色ID查询拥有的菜单ID
        List<SysRoleMenu> roleMenuList = roleMenuMapper.findByRoleId(roleId);
        if (!CollectionUtils.isEmpty(roleMenuList)) {
            role.setRoleMenuList(roleMenuList);
        }
        //根据角色ID查询拥有的列权限
        List<SysRoleFunctionColumn> roleFunctionColumnList = roleFunctionColumnMapper.findByRoleId(roleId);
        if (!CollectionUtils.isEmpty(roleFunctionColumnList)) {
            role.setCheckedFunctionColumns(roleFunctionColumnList);
        }
        //根据角色ID查询列权限值集
        List<SysRoleColumnValueSet> roleColumnValueSets = roleColumnValueSetMapper.findByRoleId(roleId);
        if (!CollectionUtils.isEmpty(roleColumnValueSets)) {
            role.setRoleColumnValueSets(roleColumnValueSets);
        }
        //根据角色ID查询角色数据权限
        List<SysDataAuth> dataAuthList = dataAuthMapper.findByObjectId(roleId);
        if (!CollectionUtils.isEmpty(dataAuthList)) {
            List<Long> authIdList = dataAuthList.stream().map(SysDataAuth::getAuthId).collect(Collectors.toList());
            List<SysDataAuthValue> dataAuthValueList = dataAuthValueMapper.findByAuthIds(authIdList);
            dataAuthList.forEach(o -> {
                o.setDataAuthValueList(dataAuthValueList.stream().filter(i -> i.getAuthId().equals(o.getAuthId())).collect(Collectors.toList()));
            });
            role.setDataAuthList(dataAuthList);
        }
        return role;
    }

    /**
     * 根据角色ID查询角色信息(精简信息)
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 角色信息
     */
    @Override
    public SysRole findByIdMin(long roleId, String language) {
        //查询角色信息
        return roleMapper.findById(roleId, language);
    }

    /**
     * 根据角色编码查询角色信息
     *
     * @param roleCode 角色编码
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysRole findByCode(String roleCode, String language) {
        return roleMapper.findByCode(roleCode, language);
    }

    /**
     * 查询当前用户拥有的角色列表
     *
     * @param userId   用户ID
     * @param language 语言
     * @return 角色列表
     */
    @Override
    public List<SysRole> findByUserId(long userId, String language) {
        return roleMapper.findByUserId(userId, language);
    }

    /**
     * 根据角色名称查询角色信息
     *
     * @param roleName 角色名称
     * @param language 语言
     * @return 角色信息
     */
    @Override
    public SysRole findByName(String roleName, String language) {
        return roleMapper.findByName(roleName, language);
    }

    /**
     * 根据编码集合查询角色信息
     *
     * @param roleCodeList 编码集合
     * @param language     语言
     * @return 结果
     */
    @Override
    public List<SysRole> findByCodes(List<String> roleCodeList, String language) {
        return roleMapper.findByCodes(roleCodeList, language);
    }
}
package com.crrc.sys.controller.menu;

import com.crrc.sys.entity.menu.*;
import com.crrc.sys.mapper.mapperImpl.SysMenuMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/menu")
@Tag(name = "菜单管理测试接口", description = "用于测试菜单相关功能的接口")
public class SysMenuTestController {

    @Autowired
    private SysMenuMapperImpl sysMenuMapper;

    @PostMapping("/findByFuzzy")
    @Operation(summary = "模糊查询菜单列表")
    public List<SysMenu> findByFuzzy(@RequestBody SysMenuFindByPageParam param) {
        return sysMenuMapper.findByFuzzy(param);
    }

    @PostMapping("/insertTl")
    @Operation(summary = "批量插入菜单多语言数据")
    public int insertTl(@RequestBody List<SysMenuTl> menuTlList) {
        return sysMenuMapper.insertTl(menuTlList);
    }

    @PostMapping("/insert")
    @Operation(summary = "插入单个菜单")
    public int insert(@RequestBody SysMenuPO menu) {
        return sysMenuMapper.insert(menu);
    }

    @PostMapping("/insertList")
    @Operation(summary = "批量插入菜单")
    public int insertList(@RequestBody List<SysMenu> menuList) {
        return sysMenuMapper.insertList(menuList);
    }

    @GetMapping("/findBySubmenu")
    @Operation(summary = "查询所有子菜单")
    public List<SysMenu> findBySubmenu(@RequestParam String language) {
        return sysMenuMapper.findBySubmenu(language);
    }

    @PutMapping("/updateTl")
    @Operation(summary = "更新菜单多语言信息")
    public int updateTl(@RequestBody SysMenuTl menu) {
        return sysMenuMapper.updateTl(menu);
    }

    @PutMapping("/update")
    @Operation(summary = "更新菜单信息")
    public int update(@RequestBody SysMenuPO menu) {
        return sysMenuMapper.update(menu);
    }

    @PostMapping("/findByIds")
    @Operation(summary = "根据ID列表查询菜单")
    public List<SysMenu> findByIds(@RequestBody List<Long> menuIds, @RequestParam String language) {
        return sysMenuMapper.findByIds(menuIds, language);
    }

    @GetMapping("/findById")
    @Operation(summary = "根据ID查询单个菜单")
    public SysMenu findById(@RequestParam long menuId, @RequestParam String language) {
        return sysMenuMapper.findById(menuId, language);
    }

    @DeleteMapping("/deleteTl")
    @Operation(summary = "批量删除菜单多语言数据")
    public void deleteTl(@RequestBody List<Long> menuIdList) {
        sysMenuMapper.deleteTl(menuIdList);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除菜单")
    public int delete(@RequestBody List<Long> menuIdList) {
        return sysMenuMapper.delete(menuIdList);
    }

    @GetMapping("/findByParentId")
    @Operation(summary = "根据父菜单ID查询子菜单")
    public List<SysMenu> findByParentId(@RequestParam(required = false) Long parentId, @RequestParam String language) {
        return sysMenuMapper.findByParentId(parentId, language);
    }

    @GetMapping("/findAll")
    @Operation(summary = "查询所有菜单")
    public List<SysMenu> findAll(@RequestParam String language) {
        return sysMenuMapper.findAll(language);
    }

    @PostMapping("/findByCodes")
    @Operation(summary = "根据编码列表查询菜单")
    public List<SysMenu> findByCodes(@RequestBody List<String> codeList, @RequestParam String language) {
        return sysMenuMapper.findByCodes(codeList, language);
    }

    @GetMapping("/findByRoleId")
    @Operation(summary = "根据角色ID查询菜单")
    public List<SysMenu> findByRoleId(@RequestParam long roleId, @RequestParam String language) {
        return sysMenuMapper.findByRoleId(roleId, language);
    }

    @PostMapping("/findBindFunctionId")
    @Operation(summary = "查询菜单绑定的功能ID列表")
    public List<Long> findBindFunctionId(@RequestBody List<Long> menuIds) {
        return sysMenuMapper.findBindFunctionId(menuIds);
    }

    @PostMapping("/findBindChildId")
    @Operation(summary = "查询菜单绑定的子菜单ID列表")
    public List<Long> findBindChildId(@RequestBody List<Long> menuIds) {
        return sysMenuMapper.findBindChildId(menuIds);
    }

    @PostMapping("/findByParentIds")
    @Operation(summary = "根据父菜单ID列表查询子菜单ID")
    public List<Long> findByParentIds(@RequestBody List<Long> parentIdList) {
        return sysMenuMapper.findByParentIds(parentIdList);
    }

    @GetMapping("/findCount")
    @Operation(summary = "查询菜单总数")
    public int findCount() {
        return sysMenuMapper.findCount();
    }

    @GetMapping("/findByPage")
    @Operation(summary = "分页查询菜单")
    public List<SysMenu> findByPage(@RequestParam String language,
                                    @RequestParam int offset,
                                    @RequestParam int endset) {
        return sysMenuMapper.findByPage(language, offset, endset);
    }

    @GetMapping("/findCountByRoleId")
    @Operation(summary = "查询角色关联的菜单数量")
    public int findCountByRoleId(@RequestParam long roleId) {
        return sysMenuMapper.findCountByRoleId(roleId);
    }

    @GetMapping("/findPageByRoleId")
    @Operation(summary = "分页查询角色关联的菜单")
    public List<SysMenu> findPageByRoleId(@RequestParam long roleId,
                                          @RequestParam String language,
                                          @RequestParam int offset,
                                          @RequestParam int endset) {
        return sysMenuMapper.findPageByRoleId(roleId, language, offset, endset);
    }

    @GetMapping("/findRootMenu")
    @Operation(summary = "查询根菜单")
    public List<SysMenu> findRootMenu(@RequestParam(required = false) Long projectId,
                                      @RequestParam String language) {
        return sysMenuMapper.findRootMenu(projectId, language);
    }

    @PutMapping("/updateProjectId")
    @Operation(summary = "更新菜单项目ID")
    public void updateProjectId(@RequestParam Long menuId,
                                @RequestParam Long projectId) {
        sysMenuMapper.updateProjectId(menuId, projectId);
    }
}
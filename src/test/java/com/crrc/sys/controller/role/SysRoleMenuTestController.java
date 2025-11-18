package com.crrc.sys.controller.role;

import com.crrc.sys.mapper.mapperImpl.SysRoleMenuMapperImpl;
import com.crrc.sys.entity.role.SysRoleMenu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色菜单关系表测试控制器
 */
@RestController
@RequestMapping("/test/roleMenu")
@Tag(name = "角色菜单测试接口", description = "用于测试角色菜单相关功能")
public class SysRoleMenuTestController {

    @Autowired
    private SysRoleMenuMapperImpl sysRoleMenuMapper;

    @DeleteMapping("/deleteByMenuIds")
    @Operation(summary = "根据菜单ID列表删除角色菜单关系", description = "批量删除指定菜单ID对应的所有角色菜单关系")
    public void deleteByMenuIds(@RequestBody List<Long> menuIdList) {
        sysRoleMenuMapper.deleteByMenuIds(menuIdList);
    }

    @PostMapping("/insert")
    @Operation(summary = "批量插入角色菜单关系", description = "将角色与菜单的关联关系批量存入数据库")
    public void insert(@RequestBody List<SysRoleMenu> roleMenuList) {
        sysRoleMenuMapper.insert(roleMenuList);
    }

    @DeleteMapping("/deleteByRoleId/{roleId}")
    @Operation(summary = "根据角色ID删除菜单关系", description = "删除指定角色ID对应的所有菜单关系")
    public void deleteByRoleId(@PathVariable long roleId) {
        sysRoleMenuMapper.deleteByRoleId(roleId);
    }

    @DeleteMapping("/deleteByRoleIds")
    @Operation(summary = "根据角色ID列表批量删除菜单关系", description = "批量删除多个角色ID对应的所有菜单关系")
    public void deleteByRoleIds(@RequestBody List<Long> roleIds) {
        sysRoleMenuMapper.deleteByRoleIds(roleIds);
    }

    @GetMapping("/findByRoleId/{roleId}")
    @Operation(summary = "根据角色ID查询菜单", description = "获取指定角色ID对应的所有菜单配置")
    public List<SysRoleMenu> findByRoleId(@PathVariable long roleId) {
        return sysRoleMenuMapper.findByRoleId(roleId);
    }
}
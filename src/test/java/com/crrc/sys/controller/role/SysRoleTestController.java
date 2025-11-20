package com.crrc.sys.controller.role;

import com.crrc.sys.entity.role.*;
import com.crrc.sys.mapper.mapperImpl.SysRoleMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色测试控制器
 */
@RestController
@RequestMapping("/test/sysRole")
@Tag(name = "系统角色测试接口", description = "用于测试SysRoleMapperImpl功能的控制器")
public class SysRoleTestController {

    @Autowired
    private SysRoleMapperImpl sysRoleMapper;

    @PostMapping("/findByFuzzy")
    @Operation(summary = "模糊查询角色列表")
    public List<SysRole> findByFuzzy(@RequestBody SysRoleFindByPageParam param) {
        return sysRoleMapper.findByFuzzy(param);
    }

    @PostMapping("/addTl")
    @Operation(summary = "批量添加角色多语言数据")
    public int addTl(@RequestBody List<SysRoleTl> roleTlList) {
        return sysRoleMapper.addTl(roleTlList);
    }

    @PostMapping("/addB")
    @Operation(summary = "添加角色基础数据")
    public int addB(@RequestBody SysRole role) {
        return sysRoleMapper.addB(role);
    }

    @PutMapping("/updateTl")
    @Operation(summary = "更新角色多语言数据")
    public int updateTl(@RequestBody SysRole role) {
        return sysRoleMapper.updateTl(role);
    }

    @PutMapping("/updateB")
    @Operation(summary = "更新角色基础数据")
    public int updateB(@RequestBody SysRole role) {
        return sysRoleMapper.updateB(role);
    }

    @DeleteMapping("/deleteTl")
    @Operation(summary = "批量删除角色多语言数据")
    public int deleteTl(@RequestBody List<Long> ids) {
        return sysRoleMapper.deleteTl(ids);
    }

    @DeleteMapping("/deleteB")
    @Operation(summary = "批量删除角色基础数据")
    public int deleteB(@RequestBody List<Long> ids) {
        return sysRoleMapper.deleteB(ids);
    }

    @GetMapping("/findById")
    @Operation(summary = "根据ID和语言查询角色详情")
    public SysRole findById(@RequestParam long roleId, @RequestParam String language) {
        return sysRoleMapper.findById(roleId, language);
    }

    @GetMapping("/findByCode")
    @Operation(summary = "根据角色编码和语言查询角色")
    public SysRole findByCode(@RequestParam String roleCode, @RequestParam String language) {
        return sysRoleMapper.findByCode(roleCode, language);
    }

    @GetMapping("/findByUserId")
    @Operation(summary = "根据用户ID和语言查询角色列表")
    public List<SysRole> findByUserId(@RequestParam long userId, @RequestParam String language) {
        return sysRoleMapper.findByUserId(userId, language);
    }

    @GetMapping("/findByName")
    @Operation(summary = "根据角色名称和语言查询角色")
    public SysRole findByName(@RequestParam String roleName, @RequestParam String language) {
        return sysRoleMapper.findByName(roleName, language);
    }

    @PostMapping("/findByCodes")
    @Operation(summary = "根据角色编码列表和语言批量查询角色")
    public List<SysRole> findByCodes(@RequestBody List<String> roleCodeList, @RequestParam String language) {
        return sysRoleMapper.findByCodes(roleCodeList, language);
    }

    @GetMapping("/findRoleIdList")
    @Operation(summary = "查询所有角色ID列表")
    public List<Long> findRoleIdList() {
        return sysRoleMapper.findRoleIdList();
    }
}
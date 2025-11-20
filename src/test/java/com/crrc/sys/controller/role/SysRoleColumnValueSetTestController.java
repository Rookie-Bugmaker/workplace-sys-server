package com.crrc.sys.controller.role;

import com.crrc.sys.mapper.mapperImpl.SysRoleColumnValueSetMapperImpl;
import com.crrc.sys.entity.role.SysRoleColumnValueSet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色列权限值集表-测试控制器
 * @author 李江涛
 * @date 2025-11-20
 */
@RestController
@RequestMapping("/test/roleColumnValueSet")
@Tag(name = "角色列权限值集测试接口", description = "用于测试角色列权限值集相关操作")
public class SysRoleColumnValueSetTestController {

    @Autowired
    private SysRoleColumnValueSetMapperImpl sysRoleColumnValueSetMapper;

    @PostMapping("/insert")
    @Operation(summary = "批量插入角色列权限值集", 
               description = "将角色与列权限值集的关联关系批量存入数据库")
    public void insert(@RequestBody List<SysRoleColumnValueSet> roleMenuColumnValueSets) {
        sysRoleColumnValueSetMapper.insert(roleMenuColumnValueSets);
    }

    @DeleteMapping("/deleteByRoleIds")
    @Operation(summary = "根据角色ID批量删除", 
               description = "根据角色ID列表删除对应的列权限值集关联关系")
    public void deleteByRoleIds(@RequestBody List<Long> roleIdList) {
        sysRoleColumnValueSetMapper.deleteByRoleIds(roleIdList);
    }

    @GetMapping("/findByRoleId/{roleId}")
    @Operation(summary = "查询角色列权限值集", 
               description = "根据角色ID查询其关联的所有列权限值集配置")
    public List<SysRoleColumnValueSet> findByRoleId(@PathVariable long roleId) {
        return sysRoleColumnValueSetMapper.findByRoleId(roleId);
    }
}
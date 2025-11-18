package com.crrc.sys.controller.role;

import com.crrc.sys.mapper.mapperImpl.SysRoleFunctionColumnMapperImpl;
import com.crrc.sys.entity.role.SysRoleFunctionColumn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色列权限关系表测试控制器
 */
@RestController
@RequestMapping("/test/roleFunctionColumn")
@Tag(name = "角色列权限测试接口", description = "用于测试角色列权限相关功能")
public class SysRoleFunctionColumnTestController {

    @Autowired
    private SysRoleFunctionColumnMapperImpl sysRoleFunctionColumnMapper;

    @PostMapping("/insert")
    @Operation(summary = "批量插入角色列权限关系", description = "将角色与列权限的关联关系批量存入数据库")
    public void insert(@RequestBody List<SysRoleFunctionColumn> functionColumnList) {
        sysRoleFunctionColumnMapper.insert(functionColumnList);
    }

    @DeleteMapping("/deleteByRoleIds")
    @Operation(summary = "根据角色ID列表删除列权限关系", description = "批量删除指定角色ID对应的所有列权限关系")
    public void deleteByRoleIds(@RequestBody List<Long> roleIdList) {
        sysRoleFunctionColumnMapper.deleteByRoleIds(roleIdList);
    }

    @GetMapping("/findByRoleId/{roleId}")
    @Operation(summary = "根据角色ID查询列权限", description = "获取指定角色ID对应的所有列权限配置")
    public List<SysRoleFunctionColumn> findByRoleId(@PathVariable long roleId) {
        return sysRoleFunctionColumnMapper.findByRoleId(roleId);
    }
}
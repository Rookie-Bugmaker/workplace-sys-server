package com.crrc.sys.controller.api;

import com.crrc.sys.entity.api.SysApiAuth;
import com.crrc.sys.entity.api.SysApiAuthPageDTO;
import com.crrc.sys.mapper.mapperImpl.SysApiAuthMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/apiAuth")
@Tag(name = "API权限管理测试接口", description = "用于测试API权限管理相关功能")
public class SysApiAuthTestController {

    @Autowired
    private SysApiAuthMapperImpl sysApiAuthMapper;

    @PostMapping("/findByFuzzy")
    @Operation(summary = "模糊查询API权限", description = "根据URI、类型、描述进行模糊匹配查询")
    public List<SysApiAuth> findByFuzzy(@RequestBody SysApiAuthPageDTO param) {
        return sysApiAuthMapper.findByFuzzy(param);
    }

    @PostMapping("/add")
    @Operation(summary = "新增API权限", description = "添加新的API权限记录")
    public int add(@RequestBody SysApiAuth apiAuth) {
        return sysApiAuthMapper.add(apiAuth);
    }

    @GetMapping("/findByAuthType")
    @Operation(summary = "按类型查询API权限", description = "根据权限类型精确查询")
    public List<SysApiAuth> findByAuthType(@RequestParam String authType) {
        return sysApiAuthMapper.findByAuthType(authType);
    }

    @PutMapping("/update")
    @Operation(summary = "更新API权限", description = "修改指定ID的API权限信息")
    public int update(@RequestBody SysApiAuth apiAuth) {
        return sysApiAuthMapper.update(apiAuth);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除API权限", description = "根据ID列表批量删除权限记录")
    public int delete(@RequestBody List<Long> ids) {
        return sysApiAuthMapper.delete(ids);
    }

    @GetMapping("/findById")
    @Operation(summary = "按ID查询详情", description = "根据权限ID获取详细信息")
    public SysApiAuth findById(@RequestParam long authId) {
        return sysApiAuthMapper.findById(authId);
    }

    @PostMapping("/findByAuthIdList")
    @Operation(summary = "批量ID查询", description = "根据权限ID列表批量查询")
    public List<SysApiAuth> findByAuthIdList(@RequestBody List<Long> authIdList) {
        return sysApiAuthMapper.findByAuthIdList(authIdList);
    }
}
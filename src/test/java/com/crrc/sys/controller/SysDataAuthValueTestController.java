package com.crrc.sys.controller;

import com.crrc.sys.entity.auth.SysDataAuthValue;
import com.crrc.sys.mapper.mapperImpl.SysDataAuthValueMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据权限值测试控制器
 * @author 李江涛
 * @date 2025-11-13
 */
@RestController
@RequestMapping("/test/dataAuthValue")
@Tag(name = "数据权限值测试接口", description = "用于测试SysDataAuthValueMapper相关功能")
public class SysDataAuthValueTestController {

    @Autowired
    private SysDataAuthValueMapperImpl sysDataAuthValueMapper;

    @PostMapping("/insertBatch")
    @Operation(summary = "批量插入数据权限值", 
               description = "将数据权限值列表批量插入数据库，自动转换实体类型")
    public void insertList(@RequestBody List<SysDataAuthValue> dataAuthValueList) {
        sysDataAuthValueMapper.insertList(dataAuthValueList);
    }

    @DeleteMapping("/deleteByObjectId/{objectId}")
    @Operation(summary = "根据对象ID删除权限值", 
               description = "级联删除与指定对象ID关联的所有权限值记录")
    public void delete(@PathVariable long objectId) {
        sysDataAuthValueMapper.delete(objectId);
    }

    @DeleteMapping("/deleteByObjectIds")
    @Operation(summary = "批量删除对象权限值", 
               description = "根据对象ID列表批量删除关联的权限值记录")
    public void deleteByObjectIds(@RequestBody List<Long> objectIds) {
        sysDataAuthValueMapper.deleteByObjectIds(objectIds);
    }

    @GetMapping("/findByAuthIds")
    @Operation(summary = "查询权限值列表", 
               description = "根据权限ID列表查询对应的权限值详细信息")
    public List<SysDataAuthValue> findByAuthIds(@RequestParam List<Long> authIdList) {
        return sysDataAuthValueMapper.findByAuthIds(authIdList);
    }
}
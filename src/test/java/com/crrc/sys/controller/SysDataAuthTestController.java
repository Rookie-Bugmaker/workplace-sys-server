package com.crrc.sys.controller;

import com.crrc.sys.entity.auth.SysDataAuth;
import com.crrc.sys.mapper.mapperImpl.SysDataAuthMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据权限测试控制器
 * @author 李江涛
 * @date 2025-11-13
 */
@RestController
@RequestMapping("/test/dataAuth")
@Tag(name = "数据权限测试接口", description = "用于测试数据权限Mapper功能的控制器")
public class SysDataAuthTestController {

    @Autowired
    private SysDataAuthMapperImpl sysDataAuthMapper;

    @PostMapping("/insertBatch")
    @Operation(summary = "批量插入数据权限记录", 
              description = "将多条数据权限记录批量插入数据库")
    public void testInsertList(@RequestBody List<SysDataAuth> dataAuthList) {
        sysDataAuthMapper.insertList(dataAuthList);
    }

    @DeleteMapping("/delete/{objectId}")
    @Operation(summary = "根据对象ID删除权限记录", 
              description = "通过单个对象ID删除对应的数据权限记录")
    public void testDelete(@PathVariable long objectId) {
        sysDataAuthMapper.delete(objectId);
    }

    @DeleteMapping("/deleteBatch")
    @Operation(summary = "批量删除权限记录", 
              description = "通过对象ID列表批量删除数据权限记录")
    public void testDeleteByObjectIds(@RequestBody List<Long> objectIds) {
        sysDataAuthMapper.deleteByObjectIds(objectIds);
    }

    @GetMapping("/query/{objectId}")
    @Operation(summary = "根据对象ID查询权限", 
              description = "通过对象ID查询关联的数据权限列表")
    public List<SysDataAuth> testFindByObjectId(@PathVariable long objectId) {
        return sysDataAuthMapper.findByObjectId(objectId);
    }
}
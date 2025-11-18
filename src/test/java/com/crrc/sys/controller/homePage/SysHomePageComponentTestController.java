package com.crrc.sys.controller.homePage;

import com.crrc.sys.entity.homepage.*;
import com.crrc.sys.mapper.mapperImpl.SysHomePageComponentMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 主页组件管理测试控制器
 */
@RestController
@RequestMapping("/test/homePageComponent")
@Tag(name = "主页组件管理测试接口", description = "用于测试主页组件相关功能")
public class SysHomePageComponentTestController {

    @Autowired
    private SysHomePageComponentMapperImpl sysHomePageComponentMapper;

    @PostMapping("/add")
    @Operation(summary = "添加主页组件", description = "添加一个新的主页组件")
    public Integer add(@RequestBody SysHomePageComponent param) {
        return sysHomePageComponentMapper.add(param);
    }

    @GetMapping("/findList")
    @Operation(summary = "查询主页组件列表", description = "根据条件查询主页组件列表")
    public List<SysHomePageComponent> findList(HomePageParam param) {
        return sysHomePageComponentMapper.findList(param);
    }

    @DeleteMapping("/deleteById/{componentId}")
    @Operation(summary = "删除主页组件", description = "根据组件ID删除主页组件")
    public Integer deleteById(@PathVariable Long componentId) {
        return sysHomePageComponentMapper.deleteById(componentId);
    }

    @PutMapping("/updateById")
    @Operation(summary = "更新主页组件", description = "根据组件ID更新主页组件信息")
    public Integer updateById(@RequestBody SysHomePageComponent param) {
        return sysHomePageComponentMapper.updateById(param);
    }

    @PostMapping("/addUser")
    @Operation(summary = "添加组件用户关联", description = "批量添加组件与用户的关联关系")
    public Integer addUser(@RequestBody List<SysHomePageComponentUser> list) {
        return sysHomePageComponentMapper.addUser(list);
    }

    @GetMapping("/findUserByComponentId/{componentId}")
    @Operation(summary = "查询组件关联用户", description = "根据组件ID查询关联的用户ID列表")
    public List<Long> findUserByComponentId(@PathVariable Long componentId) {
        return sysHomePageComponentMapper.findUserByComponentId(componentId);
    }

    @GetMapping("/findComponentByUserId/{userId}")
    @Operation(summary = "查询用户关联组件", description = "根据用户ID查询关联的主页组件列表")
    public List<SysHomePageComponent> findComponentByUserId(@PathVariable Long userId) {
        return sysHomePageComponentMapper.findComponentByUserId(userId);
    }

    @GetMapping("/findComponentByUserType/{userType}")
    @Operation(summary = "查询用户类型关联组件", description = "根据用户类型查询关联的主页组件列表")
    public List<SysHomePageComponent> findComponentByUserType(@PathVariable String userType) {
        return sysHomePageComponentMapper.findComponentByUserType(userType);
    }

    @DeleteMapping("/deleteUserByComponentId/{componentId}")
    @Operation(summary = "删除组件用户关联", description = "根据组件ID删除所有关联的用户关系")
    public Integer deleteUserByComponentId(@PathVariable Long componentId) {
        return sysHomePageComponentMapper.deleteUserByComponentId(componentId);
    }
}
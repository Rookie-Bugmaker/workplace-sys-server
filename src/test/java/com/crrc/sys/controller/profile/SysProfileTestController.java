package com.crrc.sys.controller.profile;

import com.crrc.sys.entity.profile.*;
import com.crrc.sys.mapper.mapperImpl.SysProfileMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/profile")
@Tag(name = "配置文件测试接口", description = "用于测试配置文件相关功能")
public class SysProfileTestController {

    @Autowired
    private SysProfileMapperImpl sysProfileMapper;

    @PostMapping("/findByFuzzy")
    @Operation(summary = "模糊查询配置文件", description = "根据条件模糊查询配置文件列表")
    public List<SysProfile> findByFuzzy(@RequestBody SysProfileFindByPageParam param) {
        return sysProfileMapper.findByFuzzy(param);
    }

    @PostMapping("/insert")
    @Operation(summary = "新增配置文件", description = "新增一个配置文件记录")
    public int insert(@RequestBody SysProfile profile) {
        return sysProfileMapper.insert(profile);
    }

    @PutMapping("/update")
    @Operation(summary = "更新配置文件", description = "更新指定的配置文件信息")
    public int update(@RequestBody SysProfile profile) {
        return sysProfileMapper.update(profile);
    }

    @GetMapping("/findById")
    @Operation(summary = "根据ID查询", description = "根据ID查询指定的配置文件详情")
    public SysProfile findById(@RequestParam long profileId, @RequestParam String language) {
        return sysProfileMapper.findById(profileId, language);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除", description = "根据ID列表批量删除配置文件")
    public int delete(@RequestBody List<Long> profileIdList) {
        return sysProfileMapper.delete(profileIdList);
    }

    @GetMapping("/findByCode")
    @Operation(summary = "根据编码查询", description = "根据配置文件编码查询详情")
    public SysProfile findByCode(@RequestParam String profileCode) {
        return sysProfileMapper.findByCode(profileCode);
    }

    @PostMapping("/executeSql")
    @Operation(summary = "执行动态SQL", description = "执行配置文件中定义的动态SQL查询")
    public List<ValueSet> executeSql(@RequestBody SysProfileValuePageSearch search) {
        return sysProfileMapper.executeSql(search);
    }

    @GetMapping("/findByType")
    @Operation(summary = "根据类型查询", description = "根据配置文件类型查询列表")
    public List<SysProfile> findByType(@RequestParam String profileType, @RequestParam String language) {
        return sysProfileMapper.findByType(profileType, language);
    }

    @PostMapping("/findByIds")
    @Operation(summary = "批量ID查询", description = "根据ID列表批量查询配置文件")
    public List<SysProfile> findByIds(@RequestBody List<Long> profileIdList, @RequestParam String language) {
        return sysProfileMapper.findByIds(profileIdList, language);
    }
}
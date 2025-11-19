package com.crrc.sys.controller.profile;

import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.entity.profile.SysProfileTl;
import com.crrc.sys.mapper.mapperImpl.SysProfileTlMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/profileTl")
@Tag(name = "配置文件国际化测试接口", description = "用于测试配置文件国际化相关功能")
public class SysProfileTlTestController {

    @Autowired
    private SysProfileTlMapperImpl sysProfileTlMapper;

    @PostMapping("/insertList")
    @Operation(summary = "批量新增国际化", description = "批量新增配置文件国际化信息")
    public void insertList(@RequestBody List<SysProfileTl> tlList) {
        sysProfileTlMapper.insertList(tlList);
    }

    @PutMapping("/update")
    @Operation(summary = "更新国际化", description = "更新指定的配置文件国际化信息")
    public void update(@RequestBody SysProfile profile) {
        sysProfileTlMapper.update(profile);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除国际化", description = "根据ID列表批量删除配置文件国际化信息")
    public void delete(@RequestBody List<Long> profileIdList) {
        sysProfileTlMapper.delete(profileIdList);
    }
}
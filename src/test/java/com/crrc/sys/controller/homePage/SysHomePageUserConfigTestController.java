package com.crrc.sys.controller.homePage;

import com.crrc.sys.entity.homepage.SysHomePageUserConfig;
import com.crrc.sys.entity.homepage.SysHomePageUserConfigDBEntity;
import com.crrc.sys.mapper.mapperImpl.SysHomePageUserConfigMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/homePageUserConfig")
@Tag(name = "用户主页配置测试接口")
public class SysHomePageUserConfigTestController {

    @Autowired
    private SysHomePageUserConfigMapperImpl sysHomePageUserConfigMapper;

    @PostMapping("/add")
    @Operation(summary = "添加用户主页配置")
    public Integer add(@RequestBody SysHomePageUserConfig param) {
        return sysHomePageUserConfigMapper.add(param);
    }

    @GetMapping("/findByUserId/{userId}")
    @Operation(summary = "根据用户ID查询配置")
    public SysHomePageUserConfig findByUserId(@PathVariable Long userId) {
        return sysHomePageUserConfigMapper.findByUserId(userId);
    }

    @DeleteMapping("/deleteByUserId/{userId}")
    @Operation(summary = "根据用户ID删除配置")
    public Integer deleteByUserId(@PathVariable Long userId) {
        return sysHomePageUserConfigMapper.deleteByUserId(userId);
    }
}
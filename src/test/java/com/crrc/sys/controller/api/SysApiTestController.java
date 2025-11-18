package com.crrc.sys.controller.api;

import com.crrc.sys.entity.api.SysApi;
import com.crrc.sys.entity.api.SysApiFindByPageParam;
import com.crrc.sys.mapper.SysApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/test/sysApi")
@Tag(name = "API接口管理", description = "系统API接口的增删改查操作")
public class SysApiTestController {

    @Resource
    private SysApiMapper sysApiMapper;

    @PostMapping("/findByFuzzy")
    @Operation(summary = "模糊查询API接口", 
               description = "根据API名称、URI或描述进行模糊匹配查询")
    public List<SysApi> findByFuzzy(@RequestBody SysApiFindByPageParam param) {
        return sysApiMapper.findByFuzzy(param);
    }

    @PostMapping
    @Operation(summary = "新增API接口", 
               description = "创建新的API接口记录")
    public int insert(@RequestBody SysApi api) {
        return sysApiMapper.insert(api);
    }

    @PutMapping
    @Operation(summary = "更新API接口", 
               description = "根据ID更新API接口的全部字段")
    public int update(@RequestBody SysApi api) {
        return sysApiMapper.update(api);
    }

    @DeleteMapping
    @Operation(summary = "批量删除API接口", 
               description = "根据ID列表批量删除API接口")
    public int delete(@RequestBody List<Long> ids) {
        return sysApiMapper.delete(ids);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询API接口", 
               description = "通过主键ID获取单个API接口详情")
    public SysApi findById(@PathVariable long id) {
        return sysApiMapper.findById(id);
    }

    @PostMapping("/findByIds")
    @Operation(summary = "批量ID查询API接口", 
               description = "通过ID列表批量获取API接口")
    public List<SysApi> findByIds(@RequestBody List<Long> ids) {
        return sysApiMapper.findByIds(ids);
    }

    @GetMapping("/findByUri")
    @Operation(summary = "根据URI查询API接口", 
               description = "通过完整URI路径精确匹配查询API")
    public SysApi findByUri(@RequestParam String uri) {
        return sysApiMapper.findByUri(uri);
    }

    @GetMapping("/findByMethodAndUri")
    @Operation(summary = "根据方法和URI查询API", 
               description = "通过HTTP方法和URI组合精确查询API")
    public SysApi findByMethodAndUri(
            @RequestParam String apiMethod, 
            @RequestParam String apiUri) {
        return sysApiMapper.findByMethodAndUri(apiMethod, apiUri);
    }

    @GetMapping("/all")
    @Operation(summary = "获取全部API接口", 
               description = "查询系统中所有API接口列表")
    public List<SysApi> findAll() {
        return sysApiMapper.findAll();
    }

    @PostMapping("/findByMethodAndUris")
    @Operation(summary = "批量方法+URI查询", 
               description = "根据API对象列表中的方法和URI批量查询")
    public List<SysApi> findByMethodAndUris(@RequestBody List<SysApi> apiList) {
        return sysApiMapper.findByMethodAndUris(apiList);
    }

    @PostMapping("/findByUris")
    @Operation(summary = "批量URI查询", 
               description = "根据URI列表批量查询API接口")
    public List<SysApi> findByUris(@RequestBody List<String> apiUriList) {
        return sysApiMapper.findByUris(apiUriList);
    }
}
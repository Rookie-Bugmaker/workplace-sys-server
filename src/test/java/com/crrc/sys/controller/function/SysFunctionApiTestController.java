package com.crrc.sys.controller.function;

import com.crrc.sys.entity.function.SysFunctionApi;
import com.crrc.sys.entity.function.SysFunctionApiBO;
import com.crrc.sys.entity.function.SysFunctionApiPO;
import com.crrc.sys.mapper.SysFunctionApiMapper;
import com.crrc.sys.mapper.mapperImpl.SysFunctionApiMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/functionApi")
@Tag(name = "功能API关系测试控制器", description = "用于测试功能API关系相关接口")
public class SysFunctionApiTestController {

    @Autowired
    private SysFunctionApiMapperImpl sysFunctionApiMapper;

    @DeleteMapping("/byFunctionId/{functionId}")
    @Operation(summary = "根据功能ID删除关联关系", description = "删除指定功能ID下的所有API关联")
    public int deleteByFunctionId(@PathVariable Long functionId) {
        return sysFunctionApiMapper.deleteByFunctionId(functionId);
    }

    @DeleteMapping("/byFunctionIds")
    @Operation(summary = "批量删除功能关联关系", description = "根据功能ID列表批量删除API关联")
    public int deleteByFunctionIds(@RequestBody List<Long> functionIdList) {
        return sysFunctionApiMapper.deleteByFunctionIds(functionIdList);
    }

    @PostMapping
    @Operation(summary = "批量新增功能API关联", description = "批量插入功能与API的关联关系")
    public int insert(@RequestBody List<SysFunctionApiPO> functionApiList) {
        return sysFunctionApiMapper.insert(functionApiList);
    }

    @GetMapping("/byFunctionIds")
    @Operation(summary = "查询功能关联的API列表", description = "根据功能ID列表查询关联的API信息")
    public List<SysFunctionApiBO> findByFunctionIds(@RequestParam List<Long> functionIdList) {
        return sysFunctionApiMapper.findByFunctionIds(functionIdList);
    }

    @GetMapping("/byFunctionId/{functionId}")
    @Operation(summary = "查询单个功能关联的API", description = "根据功能ID查询关联的API信息")
    public SysFunctionApiBO findByFunctionId(@PathVariable Long functionId) {
        return sysFunctionApiMapper.findByFunctionId(functionId);
    }

    @GetMapping("/count")
    @Operation(summary = "统计所有功能API关联数量", description = "获取系统中所有功能API关联的总数")
    public int findAllFunctionApi() {
        return sysFunctionApiMapper.findAllFunctionApi();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询功能API关联", description = "分页查询功能与API的关联关系")
    public List<SysFunctionApi> findPageFunctionApi(
            @RequestParam Integer offset,
            @RequestParam Integer endset) {
        return sysFunctionApiMapper.findPageFunctionApi(offset, endset);
    }

    @DeleteMapping("/byApiIds")
    @Operation(summary = "根据API ID列表删除关联", description = "根据API ID列表批量删除功能关联关系")
    public int deleteByApiIds(@RequestBody List<Long> ids) {
        return sysFunctionApiMapper.deleteByApiIds(ids);
    }
}
package com.crrc.sys.controller.function;

import com.crrc.sys.entity.function.column.SysFunctionColumn;
import com.crrc.sys.entity.function.column.SysFunctionColumnTl;
import com.crrc.sys.mapper.mapperImpl.SysFunctionColumnMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/functionColumn")
@Tag(name = "列权限测试接口", description = "列权限相关功能的测试接口")
public class SysFunctionColumnTestController {

    @Autowired
    private SysFunctionColumnMapperImpl sysFunctionColumnMapper;

    @GetMapping("/findByParentIds")
    @Operation(summary = "根据父ID列表查询列权限")
    public List<SysFunctionColumn> findByParentIds(@RequestParam List<Long> blockIdList, 
                                                  @RequestParam String language) {
        return sysFunctionColumnMapper.findByParentIds(blockIdList, language);
    }

    @PostMapping("/insertTl")
    @Operation(summary = "批量插入列权限多语言数据")
    public void insertTl(@RequestBody List<SysFunctionColumnTl> functionColumnTlList) {
        sysFunctionColumnMapper.insertTl(functionColumnTlList);
    }

    @PostMapping("/insert")
    @Operation(summary = "插入列权限")
    public int insert(@RequestBody SysFunctionColumn functionColumn) {
        return sysFunctionColumnMapper.insert(functionColumn);
    }

    @GetMapping("/findByBlockCode")
    @Operation(summary = "根据区块代码查询列权限")
    public SysFunctionColumn findByBlockCode(@RequestParam String blockCode, 
                                           @RequestParam String language) {
        return sysFunctionColumnMapper.findByBlockCode(blockCode, language);
    }

    @GetMapping("/findByColumnCode")
    @Operation(summary = "根据列代码查询列权限")
    public SysFunctionColumn findByColumnCode(@RequestParam String columnCode, 
                                            @RequestParam String language,
                                            @RequestParam long blockId) {
        return sysFunctionColumnMapper.findByColumnCode(columnCode, language, blockId);
    }

    @PutMapping("/updateTl")
    @Operation(summary = "更新列权限多语言数据")
    public void updateTl(@RequestBody SysFunctionColumn functionColumn) {
        sysFunctionColumnMapper.updateTl(functionColumn);
    }

    @PutMapping("/update")
    @Operation(summary = "更新列权限")
    public int update(@RequestBody SysFunctionColumn functionColumn) {
        return sysFunctionColumnMapper.update(functionColumn);
    }

    @DeleteMapping("/deleteTlByIds")
    @Operation(summary = "根据ID列表删除列权限多语言数据")
    public void deleteTlByIds(@RequestParam List<Long> ids) {
        sysFunctionColumnMapper.deleteTlByIds(ids);
    }

    @DeleteMapping("/deleteByIds")
    @Operation(summary = "根据ID列表删除列权限")
    public int deleteByIds(@RequestParam List<Long> ids) {
        return sysFunctionColumnMapper.deleteByIds(ids);
    }

    @GetMapping("/findByFunctionIds")
    @Operation(summary = "根据功能ID列表查询列权限")
    public List<SysFunctionColumn> findByFunctionIds(@RequestParam List<Long> functionIdList, 
                                                   @RequestParam String language) {
        return sysFunctionColumnMapper.findByFunctionIds(functionIdList, language);
    }

    @GetMapping("/getHaveNoColumnCode")
    @Operation(summary = "获取没有列代码的列权限")
    public List<SysFunctionColumn> getHaveNoColumnCode(@RequestParam long roleId, 
                                                     @RequestParam String blockCode) {
        return sysFunctionColumnMapper.getHaveNoColumnCode(roleId, blockCode);
    }

    @GetMapping("/findByColumnIds")
    @Operation(summary = "根据列ID列表查询列权限")
    public List<SysFunctionColumn> findByColumnIds(@RequestParam List<Long> columnIdList, 
                                                 @RequestParam String language) {
        return sysFunctionColumnMapper.findByColumnIds(columnIdList, language);
    }
}
package com.crrc.sys.controller.function;

import com.crrc.sys.entity.function.SysFunctionTl;
import com.crrc.sys.mapper.SysFunctionTlMapper;
import com.crrc.sys.mapper.mapperImpl.SysFunctionTlMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/functionTl")
@Tag(name = "系统功能多语言测试接口", description = "系统功能多语言相关功能的测试接口")
public class SysFunctionTlTestController {

    @Autowired
    private SysFunctionTlMapperImpl sysFunctionTlMapper;

    @PostMapping("/insertTl")
    @Operation(summary = "批量插入系统功能多语言数据")
    public int insertTl(@RequestBody List<SysFunctionTl> functionTlList) {
        return sysFunctionTlMapper.insertTl(functionTlList);
    }

    @DeleteMapping("/deleteTl")
    @Operation(summary = "根据功能ID列表删除多语言数据")
    public int deleteTl(@RequestParam List<Long> functionIds) {
        return sysFunctionTlMapper.deleteTl(functionIds);
    }

    @GetMapping("/findByFunctionIds")
    @Operation(summary = "根据功能ID列表查询多语言数据")
    public List<SysFunctionTl> findByFunctionIds(@RequestParam List<Long> functionIdList) {
        return sysFunctionTlMapper.findByFunctionIds(functionIdList);
    }
}
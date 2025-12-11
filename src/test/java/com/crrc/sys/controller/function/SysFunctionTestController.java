package com.crrc.sys.controller.function;

import com.crrc.sys.entity.function.*;
import com.crrc.sys.mapper.mapperImpl.SysFunctionMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/function")
@Tag(name = "系统功能测试接口", description = "系统功能相关功能的测试接口")
public class SysFunctionTestController {

    @Autowired
    private SysFunctionMapperImpl sysFunctionMapper;

    @PostMapping("/findByFuzzy")
    @Operation(summary = "模糊查询系统功能")
    public List<SysFunction> findByFuzzy(@RequestBody SysFunctionFindByPageParam param) {
        return sysFunctionMapper.findByFuzzy(param);
    }

    @PostMapping("/insert")
    @Operation(summary = "插入系统功能")
    public int insert(@RequestBody SysFunction function) {
        return sysFunctionMapper.insert(function);
    }

    @GetMapping("/findByParentIds")
    @Operation(summary = "根据父ID列表查询系统功能")
    public List<SysFunction> findByParentIds(@RequestParam(required = false) List<Long> parentIdList,
                                             @RequestParam String language) {
        return sysFunctionMapper.findByParentIds(parentIdList, language);
    }

    @PutMapping("/updateTl")
    @Operation(summary = "更新系统功能多语言数据")
    public int updateTl(@RequestBody SysFunctionTl functionTl) {
        return sysFunctionMapper.updateTl(functionTl);
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统功能")
    public int update(@RequestBody SysFunction function) {
        return sysFunctionMapper.update(function);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统功能")
    public int delete(@RequestParam List<Long> functionIds) {
        return sysFunctionMapper.delete(functionIds);
    }

    @GetMapping("/findById")
    @Operation(summary = "根据ID查询系统功能")
    public SysFunction findById(@RequestParam long functionId,
                                @RequestParam String language) {
        return sysFunctionMapper.findById(functionId, language);
    }

    @GetMapping("/findByCode")
    @Operation(summary = "根据代码查询系统功能")
    public SysFunction findByCode(@RequestParam String functionCode,
                                  @RequestParam String language) {
        List<SysFunction> functionList = sysFunctionMapper.findByCode(functionCode, language);
        if (CollectionUtils.isEmpty(functionList)) {
            return null;
        }
        return functionList.get(0);
    }

    @GetMapping("/findByIds")
    @Operation(summary = "根据ID列表查询系统功能")
    public List<SysFunction> findByIds(@RequestParam List<Long> functionIdList,
                                       @RequestParam String language) {
        return sysFunctionMapper.findByIds(functionIdList, language);
    }

    @GetMapping("/findAll")
    @Operation(summary = "查询所有系统功能")
    public List<SysFunction> findAll(@RequestParam String language) {
        return sysFunctionMapper.findAll(language);
    }

    @GetMapping("/findByRoleId")
    @Operation(summary = "根据角色ID查询系统功能")
    public List<SysFunction> findByRoleId(@RequestParam long roleId,
                                          @RequestParam String language) {
        return sysFunctionMapper.findByRoleId(roleId, language);
    }

    @GetMapping("/findByPageId")
    @Operation(summary = "根据页面ID查询系统功能")
    public SysFunction findByPageId(@RequestParam long pageId,
                                    @RequestParam String language) {
        return sysFunctionMapper.findByPageId(pageId, language);
    }

    @GetMapping("/findButtonById")
    @Operation(summary = "根据功能ID查询按钮")
    public List<SysFunction> findButtonById(@RequestParam long functionId) {
        return sysFunctionMapper.findButtonById(functionId);
    }

    @GetMapping("/findByRouteName")
    @Operation(summary = "根据路由名称查询系统功能")
    public SysFunction findByRouteName(@RequestParam String routeName) {
        return sysFunctionMapper.findByRouteName(routeName);
    }

    @GetMapping("/findCount")
    @Operation(summary = "查询系统功能总数")
    public int findCount() {
        return sysFunctionMapper.findCount();
    }

    @GetMapping("/findByPage")
    @Operation(summary = "分页查询系统功能")
    public List<SysFunction> findByPage(@RequestParam String language,
                                        @RequestParam int offset,
                                        @RequestParam int endset) {
        return sysFunctionMapper.findByPage(language, offset, endset);
    }

    @GetMapping("/findCountByRoleId")
    @Operation(summary = "根据角色ID查询系统功能数量")
    public int findCountByRoleId(@RequestParam long roleId) {
        return sysFunctionMapper.findCountByRoleId(roleId);
    }

    @GetMapping("/findPageByRoleId")
    @Operation(summary = "根据角色ID分页查询系统功能")
    public List<SysFunction> findPageByRoleId(@RequestParam long roleId,
                                              @RequestParam String language,
                                              @RequestParam int offset,
                                              @RequestParam int endset) {
        return sysFunctionMapper.findPageByRoleId(roleId, language, offset, endset);
    }

    @GetMapping("/findNotParent")
    @Operation(summary = "查询非父级系统功能数量")
    public int findNotParent(@RequestParam String language) {
        return sysFunctionMapper.findNotParent(language);
    }

    @GetMapping("/findPageByNotParent")
    @Operation(summary = "分页查询非父级系统功能")
    public List<SysFunction> findPageByNotParent(@RequestParam String language,
                                                 @RequestParam Integer offset,
                                                 @RequestParam Integer endset) {
        return sysFunctionMapper.findPageByNotParent(language, offset, endset);
    }

    @PutMapping("/updateProjectId")
    @Operation(summary = "更新项目ID")
    public void updateProjectId(@RequestParam long functionId,
                                @RequestParam Long projectId) {
        sysFunctionMapper.updateProjectId(functionId, projectId);
    }
}
package com.crrc.sys.controller.org.person;

import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.entity.org.person.SysOrgPersonTl;
import com.crrc.sys.mapper.mapperImpl.SysOrgPersonMapperImpl;
import com.crrc.sys.mapper.mapperImpl.SysOrgPersonTlMapperImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "组织架构-人员国际化测试接口")
@RestController
@RequestMapping("/test/org/personTl")
public class SysOrgPersonTlTestController {

    @Autowired
    private SysOrgPersonTlMapperImpl sysOrgPersonTlMapper;

    @Operation(summary = "批量新增人员国际化信息")
    @PostMapping("/insertList")
    public void insertList(@RequestBody List<SysOrgPersonTl> personTlList) {
        sysOrgPersonTlMapper.insertList(personTlList);
    }

    @Operation(summary = "更新人员国际化信息")
    @PutMapping("/update")
    public void update(@RequestBody SysOrgPerson person) {
        sysOrgPersonTlMapper.update(person);
    }

    @Operation(summary = "批量删除人员国际化信息")
    @DeleteMapping("/delete")
    public void delete(@RequestParam List<Long> personIdList) {
        sysOrgPersonTlMapper.delete(personIdList);
    }
}
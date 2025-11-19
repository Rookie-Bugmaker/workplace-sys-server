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

import java.util.Date;
import java.util.List;

@Tag(name = "组织架构-人员测试接口")
@RestController
@RequestMapping("/test/org/person")
public class SysOrgPersonTestController {

    @Autowired
    private SysOrgPersonMapperImpl sysOrgPersonMapper;

    @Autowired
    private SysOrgPersonTlMapperImpl sysOrgPersonTlMapper;

    @Operation(summary = "模糊查询人员列表")
    @PostMapping("/findByFuzzy")
    public List<SysOrgPerson> findByFuzzy(@RequestBody SysOrgPersonFindByPageParam param) {
        return sysOrgPersonMapper.findByFuzzy(param);
    }

    @Operation(summary = "新增人员")
    @PostMapping("/insert")
    public int insert(@RequestBody SysOrgPerson person) {
        return sysOrgPersonMapper.insert(person);
    }

    @Operation(summary = "更新人员信息")
    @PutMapping("/update")
    public int update(@RequestBody SysOrgPerson person) {
        return sysOrgPersonMapper.update(person);
    }

    @Operation(summary = "根据ID查询人员详情")
    @GetMapping("/findById")
    public SysOrgPerson findById(@RequestParam long personId, @RequestParam String language) {
        return sysOrgPersonMapper.findById(personId, language);
    }

    @Operation(summary = "批量删除人员")
    @DeleteMapping("/delete")
    public int delete(@RequestParam List<Long> personIdList) {
        return sysOrgPersonMapper.delete(personIdList);
    }

    @Operation(summary = "根据工号查询人员")
    @GetMapping("/findByJobNo")
    public SysOrgPerson findByJobNo(@RequestParam String jobNo) {
        return sysOrgPersonMapper.findByJobNo(jobNo);
    }

    @Operation(summary = "根据工号列表查询人员")
    @GetMapping("/findByJobNos")
    public List<SysOrgPerson> findByJobNos(@RequestParam List<String> jobNos) {
        return sysOrgPersonMapper.findByJobNos(jobNos);
    }

    @Operation(summary = "根据人员ID列表查询人员")
    @GetMapping("/findByPersonIds")
    public List<SysOrgPerson> findByPersonIds(@RequestParam List<Long> personIdList, 
                                            @RequestParam String language) {
        return sysOrgPersonMapper.findByPersonIds(personIdList, language);
    }

    @Operation(summary = "根据ID列表查询人员")
    @GetMapping("/findByIds")
    public List<SysOrgPerson> findByIds(@RequestParam List<Long> personIdList,
                                              @RequestParam String language) {
        return sysOrgPersonMapper.findByPersonIds(personIdList, language);
    }

    @Operation(summary = "批量新增人员")
    @PostMapping("/insertBatch")
    public boolean insertBatch(@RequestBody List<SysOrgPerson> persons) {
        return sysOrgPersonMapper.insertBatch(persons);
    }

    @Operation(summary = "查询离职人员")
    @GetMapping("/findByQuitDate")
    public List<SysOrgPerson> findByQuitDate(@RequestParam Date nowDate) {
        return sysOrgPersonMapper.findByQuitDate(nowDate);
    }

    @Operation(summary = "批量更新人员状态")
    @PutMapping("/updateStatus")
    public void updateStatus(@RequestParam List<Long> personIdList, 
                           @RequestParam int status) {
        sysOrgPersonMapper.updateStatus(personIdList, status);
    }

    @Operation(summary = "根据手机号列表查询人员")
    @GetMapping("/findByPhoneList")
    public List<SysOrgPerson> findByPhoneList(@RequestParam List<String> phoneList) {
        return sysOrgPersonMapper.findByPhoneList(phoneList);
    }
}
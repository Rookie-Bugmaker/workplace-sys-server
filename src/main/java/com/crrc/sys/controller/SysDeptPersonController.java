package com.crrc.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.convert.SysDeptPersonConvert;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.model.dept.person.SysDeptPersonDTO;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.service.DeptService;
import com.crrc.sys.service.SysDeptPersonService;
import com.crrc.sys.service.SysOrgPersonService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门人员管理-控制器
 *
 * @author 谭永强
 * @date 2025-09-03
 */
@RestController
@RequestMapping("/dept/person")
public class SysDeptPersonController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private SysDeptPersonService deptPersonService;
    @Resource
    private DeptService deptService;
    @Resource
    private SysOrgPersonService personService;


    /**
     * 新增
     *
     * @param deptPerson 参数
     */
    @PostMapping
    public void add(@RequestBody SysDeptPersonPO deptPerson) {
        //参数校验
        verification(OperationTypeEnum.ADD.getValue(), deptPerson);
        //设置主键ID
        deptPerson.setId(idKeyGenerator.getId());
        boolean result = deptPersonService.save(deptPerson);
        if (!result) {
            throw new BizException("保存失败");
        }
    }

    /**
     * 修改
     *
     * @param deptPerson 参数
     */
    @PutMapping
    public void update(@RequestBody SysDeptPersonPO deptPerson) {
        verification(OperationTypeEnum.UPDATE.getValue(), deptPerson);
        boolean result = deptPersonService.updateById(deptPerson);
        if (!result) {
            throw new BizException("保存失败");
        }
    }

    /**
     * 公共参数校验
     *
     * @param operation  新增或修改
     * @param deptPerson 参数信息
     */
    private void verification(String operation, SysDeptPersonPO deptPerson) {
        if (ObjectUtils.isEmpty(deptPerson)) {
            throw new ParamValidateException("参数错误");
        }
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            //修改
            if (ObjectUtils.isEmpty(deptPerson.getId())) {
                throw new ParamValidateException("主键ID不能为空!");
            }
        }
        if (ObjectUtils.isEmpty(deptPerson.getCompanyId())) {
            throw new ParamValidateException("公司ID不能为空!");
        }
        if (ObjectUtils.isEmpty(deptPerson.getDeptId())) {
            throw new ParamValidateException("部门ID不能为空!");
        }
        if (ObjectUtils.isEmpty(deptPerson.getPersonId())) {
            throw new ParamValidateException("人员ID不能为空!");
        }
    }

    /**
     * 根据主键ID查询部门人员信息
     *
     * @param id 主键ID
     * @return 部门人员信息
     */
    @GetMapping("{id}")
    public SysDeptPersonPO findById(@PathVariable("id") Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ParamValidateException("参数不能为空");
        }
        return deptPersonService.getById(id);
    }

    /**
     * 根据主键删除部门人员
     *
     * @param ids 主键集合
     */
    @DeleteMapping
    public void delete(@RequestBody List<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new ParamValidateException("参数不能为空!");
        }
        boolean result = deptPersonService.removeBatchByIds(ids);
        if (!result) {
            throw new BizException("删除失败");
        }
    }

    /**
     * 根据公司ID查询部门人员列表
     *
     * @param companyId 公司ID
     * @param page      当前页码
     * @param pageSize  查询数据条数
     * @return 部门人员列表
     */
    @GetMapping("findByCompanyId/{companyId}")
    public PaginationGrid<SysDeptPersonDTO> findByCompanyId(@PathVariable("companyId") Long companyId, Long page, Long pageSize) {
        if (ObjectUtils.isEmpty(page) || page <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(pageSize)) {
            throw new ParamValidateException("pageSize不能为空");
        }
        //根据公司ID查询部门列表
        List<Dept> deptList = deptService.getDeptListByCompanyId(companyId);
        if (ObjectUtils.isEmpty(deptList)) {
            return null;
        }
        List<Long> deptIdList = deptList.stream().map(Dept::getId).collect(Collectors.toList());
        //根据部门ID集合查询部门人员列表
        LambdaQueryWrapper<SysDeptPersonPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(!ObjectUtils.isEmpty(deptIdList), SysDeptPersonPO::getDeptId, deptIdList);
        IPage<SysDeptPersonPO> iPage = new Page<>(page, pageSize);
        iPage = deptPersonService.page(iPage, queryWrapper);
        List<SysDeptPersonPO> records = iPage.getRecords();
        PaginationGrid<SysDeptPersonDTO> paginationGrid = new PaginationGrid<>((int) iPage.getCurrent(), (int) iPage.getSize());
        paginationGrid.setTotal(iPage.getTotal());
        if (ObjectUtils.isEmpty(records)) {
            return paginationGrid;
        }
        paginationGrid.setRows(poToDto(records));
        return paginationGrid;
    }

    /**
     * 根据部门ID查询部门人员列表
     *
     * @param deptId   部门ID
     * @param page     当前页码
     * @param pageSize 查询数据条数
     * @return 部门人员列表
     */
    @GetMapping("findByDeptId/{deptId}")
    public PaginationGrid<SysDeptPersonDTO> findByDeptId(@PathVariable("deptId") Long deptId, Long page, Long pageSize) {
        if (ObjectUtils.isEmpty(page) || page <= 0) {
            throw new ParamValidateException("page不能为空且必须>0");
        }
        if (ObjectUtils.isEmpty(pageSize)) {
            throw new ParamValidateException("pageSize不能为空");
        }
        LambdaQueryWrapper<SysDeptPersonPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!ObjectUtils.isEmpty(deptId), SysDeptPersonPO::getDeptId, deptId);
        IPage<SysDeptPersonPO> iPage = new Page<>(page, pageSize);
        iPage = deptPersonService.page(iPage, queryWrapper);
        List<SysDeptPersonPO> records = iPage.getRecords();
        PaginationGrid<SysDeptPersonDTO> paginationGrid = new PaginationGrid<>((int) iPage.getCurrent(), (int) iPage.getSize());
        paginationGrid.setTotal(iPage.getTotal());
        if (ObjectUtils.isEmpty(records)) {
            return paginationGrid;
        }
        paginationGrid.setRows(poToDto(records));
        return paginationGrid;
    }

    /**
     * PO转DTO，同时设置人员姓名
     *
     * @param personPoList 人员实体集合
     * @return DTO集合
     */
    private List<SysDeptPersonDTO> poToDto(List<SysDeptPersonPO> personPoList) {
        List<SysDeptPersonDTO> personDtoList = SysDeptPersonConvert.INSTANCE.poToDto(personPoList);
        List<Long> personIdList1 = personDtoList.stream().map(SysDeptPersonDTO::getPersonId).collect(Collectors.toList());
        List<Long> personIdList2 = personDtoList.stream().map(SysDeptPersonDTO::getParentPersonId).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(personIdList2)) {
            personIdList1.addAll(personIdList2);
        }
        //根据人员ID查询人员信息
        List<SysOrgPerson> personList = personService.findByIds(personIdList1);
        if (CollectionUtils.isEmpty(personList)) {
            return personDtoList;
        }
        for (SysDeptPersonDTO personDto : personDtoList) {
            //查找人员信息
            SysOrgPerson person = personList.stream().filter(o -> o.getPersonId().equals(personDto.getPersonId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(person)) {
                personDto.setPersonName(person.getFullName());
                personDto.setJobNo(person.getJobNo());
            }
            //查找人员信息
            person = personList.stream().filter(o -> o.getPersonId().equals(personDto.getParentPersonId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(person)) {
                personDto.setParentPersonName(person.getFullName());
            }
        }
        return personDtoList;
    }
}

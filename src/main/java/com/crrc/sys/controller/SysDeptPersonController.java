package com.crrc.sys.controller;

import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.service.SysDeptPersonService;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
        if(ObjectUtils.isEmpty(ids)){
            throw new ParamValidateException("参数不能为空!");
        }
        deptPersonService.removeBatchByIds(ids);
    }
}

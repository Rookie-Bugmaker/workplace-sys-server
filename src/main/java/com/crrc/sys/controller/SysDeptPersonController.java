package com.crrc.sys.controller;

import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门人员管理-控制器
 *
 * @author 谭永强
 * @date 2025-09-03
 */
@RestController
@RequestMapping("/dept/person")
public class SysDeptPersonController {


    /**
     * 新增
     *
     * @param deptPersonAdd 参数
     */
    @PostMapping
    public void add(@RequestBody SysDeptPersonPO deptPersonAdd) {
        verification(OperationTypeEnum.ADD.getValue(), deptPersonAdd);

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
        if(OperationTypeEnum.ADD.getValue().equals(operation)){
            //新增
        }
        if(OperationTypeEnum.UPDATE.getValue().equals(operation)){
            //修改
            if (ObjectUtils.isEmpty(deptPerson.getId())) {
                throw new ParamValidateException("主键ID不能为空!");
            }
        }
    }
}

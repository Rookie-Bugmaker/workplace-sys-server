package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;

import java.util.List;

/**
 * 部门人员接口
 *
 * @author 谭永强
 * @date 2025-09-03
 */
public interface SysDeptPersonService extends IService<SysDeptPersonPO> {

    /**
     * 根据人员ID查询部门人员信息(批量)
     *
     * @param personIdList 人员ID集合
     * @return 结果
     */
    List<SysDeptPersonPO> findByPersonId(List<Long> personIdList);
}

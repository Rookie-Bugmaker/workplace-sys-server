package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.mapper.SysDeptPersonMapper;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import com.crrc.sys.service.SysDeptPersonService;
import org.springframework.stereotype.Service;

/**
 * 部门人员接口实现类
 *
 * @author 谭永强
 * @date 2025-09-03
 */
@Service
public class SysDeptPersonServiceImpl extends ServiceImpl<SysDeptPersonMapper, SysDeptPersonPO> implements SysDeptPersonService {
}

package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.function.SysFunction;
import com.crrc.sys.entity.function.SysFunctionBDBEntity;
import com.crrc.sys.entity.function.SysFunctionTl;
import com.crrc.sys.entity.function.SysFunctionTlDBEntity;
import com.crrc.sys.mapper.SysFunctionTlMapper;
import com.crrc.sys.mapper.mapperService.SysFunctionTlMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统功能多语言表-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-17
 */
@Repository
public class SysFunctionTlMapperImpl extends ServiceImpl<SysFunctionTlMapperService, SysFunctionTlDBEntity> {

    @Resource
    private SysFunctionTlMapperService sysFunctionTlMapperService;


    public int insertTl(List<SysFunctionTl> functionTlList) {
        List<SysFunctionTlDBEntity> sysFunctionTlDBEntities = convertToDB(functionTlList);
        return super.saveBatch(sysFunctionTlDBEntities) ? sysFunctionTlDBEntities.size() : 0;
    }


    public int deleteTl(List<Long> functionIds) {
        LambdaUpdateWrapper<SysFunctionTlDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionTlDBEntity::getFunctionId, functionIds);
        return sysFunctionTlMapperService.delete(wrapper);
    }


    public List<SysFunctionTl> findByFunctionIds(List<Long> functionIdList) {
        LambdaQueryWrapper<SysFunctionTlDBEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysFunctionTlDBEntity::getFunctionId, functionIdList);
        List<SysFunctionTlDBEntity> sysFunctionTlDBEntities = sysFunctionTlMapperService.selectList(wrapper);
        return convertFromDB(sysFunctionTlDBEntities);
    }

    private List<SysFunctionTlDBEntity> convertToDB(List<SysFunctionTl> dataList) {
        List<SysFunctionTlDBEntity> entityList = BeanUtils.toBean(dataList, SysFunctionTlDBEntity.class);
        return entityList;
    }

    private List<SysFunctionTl> convertFromDB(List<SysFunctionTlDBEntity> entityList) {
        List<SysFunctionTl> dataList = BeanUtils.toBean(entityList, SysFunctionTl.class);
        return dataList;
    }
}
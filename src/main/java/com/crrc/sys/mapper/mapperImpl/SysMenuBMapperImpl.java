package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.menu.SysMenuBDBEntity;
import com.crrc.sys.mapper.mapperService.SysMenuBMapperService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-18
 */
@Repository
public class SysMenuBMapperImpl extends ServiceImpl<SysMenuBMapperService, SysMenuBDBEntity>{

    @Resource
    private SysMenuBMapperService sysMenuBMapperService;
}
package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.mapper.ProjectMapper;
import com.crrc.sys.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {


    /**
     * 根据项目编码查询项目信息
     *
     * @param projectCode 项目编码
     * @return 结果
     */
    @Override
    public ProjectEntity findByCode(String projectCode) {
        if (ObjectUtils.isEmpty(projectCode)) {
            return null;
        }
        LambdaQueryWrapper<ProjectEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectEntity::getName, projectCode);
        return super.getOne(queryWrapper);
    }

    @Override
    public Long findCountByCode(String projectCode) {
        LambdaQueryWrapper<ProjectEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ProjectEntity::getId).eq(ProjectEntity::getName, projectCode);
        return this.count(wrapper);
    }
}

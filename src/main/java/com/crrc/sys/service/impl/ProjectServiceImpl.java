package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.entity.project.ProjectServiceEntity;
import com.crrc.sys.mapper.ProjectMapper;
import com.crrc.sys.mapper.ProjectServiceMapper;
import com.crrc.sys.service.ProjectService;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {

    @Resource
    private ProjectMapper projectMapper;


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

    @Override
    public List<String> findGroup() {
        MPJLambdaQueryWrapper<ProjectEntity> wrapper = new MPJLambdaQueryWrapper<>();
        wrapper.distinct()
                .select(ProjectEntity::getOwnershipGroup)
                .isNotNull(ProjectEntity::getOwnershipGroup)
                .ne(ProjectEntity::getOwnershipGroup, "");
        List<ProjectEntity> list = projectMapper.selectList(wrapper);
        if(list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(ProjectEntity::getOwnershipGroup)
                .filter(group -> StringUtils.isNotBlank(group))
                .collect(Collectors.toList());
    }
}

package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.project.ProjectEntity;
import com.crrc.sys.mapper.ProjectMapper;
import com.crrc.sys.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {
}

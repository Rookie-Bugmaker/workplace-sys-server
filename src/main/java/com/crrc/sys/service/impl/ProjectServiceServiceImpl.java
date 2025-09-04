package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.project.ProjectServiceEntity;
import com.crrc.sys.mapper.ProjectServiceMapper;
import com.crrc.sys.service.ProjectServiceService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceServiceImpl extends ServiceImpl<ProjectServiceMapper, ProjectServiceEntity> implements ProjectServiceService {
}

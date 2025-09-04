package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.project.ProjectServiceEntity;
import com.crrc.sys.entity.server.ServiceAccessManagementConfig;
import com.crrc.sys.mapper.ProjectServiceMapper;
import com.crrc.sys.mapper.ServiceAccessManagementMapper;
import com.crrc.sys.model.server.ServiceSysListRespVO;
import com.crrc.sys.service.ServiceAccessManagementService;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ServiceAccessManagementServiceImpl
        extends ServiceImpl<ServiceAccessManagementMapper, ServiceAccessManagementConfig>
        implements ServiceAccessManagementService {
    @Resource
    private ServiceAccessManagementMapper serviceAccessManagementMapper;
    @Resource
    private ProjectServiceMapper projectServiceMapper;

    public List<ServiceSysListRespVO> getSysList() {
        MPJLambdaWrapper<ServiceAccessManagementConfig> wrapper = JoinWrappers.lambda(ServiceAccessManagementConfig.class)
                .distinct()
                .select(ServiceAccessManagementConfig::getSysName,
                        ServiceAccessManagementConfig::getSysService,
                        ServiceAccessManagementConfig::getStatus);
        return serviceAccessManagementMapper.selectJoinList(ServiceSysListRespVO.class, wrapper);
    }

    @Override
    public List<ServiceAccessManagementConfig> getServiceByProjectId(Long projectId) {
        MPJLambdaWrapper<ProjectServiceEntity> wrapper = JoinWrappers.lambda(ProjectServiceEntity.class)
                .selectAll(ServiceAccessManagementConfig.class)
                .leftJoin(ServiceAccessManagementConfig.class, ServiceAccessManagementConfig::getId, ProjectServiceEntity::getServiceId)
                .eq(ProjectServiceEntity::getProjectId, projectId);
        return projectServiceMapper.selectJoinList(ServiceAccessManagementConfig.class, wrapper);
    }

}

package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.entity.server.ServiceAccessManagementConfig;
import com.crrc.sys.model.server.ServiceSysListRespVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceAccessManagementService extends IService<ServiceAccessManagementConfig> {
    List<ServiceSysListRespVO> getSysList();

    List<ServiceAccessManagementConfig> getServiceByProjectId(@Param("project_id") Long projectId);
}

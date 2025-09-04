package com.crrc.sys.mapper;

import com.crrc.sys.entity.server.ServiceAccessManagementConfig;
import com.crrc.sys.model.server.ServiceSysListRespVO;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ServiceAccessManagementMapper extends MPJBaseMapper<ServiceAccessManagementConfig> {
    @Select("select DISTINCT sys_name,sys_service,status from sys_service")
    List<ServiceSysListRespVO> getSysList();

    @Select("select ss.* from sys_project_service sps\n" +
            "LEFT JOIN sys_service ss on sps.service_id = ss.id\n" +
            "where sps.project_id = #{project_id}")
    List<ServiceAccessManagementConfig> getServiceByProjectId(@Param("project_id") Long projectId);
}

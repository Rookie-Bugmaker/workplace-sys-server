package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.entity.project.ProjectEntity;

public interface ProjectService extends IService<ProjectEntity> {

    /**
     * 根据项目编码查询项目信息
     *
     * @param projectCode 项目编码
     * @return 结果
     */
    ProjectEntity findByCode(String projectCode);

    Long findCountByCode(String projectCode);
}

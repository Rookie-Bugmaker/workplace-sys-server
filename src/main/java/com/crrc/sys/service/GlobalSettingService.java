package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.parameter.GlobalSetting;
import com.crrc.sys.model.parameter.MongoPage;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface GlobalSettingService {
    String create(GlobalSetting globalSetting);
    Boolean update(GlobalSetting globalSetting);
    Boolean delete(String settingId);
    GlobalSetting getById(String settingId);
    List<GlobalSetting> getLike(String settingName, String settingId);

    List<GlobalSetting> getAll();
    PaginationGrid<GlobalSetting> getByPage(MongoPage pageRequest);
    void exportTemplate(HttpServletResponse response);
    Boolean importData(MultipartFile file);
    void exportData(HttpServletResponse response, String settingId);
}
package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.parameter.GlobalSetting;
import com.crrc.sys.entity.parameter.GlobalSettingValue;
import com.crrc.sys.model.parameter.MongoPage;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface GlobalSettingValueService {
    String create(GlobalSettingValue globalSettingValue);
    Boolean update(GlobalSettingValue globalSettingValue);
    Boolean delete(String id);
    GlobalSettingValue getById(String id);
    List<GlobalSettingValue> getBySettingId(String settingId);
    List<GlobalSettingValue> getAll();
    PaginationGrid<GlobalSettingValue> getByPage(MongoPage pageRequest);
    void exportTemplate(HttpServletResponse response, String settingId);
    Boolean importData(MultipartFile file, String settingId);
    void exportData(HttpServletResponse response, String settingId);
    List<Map<String, Object>> getByCode(String settingId, String settingItemId, String settingItemContent, String pattern);
    List<Map<String, Object>> getByCode(String settingId);
}
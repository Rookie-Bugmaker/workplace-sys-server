package com.crrc.sys.controller;


import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.parameter.GlobalSetting;
import com.crrc.sys.model.parameter.MongoPage;
import com.crrc.sys.service.GlobalSettingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "全局参数配置", description = "全局参数配置")
@Slf4j
@RestController
@RequestMapping("globalSetting")
public class GlobalSettingController {

    @Autowired
    private GlobalSettingService globalSettingService;

    @PostMapping("/create")
    public String create(@Valid @RequestBody GlobalSetting globalSetting) {
        return globalSettingService.create(globalSetting);
    }

    @PostMapping("/update")
    public Boolean update(@Valid @RequestBody GlobalSetting globalSetting) {
        return globalSettingService.update(globalSetting);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String settingId) {
        return globalSettingService.delete(settingId);
    }

    @GetMapping("/get")
    public GlobalSetting getById(@RequestParam String settingId) {
        return globalSettingService.getById(settingId);
    }

    @GetMapping("/all")
    public List<GlobalSetting> getAll() {
        return globalSettingService.getAll();
    }

    @GetMapping("/query")
    public List<GlobalSetting> query(GlobalSetting globalSetting) {
        if(globalSetting != null) {
            if(StringUtils.isNotBlank(globalSetting.getSettingId())
                    || StringUtils.isNotBlank(globalSetting.getSettingName())) {
                return globalSettingService.getLike(globalSetting.getSettingName(), globalSetting.getSettingId());
            }
        }
        return globalSettingService.getAll();
    }

    @GetMapping("/download/template")
    public void exportTemplate(HttpServletResponse response) {
        globalSettingService.exportTemplate(response);
    }

    @PostMapping("/import")
    public Boolean importData(MultipartFile file) {
        return globalSettingService.importData(file);
    }

    @GetMapping("/download/export")
    public void exportData(HttpServletResponse response,@RequestParam String settingId) {
        globalSettingService.exportData(response, settingId);
    }

    @GetMapping("/page")
    public PaginationGrid<GlobalSetting> getByPage(MongoPage pageRequest) {
        return globalSettingService.getByPage(pageRequest);
    }
}
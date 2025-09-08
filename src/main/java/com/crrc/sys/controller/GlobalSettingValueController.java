package com.crrc.sys.controller;

import com.crrc.sys.common.MongoResResult;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.parameter.GlobalSettingValue;
import com.crrc.sys.model.parameter.MongoPage;
import com.crrc.sys.service.GlobalSettingValueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Tag(name = "全局参数结果配置", description = "全局参数结果配置")
@Slf4j
@RestController
@RequestMapping("globalSetting/value")
public class GlobalSettingValueController {

    @Autowired
    private GlobalSettingValueService globalSettingValueService;

    @PostMapping("/create")
    public String create(@RequestBody GlobalSettingValue globalSettingValue) {
        return globalSettingValueService.create(globalSettingValue);
    }

    @PostMapping("/update")
    public Boolean update(@RequestBody GlobalSettingValue globalSettingValue) {
        return globalSettingValueService.update(globalSettingValue);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String id) {
        return globalSettingValueService.delete(id);
    }

    @GetMapping("/getById")
    public GlobalSettingValue getById(@RequestParam String id) {
        return globalSettingValueService.getById(id);
    }

    @GetMapping("/getBySettingId")
    public List<GlobalSettingValue> getBySettingId(@RequestParam String settingId) {
        return globalSettingValueService.getBySettingId(settingId);
    }

    @GetMapping("/all")
    public List<GlobalSettingValue> getAll() {
        return globalSettingValueService.getAll();
    }

    @GetMapping("/template")
    public void exportTemplate(HttpServletResponse response, @RequestParam String settingId) {
        globalSettingValueService.exportTemplate(response, settingId);
    }

    @PostMapping("/import")
    public Boolean importData(MultipartFile file, @RequestParam String settingId) {
        return globalSettingValueService.importData(file, settingId);
    }

    @GetMapping("/export")
    public void exportData(HttpServletResponse response, @RequestParam String settingId) {
        globalSettingValueService.exportData(response, settingId);
    }


    @GetMapping("/match/all")
    public MongoResResult getAllItem(@RequestParam String settingId) {
        return globalSettingValueService.getByCode(settingId);
    }

    @GetMapping("/match/get")
    public MongoResResult getItemByCode(@RequestParam String settingId,
                                                   @RequestParam String settingItemId,
                                                   @RequestParam String settingItemContent) {
        return globalSettingValueService.getByCode(settingId, settingItemId, settingItemContent, null);
    }

    @GetMapping("/match/like")
    public MongoResResult likeItemByCode(@RequestParam String settingId,
                                                    @RequestParam String settingItemId,
                                                    @RequestParam String settingItemContent) {
        return globalSettingValueService.getByCode(settingId, settingItemId, settingItemContent, "like");
    }

    @GetMapping("/page")
    public PaginationGrid<GlobalSettingValue> getByPage(MongoPage pageRequest) {
        return globalSettingValueService.getByPage(pageRequest);
    }


}
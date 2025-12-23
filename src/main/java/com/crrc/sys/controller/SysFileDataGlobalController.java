package com.crrc.sys.controller;

import com.crrc.sys.annotation.AuditLog;
import com.crrc.sys.entity.filedata.FileData;
import com.crrc.sys.enums.LogType;
import com.crrc.sys.enums.OperateType;
import com.crrc.sys.service.impl.FileDataServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 文件数据表 前端控制器
 * </p>
 *
 * @author lijiangtao
 * @since 2025-06-16 14:44:51
 */
@RestController
@RequestMapping("global/fileData")
@Tag(name = "文件数据表", description = "文件数据表")
public class SysFileDataGlobalController {

    @Autowired
    private FileDataServiceImpl fileDataService;

    @Transactional
    @GetMapping("/saveFileData")
    @Operation(summary = "公共方法：存储文件数据表实体")
    @AuditLog(value = "公共方法：存储文件数据表实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public void saveFileData(@RequestParam("fileId")String fileId,
                             @RequestParam("actualName")String actualName,
                             @RequestParam("contentType")String contentType,
                             @RequestParam("size")Long size) {
        fileDataService.saveFileData(fileId, actualName, contentType, size);
    }

    @GetMapping("/getFileData")
    @Operation(summary = "公共方法：获取文件数据表实体")
    @AuditLog(value = "公共方法：获取文件数据表实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public FileData getFileData(@RequestParam("fileId") String fileId) {
        return fileDataService.getFileData(fileId);
    }

    @GetMapping("/listFileData")
    @Operation(summary = "公共方法：获取文件数据表实体")
    @AuditLog(value = "公共方法：获取文件数据表实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public List<FileData> listFileData(@RequestParam("fileIds") List<String> fileIds) {
        return fileDataService.listFileData(fileIds);
    }

    @GetMapping("/removeFileData")
    @Operation(summary = "公共方法：获取文件数据表实体")
    @AuditLog(value = "公共方法：获取文件数据表实体", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_DELETE)
    public void removeFileData(@RequestParam("fileIds") List<String> fileIds) {
        fileDataService.removeFileData(fileIds);
    }
}

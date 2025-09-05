package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.entity.filedata.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 文件数据表 服务类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-06-16 11:13:04
 */
public interface FileDataService extends IService<FileData> {
    FileData getFileData(String fileId);

    List<FileData> listFileData(List<String> fileIds);

    void saveFileData(String fileId, String actualName, String contentType, Long size);
}
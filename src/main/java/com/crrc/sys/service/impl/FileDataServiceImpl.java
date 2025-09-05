package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.filedata.FileData;
import com.crrc.sys.mapper.FileDataMapper;
import com.crrc.sys.service.FileDataService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 文件数据表 服务实现类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-06-16 11:13:04
 */
@Service
public class FileDataServiceImpl extends ServiceImpl<FileDataMapper, FileData> implements FileDataService {

    @Override
    public FileData getFileData(String fileId) {
        LambdaQueryWrapper<FileData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileData::getFileId, fileId);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<FileData> listFileData(List<String> fileIds) {
        LambdaQueryWrapper<FileData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FileData::getFileId, fileIds);
        return this.list(queryWrapper);
    }

    @Override
    public void saveFileData(String fileId, String actualName, String contentType, Long size) {
        if (!StringUtils.hasText(actualName)) {
            throw new RuntimeException("文件名不能为空");
        }
        // 生成存储信息
        int lastDotIndexOf = actualName.lastIndexOf(".");
        String extension = lastDotIndexOf == -1 ? "" : actualName.substring(lastDotIndexOf);
        String fileName = fileId + extension;
        String prefix = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String objectName = prefix + "/" + fileName;

        // 数据库记录
        FileData fileData = new FileData();
        fileData.setFileId(fileId);
        fileData.setActualName(actualName);
        fileData.setFilePath(objectName);
        fileData.setMimeType(contentType);
        fileData.setFileSize(size);
        this.save(fileData);
    }
}
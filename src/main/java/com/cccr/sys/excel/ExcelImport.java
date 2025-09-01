package com.cccr.sys.excel;

import com.cccr.sys.entity.excel.ExcelParams;
import org.springframework.web.multipart.MultipartFile;


/**
 * excel导入
 *
 * @author huangsen
 * @date 2023/5/24 09:39
 */
public interface ExcelImport {

    /**
     * 导出excel
     *
     * @param excelParams excel参数
     * @param file        文件
     */
    void importExcel(ExcelParams excelParams, MultipartFile file);
}

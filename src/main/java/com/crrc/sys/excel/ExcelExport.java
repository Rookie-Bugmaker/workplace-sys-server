package com.crrc.sys.excel;

import com.crrc.sys.entity.excel.ExcelParams;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * excel导出
 *
 * @author huangsen
 * @date 2023/5/24 09:38
 */
public interface ExcelExport {
    /**
     * 导出excel
     *
     * @param data        导出数据
     * @param excelParams excel参数
     * @param response    响应对象
     * @throws IOException io异常
     */
    <T> void exportExcel(List<T> data, ExcelParams excelParams, HttpServletResponse response) throws IOException;
}

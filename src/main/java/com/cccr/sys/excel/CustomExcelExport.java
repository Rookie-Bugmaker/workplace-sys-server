package com.cccr.sys.excel;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义Excel导出
 *
 * @author huangsen
 * @date 2023/6/8 14:09
 */
public interface CustomExcelExport {
    /**
     * 导出excel
     *
     * @param data     导出数据
     * @param response 响应对象
     * @param handler  导出excel处理器
     */
    <T> void exportExcel(T data, HttpServletResponse response, CustomExcelHandler handler) throws IOException;
}

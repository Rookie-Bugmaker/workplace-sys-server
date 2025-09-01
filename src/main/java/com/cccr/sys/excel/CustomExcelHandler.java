package com.cccr.sys.excel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义excel处理器
 *
 * @author huangsen
 * @date 2023/6/8 14:11
 */
public interface CustomExcelHandler {
    /**
     * 导出
     *
     * @param data     导出数据
     * @param response 响应对象
     */
    <T> void export(T data, HttpServletResponse response) throws IOException;
}

package com.crrc.sys.excel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.crrc.sys.config.exception.asserts.BizServiceAssert;
import com.crrc.sys.entity.excel.ExcelParams;
import com.crrc.sys.enums.ExcelEnums;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * 抽象excel处理器
 *
 * @author huangsen
 * @date 2023/5/24 10:23
 */
@Service
public class ExcelHandler implements ExcelExport, ExcelImport,CustomExcelExport {

    private static final Logger log = LoggerFactory.getLogger(ExcelHandler.class);


    @Override
    public <T> void exportExcel(List<T> data, ExcelParams excelParams, HttpServletResponse response) throws IOException {
        BizServiceAssert.state(Objects.nonNull(excelParams), "excel参数对象必须传递");
        handleExport(data, excelParams, response);
    }

    private <T> void handleExport(List<T> data, ExcelParams excelParams, HttpServletResponse response) throws IOException {
        try (ServletOutputStream output = response.getOutputStream()) {
            if (excelParams.isHasMultiSheet()) {
                handleMultiSheetExport(output, excelParams, data);
            } else {
                handleSingleSheetExport(output, excelParams, data);
            }
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String fileName = URLEncoder.encode(excelParams.getExcelFileTitle(), StandardCharsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            log.info("导出文件成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T> void handleMultiSheetExport(ServletOutputStream output, ExcelParams excelParams, List<T> data) {
        ExcelWriter excelWriter = EasyExcelFactory.write(output).build();
        excelParamsWrapper(excelParams);
        for (int i = 0; i < excelParams.getClazz().size(); i++) {
            WriteSheet writeSheet = null;
            if (ExcelEnums.ExportEnum.NORMAL == excelParams.getExportType()) {
                ExcelWriterSheetBuilder builder = EasyExcelFactory.writerSheet(i, getSheetName(excelParams.getSheetNames(), i)).head(getClazz(excelParams.getClazz(), i))
                        .excludeColumnFiledNames(excelParams.getExcludeColumnFiledNames());
                writeSheet = registerWriteHandler(builder, excelParams).build();
            } else if (ExcelEnums.ExportEnum.DYNAMIC == excelParams.getExportType()) {
                ExcelWriterSheetBuilder builder = EasyExcelFactory.writerSheet(i, getSheetName(excelParams.getSheetNames(), i)).head(excelParams.getDynamicHeaders().get(i))
                        .excludeColumnFiledNames(excelParams.getExcludeColumnFiledNames());
                writeSheet = registerWriteHandler(builder, excelParams).build();
            }
            Object excelData = getData(data, i);
            if (excelData instanceof List) {
                excelWriter.write((List) excelData, writeSheet);
            } else {
                BizServiceAssert.throwExp(String.format("传递的data参数{%s}有误,应该传递List<List<T>>类型的数据", JSON.toJSON(data)));
            }
        }
        excelWriter.finish();
    }

    private ExcelWriterSheetBuilder registerWriteHandler(ExcelWriterSheetBuilder builder, ExcelParams excelParams) {
        List<WriteHandler> writeHandlers = excelParams.getWriteHandlers();
        if (!CollectionUtils.isEmpty(writeHandlers)) {
            for (WriteHandler writeHandler : writeHandlers) {
                builder.registerWriteHandler(writeHandler);
            }
        }
        return builder;
    }

    private void excelParamsWrapper(ExcelParams excelParams) {
        if (Objects.nonNull(excelParams)) {
            excelParams.setWriteHandlers(Arrays.asList(initStyle(),new SimpleColumnWidthStyleStrategy(19),new SimpleRowHeightStyleStrategy((short)26,(short)22)));
        }
    }

    private WriteHandler initStyle() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景色设置
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short)12);
        headWriteFont.setColor(Font.COLOR_NORMAL);
        headWriteFont.setFontName("宋体");
        headWriteFont.setBold(true);
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short)12);
        contentWriteFont.setColor(Font.COLOR_NORMAL);
        contentWriteFont.setFontName("宋体");
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    private <T> void handleSingleSheetExport(ServletOutputStream output, ExcelParams excelParams, List<T> data) {
        excelParamsWrapper(excelParams);
        ExcelWriterSheetBuilder builder = null;
        switch (excelParams.getExportType()) {
            case NORMAL:
                builder = EasyExcelFactory.write(output).head(getClazz(excelParams.getClazz(), 0)).excludeColumnFiledNames(excelParams.getExcludeColumnFiledNames())
                        .sheet(getSheetName(excelParams.getSheetNames(), 0));
                registerWriteHandler(builder, excelParams).doWrite(data);
                break;
            case DYNAMIC:
                builder = EasyExcelFactory.write(output).head(excelParams.getDynamicHeaders().get(0)).excludeColumnFiledNames(excelParams.getExcludeColumnFiledNames()).sheet(getSheetName(excelParams.getSheetNames(), 0));
                registerWriteHandler(builder, excelParams).doWrite(data);
                break;
            default:
                break;
        }
    }

    private Class<?> getClazz(List<Class<?>> clazz, int index) {
        if (!CollectionUtils.isEmpty(clazz)) {
            return clazz.get(index);
        } else {
            BizServiceAssert.throwExp(String.format("该索引[%d]找不到对应的实体", index));
        }
        return null;
    }

    private <T> Object getData(List<T> data, int index) {
        return !CollectionUtils.isEmpty(data) ? data.get(index) : Collections.emptyList();
    }

    private String getSheetName(List<String> sheetNames, int index) {
        return !CollectionUtils.isEmpty(sheetNames) ? sheetNames.get(index) : null;
    }

    @Override
    public void importExcel(ExcelParams excelParams, MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            ExcelReader excelReader = EasyExcelFactory.read(input).build();
            int totalSheet = excelReader.excelExecutor().sheetList().size();
            ReadSheet[] readSheets = new ReadSheet[totalSheet];
            for (int i = 0; i < totalSheet; i++) {
                readSheets[i] = EasyExcelFactory.readSheet(i).head(excelParams.getClazz().get(i)).registerReadListener(excelParams.getAnalysisEventListenerList().get(i)).build();
            }
            excelReader.read(readSheets);
            excelReader.finish();
            log.info("导入文件成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public <T> void exportExcel(T data, HttpServletResponse response, CustomExcelHandler handler) throws IOException {
        handler.export(data, response);
    }
}

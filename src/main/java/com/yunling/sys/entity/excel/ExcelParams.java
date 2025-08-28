package com.yunling.sys.entity.excel;



import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.handler.WriteHandler;
import com.yunling.sys.enums.ExcelEnums;

import java.util.List;
import java.util.Set;

/**
 * excel参数
 *
 * @author huangsen
 * @date 2023/5/24 11:09
 */
public class ExcelParams {
    /**
     * 导出方式
     *
     * @see com.yunling.sys.enums.ExcelEnums.ExportEnum
     */
    private ExcelEnums.ExportEnum exportType;
    /**
     * excel对应的实体对象,当hasMultiSheet为true,这时候要提供多个实体,反之只需要提供一个实体
     */
    private List<Class<?>> clazz;
    /**
     * 是否包含多个sheet,如果没有,则认为是单个sheet,否则是多个sheet
     */
    private boolean hasMultiSheet;
    /**
     * sheet页的名称，按照顺序提供
     */
    private List<String> sheetNames;
    /**
     * excel文件的名称,一般情况下,前端会给定文件的名字
     */
    private String excelFileTitle;
    /**
     * 是否排除哪些列不需要导出,例如:实体Demo里面有一个String date属性,如果不需要导出只需要将date加入到excludeColumnFiledNames集合中
     */
    private Set<String> excludeColumnFiledNames;
    /**
     * 自定义样式提供的策略接口
     */
    private List<WriteHandler> writeHandlers;
    /**
     * 动态头列表
     */
    private List<List<List<String>>> dynamicHeaders;
    /**
     * 分析事件监听器列表
     */
    private List<AnalysisEventListener<?>> analysisEventListenerList;

    @Override
    public String toString() {
        return "ExcelParams{" +
                "exportType=" + exportType +
                ", clazz=" + clazz +
                ", hasMultiSheet=" + hasMultiSheet +
                ", sheetNames=" + sheetNames +
                ", excelFileTitle='" + excelFileTitle + '\'' +
                ", excludeColumnFiledNames=" + excludeColumnFiledNames +
                ", writeHandlers=" + writeHandlers +
                ", dynamicHeaders=" + dynamicHeaders +
                ", analysisEventListenerList=" + analysisEventListenerList +
                '}';
    }

    public ExcelEnums.ExportEnum getExportType() {
        return exportType;
    }

    public void setExportType(ExcelEnums.ExportEnum exportType) {
        this.exportType = exportType;
    }

    public List<Class<?>> getClazz() {
        return clazz;
    }

    public void setClazz(List<Class<?>> clazz) {
        this.clazz = clazz;
    }

    public boolean isHasMultiSheet() {
        return hasMultiSheet;
    }

    public void setHasMultiSheet(boolean hasMultiSheet) {
        this.hasMultiSheet = hasMultiSheet;
    }

    public List<String> getSheetNames() {
        return sheetNames;
    }

    public void setSheetNames(List<String> sheetNames) {
        this.sheetNames = sheetNames;
    }

    public String getExcelFileTitle() {
        return excelFileTitle;
    }

    public void setExcelFileTitle(String excelFileTitle) {
        this.excelFileTitle = excelFileTitle;
    }

    public Set<String> getExcludeColumnFiledNames() {
        return excludeColumnFiledNames;
    }

    public void setExcludeColumnFiledNames(Set<String> excludeColumnFiledNames) {
        this.excludeColumnFiledNames = excludeColumnFiledNames;
    }

    public List<WriteHandler> getWriteHandlers() {
        return writeHandlers;
    }

    public void setWriteHandlers(List<WriteHandler> writeHandlers) {
        this.writeHandlers = writeHandlers;
    }

    public List<List<List<String>>> getDynamicHeaders() {
        return dynamicHeaders;
    }

    public void setDynamicHeaders(List<List<List<String>>> dynamicHeaders) {
        this.dynamicHeaders = dynamicHeaders;
    }

    public List<AnalysisEventListener<?>> getAnalysisEventListenerList() {
        return analysisEventListenerList;
    }

    public void setAnalysisEventListenerList(List<AnalysisEventListener<?>> analysisEventListenerList) {
        this.analysisEventListenerList = analysisEventListenerList;
    }
}

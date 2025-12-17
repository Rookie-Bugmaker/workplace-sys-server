package com.crrc.sys.model.iddesign;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.crrc.sys.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 自定义ID
 * </p>
 *
 * @author lijiangtao
 * @since 2025-07-11 11:55:04
 */
@ExcelIgnoreUnannotated
public class IdDesignExcelVO {


    @ExcelProperty(value = {"业务标识"}, index = 0)
    @Schema(description = "业务标识", example = "")
    private String businessIdentifier;

    @ExcelProperty(value = {"时间格式"}, index = 1)
    @Schema(description = "时间格式", example = "")
    private String timeFormat;

    @ExcelProperty(value = {"时间位置"}, index = 2)
    @Schema(description = "时间位置", example = "")
    private String timePosition;

    @ExcelProperty(value = {"启用时间（0 启用 1不启用）"}, index = 3)
    @Schema(description = "启用时间（0 启用 1不启用）", example = "")
    private String enableTime;


    @ExcelProperty(value = {"唯一编码类型（0 UUID 1雪花ID 2流水号）"}, index = 4)
    @Schema(description = "唯一编码类型（0 UUID 1雪花ID 2流水号）", example = "")
    private String uniqueCodeType;

    @ExcelProperty(value = {"流水号起始值"}, index = 5)
    @Schema(description = "流水号起始值", example = "")
    private Integer serialNumberStart;


    @ExcelProperty(value = {"流水号当前值"}, index = 6)
    @Schema(description = "流水号当前值", example = "")
    private Integer serialNumberCurrent;

    @ExcelProperty(value = {"流水号位数"}, index = 7)
    @Schema(description = "流水号位数", example = "")
    private Integer serialNumberDigits;

    @ExcelProperty(value = {"唯一编码位置"}, index = 8)
    @Schema(description = "唯一编码位置", example = "")
    private String uniqueCodePosition;

    @ExcelProperty(value = {"自定义字符串(a;b;c;d)"}, index = 9)
    @Schema(description = "自定义字符串(a;b;c;d)", example = "")
    private String customString;

    @ExcelProperty(value = {"字符串位置(1;2;3;4)"}, index = 10)
    @Schema(description = "字符串位置(1;2;3;4)", example = "")
    private String stringPosition;

    @ExcelProperty(value = {"启用字符串（0 启用 1不启用）"}, index = 11)
    @Schema(description = "启用字符串（0 启用 1不启用）", example = "")
    private String enableString;

    @ExcelProperty(value = {"分隔符"}, index = 12)
    @Schema(description = "分隔符", example = "")
    private String spliceSeparator;

    @ExcelProperty(value = {"启用循环（0 启用 1不启用）"}, index = 13)
    @Schema(description = "启用循环（0 启用 1不启用）", example = "")
    private String enableCycle;

    @ExcelProperty(value = {"循环单位（HOUR DAY WEEK MONTH YEAR）"}, index = 14)
    @Schema(description = "循环单位（HOUR DAY WEEK MONTH YEAR）", example = "")
    private String cycleUnit;

    @ExcelProperty(value = {"循环日期"}, index = 15)
    @Schema(description = "循环日期", example = "")
    private Integer cycleDate;

    @ExcelProperty(value = {"最后一次重置日期"}, index = 16)
    @Schema(description = "最后一次重置日期", example = "")
    private LocalDateTime lastResetDate;

    @ExcelProperty(value = {"启用公司隔离（0 启用 1不启用）"}, index = 17)
    @Schema(description = "启用公司隔离（0 启用 1不启用）", example = "")
    private String enableCompany;

    public IdDesignExcelVO() {
    }

    public void setBusinessIdentifier(String businessIdentifier) {
        this.businessIdentifier = businessIdentifier;
    }

    public String getBusinessIdentifier() {
        return this.businessIdentifier;
    }


    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeFormat() {
        return this.timeFormat;
    }

    public void setTimePosition(String timePosition) {
        this.timePosition = timePosition;
    }

    public String getTimePosition() {
        return this.timePosition;
    }

    public void setEnableTime(String enableTime) {
        this.enableTime = enableTime;
    }

    public String getEnableTime() {
        return this.enableTime;
    }

    public void setUniqueCodeType(String uniqueCodeType) {
        this.uniqueCodeType = uniqueCodeType;
    }

    public String getUniqueCodeType() {
        return this.uniqueCodeType;
    }

    public void setSerialNumberStart(Integer serialNumberStart) {
        this.serialNumberStart = serialNumberStart;
    }

    public Integer getSerialNumberStart() {
        return this.serialNumberStart;
    }

    public void setSerialNumberCurrent(Integer serialNumberCurrent) {
        this.serialNumberCurrent = serialNumberCurrent;
    }

    public Integer getSerialNumberCurrent() {
        return this.serialNumberCurrent;
    }

    public void setSerialNumberDigits(Integer serialNumberDigits) {
        this.serialNumberDigits = serialNumberDigits;
    }

    public Integer getSerialNumberDigits() {
        return this.serialNumberDigits;
    }

    public void setUniqueCodePosition(String uniqueCodePosition) {
        this.uniqueCodePosition = uniqueCodePosition;
    }

    public String getUniqueCodePosition() {
        return this.uniqueCodePosition;
    }

    public void setCustomString(String customString) {
        this.customString = customString;
    }

    public String getCustomString() {
        return this.customString;
    }

    public void setStringPosition(String stringPosition) {
        this.stringPosition = stringPosition;
    }

    public String getStringPosition() {
        return this.stringPosition;
    }

    public void setEnableString(String enableString) {
        this.enableString = enableString;
    }

    public String getEnableString() {
        return this.enableString;
    }

    public String getSpliceSeparator() {
        return spliceSeparator;
    }

    public void setSpliceSeparator(String spliceSeparator) {
        this.spliceSeparator = spliceSeparator;
    }

    public String getEnableCycle() {
        return enableCycle;
    }

    public void setEnableCycle(String enableCycle) {
        this.enableCycle = enableCycle;
    }

    public String getCycleUnit() {
        return cycleUnit;
    }

    public void setCycleUnit(String cycleUnit) {
        this.cycleUnit = cycleUnit;
    }

    public Integer getCycleDate() {
        return cycleDate;
    }

    public void setCycleDate(Integer cycleDate) {
        this.cycleDate = cycleDate;
    }

    public LocalDateTime getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDateTime lastResetDate) {
        this.lastResetDate = lastResetDate;
    }
}

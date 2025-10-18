package com.crrc.sys.entity.iddesign;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import com.crrc.sys.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

/**
 * <p>
 * 自定义ID
 * </p>
 *
 * @author lijiangtao
 * @since 2025-07-11 11:55:04
 */
@TableName("sys_id_design")
@Schema(description = "自定义ID")
public class IdDesign extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1943519384301137920L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "备注", example = "demo")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "所属部门id", example = "1")
    private Long orgId;

//    @TableField(fill = FieldFill.INSERT)
//    @Schema(description = "租户id", example = "1")
//    private Long tenantId;

    @TableLogic(value = "0")
    @Schema(description = "删除", example = "false")
    private Integer deleted;

    @Schema(description = "业务标识", example = "")
    private String businessIdentifier;

    @Schema(description = "时间精确程度（0年 1月 2日 3时 4分 5秒）", example = "")
    private String timePrecision;


    @Schema(description = "时间格式", example = "")
    private String timeFormat;

    @Schema(description = "时间位置", example = "")
    private String timePosition;

    @Schema(description = "启用时间（0 启用 1不启用）", example = "")
    private String enableTime;

    @Schema(description = "唯一编码类型（0 UUID 1雪花ID 2流水号）", example = "")
    private String uniqueCodeType;

    @Schema(description = "流水号起始值", example = "")
    private Integer serialNumberStart;

    @Schema(description = "流水号当前值", example = "")
    private Integer serialNumberCurrent;

    @Schema(description = "流水号位数", example = "")
    private Integer serialNumberDigits;

    @Schema(description = "唯一编码位置", example = "")
    private String uniqueCodePosition;

    @Schema(description = "自定义字符串(a;b;c;d)", example = "")
    private String customString;

    @Schema(description = "字符串位置(1;2;3;4)", example = "")
    private String stringPosition;

    @Schema(description = "启用字符串（0 启用 1不启用）", example = "")
    private String enableString;

    @Schema(description = "分隔符", example = "")
    private String spliceSeparator;

    @Schema(description = "启用循环（0 启用 1不启用）", example = "")
    private String enableCycle;

    @Schema(description = "循环单位（HOUR DAY WEEK MONTH YEAR）", example = "")
    private String cycleUnit;

    @Schema(description = "循环日期", example = "")
    private Integer cycleDate;

    @Schema(description = "最后一次重置日期", example = "")
    private LocalDateTime lastResetDate;


    public IdDesign() {
    }

//    public IdDesign(CodeId codeId, TimeId timeId, FixId fixId) {
//        if(codeId != null) {
//            this.setUniqueCodeType(codeId.getUniqueCodeType());
//            this.setSerialNumberStart(codeId.getSerialNumberStart());
//            this.setSerialNumberCurrent(codeId.getSerialNumberCurrent());
//            this.setSerialNumberDigits(codeId.getSerialNumberDigits());
//            this.setUniqueCodePosition(codeId.getUniqueCodePosition());
//        }else {
//            throw new RuntimeException("流水号：唯一编码不能为空");
//        }
//
//        if(timeId != null) {
//            this.setTimePrecision(timeId.getTimePrecision());
//            this.setTimeFormat(timeId.getTimeFormat());
//            this.setTimePosition(timeId.getTimePosition());
//            this.setEnableTime("0");
//        }else {
//            this.setEnableTime("1");
//        }
//
//        if(fixId != null) {
//            this.setCustomString(fixId.getCustomString());
//            this.setStringPosition(fixId.getStringPosition());
//            this.setEnableString("0");
//        }else {
//            this.setEnableString("1");
//        }
//    }



    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setBusinessIdentifier(String businessIdentifier) {
        this.businessIdentifier = businessIdentifier;
    }

    public String getBusinessIdentifier() {
        return this.businessIdentifier;
    }

    public void setTimePrecision(String timePrecision) {
        this.timePrecision = timePrecision;
    }

    public String getTimePrecision() {
        return this.timePrecision;
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

    @Override
    public String toString() {
        return "IdDesign{" +
                "id=" + id +
                ",businessIdentifier='" + businessIdentifier + "'" +
                ",timePrecision='" + timePrecision + "'" +
                ",timeFormat='" + timeFormat + "'" +
                ",timePosition='" + timePosition + "'" +
                ",enableTime='" + enableTime + "'" +
                ",uniqueCodeType='" + uniqueCodeType + "'" +
                ",serialNumberStart='" + serialNumberStart + "'" +
                ",serialNumberCurrent='" + serialNumberCurrent + "'" +
                ",serialNumberDigits='" + serialNumberDigits + "'" +
                ",uniqueCodePosition='" + uniqueCodePosition + "'" +
                ",customString='" + customString + "'" +
                ",stringPosition='" + stringPosition + "'" +
                ",enableString='" + enableString + "'" +
                ",spliceSeparator='" + spliceSeparator + "'" +
                '}';
    }
}

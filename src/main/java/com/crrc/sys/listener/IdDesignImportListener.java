package com.crrc.sys.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.entity.iddesign.IdDesign;
import com.crrc.sys.model.dict.DictAndItemExcelVO;
import com.crrc.sys.model.dict.DictItemExcelVO;
import com.crrc.sys.model.dict.DictItemVO;
import com.crrc.sys.model.dict.DictVO;
import com.crrc.sys.model.iddesign.IdDesignExcelVO;
import com.crrc.sys.service.DictCacheService;
import com.crrc.sys.service.DictItemService;
import com.crrc.sys.service.DictService;
import com.crrc.sys.service.IdDesignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class IdDesignImportListener extends AnalysisEventListener<IdDesignExcelVO> {
    private final IdDesignService idDesignService;
    private final List<String> errorMessages = new ArrayList<>();

    private List<IdDesign> validDataList = new ArrayList<>();
    
    private Boolean errorFlag = false;

    public IdDesignImportListener(IdDesignService idDesignService) {
        this.idDesignService = idDesignService;
    }

    @Override
    public void invoke(IdDesignExcelVO excelData, AnalysisContext context) {
        IdDesign data = BeanUtils.toBean(excelData, IdDesign.class);
        int row = context.readRowHolder().getRowIndex() + 1;
        errorFlag = false;
        // 1. 检查业务标识是否已存在
        if (StringUtils.isBlank(data.getBusinessIdentifier())) {
            errorMsg("第" + row + "行: 业务标识不能为空");
        } else {
            LambdaQueryWrapper<IdDesign> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(IdDesign::getBusinessIdentifier, data.getBusinessIdentifier());
            if (idDesignService.count(queryWrapper) > 0) {
                errorMsg("第" + row + "行: 业务标识'" + data.getBusinessIdentifier() + "'已存在");
            }
            List<String> businessIds = validDataList.stream().map(IdDesign::getBusinessIdentifier).collect(Collectors.toList());
            if(businessIds.contains(data.getBusinessIdentifier())){
                errorMsg("第" + row + "行: 业务标识'" + data.getBusinessIdentifier() + "'重复导入");
            }
        }

        // 2. 检查唯一编码位置不能为空
        if (StringUtils.isBlank(data.getUniqueCodePosition())) {
            errorMsg("第" + row + "行: 唯一编码位置不能为空");
        }

        // 3. 检查流水号相关字段
        if ("2".equals(data.getUniqueCodeType())) { // 流水号类型
            if (data.getSerialNumberStart() == null) {
                errorMsg("第" + row + "行: 流水号起始值不能为空");
            }
            if (data.getSerialNumberDigits() == null) {
                errorMsg("第" + row + "行: 流水号位数不能为空");
            }
            // 如果当前值为空，设置为起始值
            if (data.getSerialNumberCurrent() == null) {
                data.setSerialNumberCurrent(data.getSerialNumberStart());
            }
        }

        // 4. 检查时间相关字段
        if ("0".equals(data.getEnableTime())) { // 启用时间
            if (StringUtils.isBlank(data.getTimeFormat())) {
                errorMsg("第" + row + "行: 时间格式不能为空");
            }
            if (StringUtils.isBlank(data.getTimePosition())) {
                errorMsg("第" + row + "行: 时间位置不能为空");
            }
            // 检查时间格式并设置精度
            if (StringUtils.isNotBlank(data.getTimeFormat())) {
                data.setTimePrecision(checkPrecision(data.getTimeFormat()));
            }
        }

        // 5. 检查字符串相关字段
        if ("0".equals(data.getEnableString())) { // 启用字符串
            if (StringUtils.isBlank(data.getCustomString())) {
                errorMsg("第" + row + "行: 自定义字符串不能为空");
            }
            if (StringUtils.isBlank(data.getStringPosition())) {
                errorMsg("第" + row + "行: 字符串位置不能为空");
            }
            // 检查字符串位置和自定义字符串数量是否匹配
            if (StringUtils.isNotBlank(data.getCustomString()) &&
                    StringUtils.isNotBlank(data.getStringPosition())) {
                String[] strings = data.getCustomString().split(";");
                String[] positions = data.getStringPosition().split(";");
                if (strings.length != positions.length) {
                    errorMsg("第" + row + "行: 自定义字符串和字符串位置数量不匹配");
                }
            }
        }

        // 6. 检查循环相关字段
        if ("0".equals(data.getEnableCycle())) { // 启用循环
            if (StringUtils.isBlank(data.getCycleUnit())) {
                errorMsg("第" + row + "行: 循环单位不能为空");
            } else if (!Arrays.asList("HOUR", "DAY", "WEEK", "MONTH", "YEAR").contains(data.getCycleUnit())) {
                errorMsg("第" + row + "行: 循环单位必须是HOUR/DAY/WEEK/MONTH/YEAR之一");
            }
            if (data.getCycleDate() == null) {
                errorMsg("第" + row + "行: 循环日期不能为空");
            }
        }
        if(!errorFlag){
            validDataList.add(data);
        }
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!errorMessages.isEmpty()) {
            throw new BizException(String.join("; ", errorMessages));
        }
        // 没有错误则插入数据
        // 这里需要根据实际业务逻辑实现数据保存
        idDesignService.saveBatch(validDataList);
    }

    // 定义精度级别及对应的正则表达式
    // 时间精确程度（0年 1月 2日 3时 4分 5秒）
    private final Map<String, Pattern> PRECISION_PATTERNS = new LinkedHashMap() {{
        put("5", Pattern.compile("[sS]"));    // 秒或毫秒
        put("4", Pattern.compile("m"));       // 分钟
        put("3", Pattern.compile("[Hh]"));      // 小时
        put("2", Pattern.compile("d"));          // 日
        put("1", Pattern.compile("M"));        // 月
        put("0", Pattern.compile("[yY]"));      // 年
    }};

    /**
     * 检查时间格式字符串的最小精度
     * @param format 时间格式字符串
     * @return 最小精度级别（year/month/day/hour/minute/second）
     */
    private String checkPrecision(String format) {
        // 按精度从高到低检查
        for (Map.Entry<String, Pattern> entry : PRECISION_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(format).find()) {
                return entry.getKey();
            }
        }
        return "0"; // 默认返回年
    }
    
    private void errorMsg(String content) {
        errorMessages.add(content);
        errorFlag = true;
    }
}
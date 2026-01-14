package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.parameter.GlobalSetting;
import com.crrc.sys.entity.parameter.GlobalSettingItem;
import com.crrc.sys.mapper.mongodb.GlobalSettingRepository;
import com.crrc.sys.model.parameter.MongoPage;
import com.crrc.sys.service.GlobalSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class GlobalSettingServiceImpl implements GlobalSettingService {

    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String create(GlobalSetting globalSetting) {
        // 检查settingId是否已存在
        if (globalSettingRepository.existsBySettingId(globalSetting.getSettingId())) {
            throw new BizException("配置编码已存在");
        }

        // 检查settingItems中的settingItemId是否有重复
        Set<String> itemIds = new HashSet<>();
        for (GlobalSettingItem item : globalSetting.getSettingItems()) {
            if (itemIds.contains(item.getSettingItemId())) {
                throw new BizException("配置项编码重复: " + item.getSettingItemId());
            }
            itemIds.add(item.getSettingItemId());
        }

        globalSettingRepository.save(globalSetting);
        return globalSetting.getSettingId();
    }

    @Override
    public Boolean update(GlobalSetting globalSetting) {
        // 检查配置是否存在
        GlobalSetting existingSetting = globalSettingRepository.findBySettingId(globalSetting.getSettingId());
        if (existingSetting == null) {
            throw new BizException("配置不存在");
        }

        // 检查settingItems中的settingItemId是否有重复
        Set<String> itemIds = new HashSet<>();
        for (GlobalSettingItem item : globalSetting.getSettingItems()) {
            if (itemIds.contains(item.getSettingItemId())) {
                throw new BizException("配置项编码重复: " + item.getSettingItemId());
            }
            itemIds.add(item.getSettingItemId());
        }

        globalSetting.setSettingId(existingSetting.getSettingId());
        globalSettingRepository.save(globalSetting);
        return true;
    }

    @Override
    public Boolean delete(String settingId) {
        GlobalSetting setting = globalSettingRepository.findBySettingId(settingId);
        if (setting == null) {
            throw new BizException("配置编码已存在");
        }
        globalSettingRepository.delete(setting);
        return true;
    }

    @Override
    public GlobalSetting getById(String settingId) {
        GlobalSetting setting = globalSettingRepository.findBySettingId(settingId);
        return setting;
    }

    @Override
    public List<GlobalSetting> getAll() {
        List<GlobalSetting> settings = globalSettingRepository.findAll();
        return settings;
    }

    @Override
    public List<GlobalSetting> getLike(String settingName, String settingId) {
        List<GlobalSetting> settings = globalSettingRepository.findByDynamicCriteria(settingName, settingId);
        return settings;
    }

    @Override
    public PaginationGrid<GlobalSetting> getByPage(MongoPage pageRequest) {
        // 构建查询条件
        Query query = new Query();

        // 添加模糊查询条件
        if (StringUtils.hasText(pageRequest.getSettingId())) {
            Pattern pattern = Pattern.compile("^.*" + pageRequest.getSettingId() + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("settingId").regex(pattern));
        }

        if (StringUtils.hasText(pageRequest.getSettingName())) {
            Pattern pattern = Pattern.compile("^.*" + pageRequest.getSettingName() + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("settingName").regex(pattern));
        }

        // 计算跳过数量
        int skip = (pageRequest.getPage() - 1) * pageRequest.getPageSize();

        // 设置分页
        query.skip(skip).limit(pageRequest.getPageSize());

        // 设置排序
        if (StringUtils.hasText(pageRequest.getSortField())) {
            Sort.Direction direction = Sort.Direction.ASC;
            if ("desc".equalsIgnoreCase(pageRequest.getSortOrder())) {
                direction = Sort.Direction.DESC;
            }
            query.with(Sort.by(direction, pageRequest.getSortField()));
        }

        // 执行查询
        List<GlobalSetting> list = mongoTemplate.find(query, GlobalSetting.class);

        // 重新构建计数查询（确保与数据查询条件完全一致）
        Query countQuery = new Query();

        if (StringUtils.hasText(pageRequest.getSettingId())) {
            Pattern pattern = Pattern.compile("^.*" + pageRequest.getSettingId() + ".*$", Pattern.CASE_INSENSITIVE);
            countQuery.addCriteria(Criteria.where("settingId").regex(pattern));
        }

        if (StringUtils.hasText(pageRequest.getSettingName())) {
            Pattern pattern = Pattern.compile("^.*" + pageRequest.getSettingName() + ".*$", Pattern.CASE_INSENSITIVE);
            countQuery.addCriteria(Criteria.where("settingName").regex(pattern));
        }

        // 获取总数（使用重新构建的查询条件）
        long total = mongoTemplate.count(countQuery, GlobalSetting.class);

        // 构建分页结果
        IPage<GlobalSetting> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        page.setRecords(list);
        page.setTotal(total);

        return PaginationGrid.success(page);
    }

    @Override
    public void exportTemplate(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("全局配置模板");

            // ============== 样式定义 ==============
            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);  // 加粗
            headerFont.setFontHeightInPoints((short) 12);  // 字体大小
            headerFont.setColor(IndexedColors.WHITE.getIndex());  // 字体颜色
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());  // 背景色
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);  // 水平居中
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中
            headerStyle.setBorderTop(BorderStyle.THIN);  // 上边框
            headerStyle.setBorderBottom(BorderStyle.THIN);  // 下边框
            headerStyle.setBorderLeft(BorderStyle.THIN);  // 左边框
            headerStyle.setBorderRight(BorderStyle.THIN);  // 右边框

            // 数据行样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);  // 左对齐
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setWrapText(true);  // 自动换行

            // 示例数据行样式
            CellStyle exampleStyle = workbook.createCellStyle();
            exampleStyle.cloneStyleFrom(dataStyle);
            exampleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            exampleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            exampleStyle.setFont(workbook.createFont());

            // ============== 创建表头 ==============
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "配置名称", "配置编码", "配置项编码",
                    "配置项名称", "配置项类型(字符串/整数/小数)", "配置项排序"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ============== 设置列宽 ==============
            int[] columnWidths = {20, 20, 20, 20, 30, 15};
            for (int i = 0; i < columnWidths.length; i++) {
                sheet.setColumnWidth(i, columnWidths[i] * 256);
            }

//            // ============== 写入示例数据 ==============
//            Row exampleRow = sheet.createRow(1);
//            Object[] exampleData = {
//                    "示例配置", "EXAMPLE_CONFIG", "ITEM_1",
//                    "示例配置项1", "字符串", 1
//            };
//
//            for (int i = 0; i < exampleData.length; i++) {
//                Cell cell = exampleRow.createCell(i);
//                if (exampleData[i] instanceof String) {
//                    cell.setCellValue((String) exampleData[i]);
//                } else if (exampleData[i] instanceof Number) {
//                    cell.setCellValue(((Number) exampleData[i]).doubleValue());
//                }
//                cell.setCellStyle(exampleStyle);
//            }

            // ============== 冻结首行 ==============
            sheet.createFreezePane(0, 1, 0, 1);

            // ============== 设置响应头 ==============
            String fileName = "全局配置模板_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // ============== 自动调整列宽(可选) ==============
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 防止自动调整列宽后内容显示不全
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 1024, 30 * 256));
            }

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出Excel模板失败", e);
            throw new BizException("导出Excel模板失败" + e.getMessage());
        }
    }

    @Override
    public Boolean importData(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            // 跳过表头
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            
            Map<String, GlobalSetting> settingsMap = new HashMap<>();
            List<String> errors = new ArrayList<>();
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                
                String settingName = getCellStringValue(row.getCell(0));
                String settingId = getCellStringValue(row.getCell(1));
                String settingItemId = getCellStringValue(row.getCell(2));
                String settingItemName = getCellStringValue(row.getCell(3));
                String settingItemTypeStr = getCellStringValue(row.getCell(4));
                String settingItemSortedStr = getCellStringValue(row.getCell(5));
                
                // 验证数据
                if (settingId == null || settingId.isEmpty()) {
                    errors.add("第" + (row.getRowNum() + 1) + "行: 配置编码不能为空");
                    continue;
                }
                
                if (settingItemId == null || settingItemId.isEmpty()) {
                    errors.add("第" + (row.getRowNum() + 1) + "行: 配置项编码不能为空");
                    continue;
                }
                
                Integer settingItemType = convertSettingItemType(settingItemTypeStr);
                if (settingItemType == null) {
                    errors.add("第" + (row.getRowNum() + 1) + "行: 配置项类型无效，必须是'字符串'、'整数'或'小数'");
                    continue;
                }
                
                Integer settingItemSorted;
                try {
                    settingItemSorted = Integer.parseInt(settingItemSortedStr);
                } catch (NumberFormatException e) {
                    errors.add("第" + (row.getRowNum() + 1) + "行: 配置项排序必须是整数");
                    continue;
                }
                
                // 创建或获取GlobalSetting对象
                GlobalSetting setting = settingsMap.get(settingId);
                if (setting == null) {
                    setting = new GlobalSetting();
                    setting.setSettingId(settingId);
                    setting.setSettingName(settingName);
                    setting.setSettingItems(new ArrayList<>());
                    settingsMap.put(settingId, setting);
                }
                
                // 检查配置项编码是否重复
                boolean itemIdExists = setting.getSettingItems().stream()
                        .anyMatch(item -> item.getSettingItemId().equals(settingItemId));
                if (itemIdExists) {
                    errors.add("第" + (row.getRowNum() + 1) + "行: 配置项编码" + settingItemId + "重复");
                    continue;
                }
                
                // 添加配置项
                GlobalSettingItem item = new GlobalSettingItem();
                item.setSettingItemId(settingItemId);
                item.setSettingItemName(settingItemName);
                item.setSettingItemType(settingItemType);
                item.setSettingItemSorted(settingItemSorted);
                setting.getSettingItems().add(item);
            }
            
            if (!errors.isEmpty()) {
                throw new BizException("导入失败:\n" + String.join(";\n", errors));
            }
            
            // 保存所有配置
            for (GlobalSetting setting : settingsMap.values()) {
                if (globalSettingRepository.existsBySettingId(setting.getSettingId())) {
                    GlobalSetting existingSetting = globalSettingRepository.findBySettingId(setting.getSettingId());
                    setting.setSettingId(existingSetting.getSettingId());
                }
                globalSettingRepository.save(setting);
            }
            
            return true;
        } catch (IOException e) {
            throw new BizException("导入失败: " + e.getMessage());
        }
    }

    @Override
    public void exportData(HttpServletResponse response, String settingId) {
        GlobalSetting setting = globalSettingRepository.findBySettingId(settingId);
        if (setting == null) {
            throw new RuntimeException("配置不存在: " + settingId);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(StringUtils.hasText(setting.getSettingName()) ?
                    setting.getSettingName() : "配置数据");

            // ============== 样式定义 ==============
            // 主标题样式
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);

            // 数据行样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setWrapText(true);

            // 特殊字段样式
            CellStyle typeStyle = workbook.createCellStyle();
            typeStyle.cloneStyleFrom(dataStyle);
            Font typeFont = workbook.createFont();
            typeFont.setColor(IndexedColors.BLUE.getIndex());
            typeStyle.setFont(typeFont);

            // ============== 添加主标题 ==============
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(setting.getSettingName() + "（" + setting.getSettingId() + "）" + " - 配置详情");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // ============== 创建表头 ==============
            Row headerRow = sheet.createRow(1);
            String[] headers = {
                    "配置名称", "配置编码", "配置项编码",
                    "配置项名称", "配置项类型", "配置项排序"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ============== 写入数据 ==============
            int rowNum = 2;
            for (GlobalSettingItem item : setting.getSettingItems()) {
                Row row = sheet.createRow(rowNum++);

                // 配置名称
                Cell nameCell = row.createCell(0);
                nameCell.setCellValue(setting.getSettingName());
                nameCell.setCellStyle(dataStyle);

                // 配置编码
                Cell idCell = row.createCell(1);
                idCell.setCellValue(setting.getSettingId());
                idCell.setCellStyle(dataStyle);

                // 配置项编码
                Cell itemIdCell = row.createCell(2);
                itemIdCell.setCellValue(item.getSettingItemId());
                itemIdCell.setCellStyle(dataStyle);

                // 配置项名称
                Cell itemNameCell = row.createCell(3);
                itemNameCell.setCellValue(item.getSettingItemName());
                itemNameCell.setCellStyle(dataStyle);

                // 配置项类型（特殊样式）
                Cell typeCell = row.createCell(4);
                typeCell.setCellValue(getTypeName(item.getSettingItemType()));
                typeCell.setCellStyle(typeStyle);

                // 配置项排序
                Cell sortCell = row.createCell(5);
                sortCell.setCellValue(item.getSettingItemSorted());
                sortCell.setCellStyle(dataStyle);
            }

            // ============== 设置列宽 ==============
            int[] columnWidths = {25, 20, 20, 25, 15, 10};
            for (int i = 0; i < columnWidths.length; i++) {
                sheet.setColumnWidth(i, columnWidths[i] * 256);
            }

            // ============== 添加自动筛选 ==============
            sheet.setAutoFilter(new CellRangeAddress(1, rowNum, 0, 5));

            // ============== 冻结标题和表头 ==============
            sheet.createFreezePane(0, 2, 0, 2);

            // ============== 设置响应头 ==============
            String fileName = String.format("%s(%s)_配置详情_%s.xlsx",
                    setting.getSettingName(),
                    setting.getSettingId(),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            );

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出配置数据失败 - {}", settingId, e);
            throw new BizException("导出配置数据失败" + e.getMessage());
        }
    }


    private Integer convertSettingItemType(String typeStr) {
        if (typeStr == null) {
            return null;
        }
        switch (typeStr.trim()) {
            case "字符串": return 0;
            case "整数": return 1;
            case "小数": return 2;
            default: return null;
        }
    }

    private String getTypeName(Integer type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case 0: return "字符串";
            case 1: return "整数";
            case 2: return "小数";
            default: return "";
        }
    }
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理整数和小数
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    // 尝试获取公式计算结果
                    return getCellStringValue(cell.getSheet().getRow(cell.getRowIndex())
                            .getCell(cell.getColumnIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                } catch (Exception ex) {
                    return cell.getCellFormula();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

}
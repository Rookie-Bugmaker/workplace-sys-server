package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.MongoResResult;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.parameter.GlobalSetting;
import com.crrc.sys.entity.parameter.GlobalSettingItem;
import com.crrc.sys.entity.parameter.GlobalSettingItemResult;
import com.crrc.sys.entity.parameter.GlobalSettingValue;
import com.crrc.sys.mapper.mongodb.GlobalSettingRepository;
import com.crrc.sys.mapper.mongodb.GlobalSettingValueRepository;
import com.crrc.sys.model.parameter.MongoPage;
import com.crrc.sys.service.GlobalSettingValueService;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GlobalSettingValueServiceImpl implements GlobalSettingValueService {

    @Autowired
    private GlobalSettingValueRepository globalSettingValueRepository;

    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String create(GlobalSettingValue globalSettingValue) {
        // 检查配置是否存在
        GlobalSetting setting = globalSettingRepository.findBySettingId(globalSettingValue.getSettingId());
        if (setting == null) {
            throw new BizException("配置不存在");
        }

        // 检查配置项编码是否在配置属性集合中存在
        Set<String> validItemIds = setting.getSettingItems().stream()
                .map(GlobalSettingItem::getSettingItemId)
                .collect(Collectors.toSet());

        Set<String> itemIdsInValue = new HashSet<>();
        for (GlobalSettingItemResult itemResult : globalSettingValue.getItemResults()) {
            // 检查配置项编码是否重复
            if (itemIdsInValue.contains(itemResult.getSettingItemId())) {
                throw new BizException("配置项编码重复: " + itemResult.getSettingItemId());
            }
            itemIdsInValue.add(itemResult.getSettingItemId());

            // 检查配置项编码是否在配置属性集合中存在
            if (!validItemIds.contains(itemResult.getSettingItemId())) {
                throw new BizException("配置项编码不存在: " + itemResult.getSettingItemId());
            }

            // 检查类型是否匹配
            GlobalSettingItem settingItem = setting.getSettingItems().stream()
                    .filter(item -> item.getSettingItemId().equals(itemResult.getSettingItemId()))
                    .findFirst()
                    .orElse(null);

            if (settingItem != null) {
                try {
                    convertValueByType(itemResult.getSettingItemContent(), settingItem.getSettingItemType());
                } catch (IllegalArgumentException e) {
                    throw new BizException("配置项" + itemResult.getSettingItemId() + "类型不匹配: " + e.getMessage());
                }
            }
        }
        globalSettingValueRepository.save(globalSettingValue);
        return globalSettingValue.getId();
    }

    @Override
    public Boolean update(GlobalSettingValue globalSettingValue) {
        // 检查配置值是否存在
        GlobalSettingValue existingValue = globalSettingValueRepository.findById(globalSettingValue.getId()).orElse(null);
        if (existingValue == null) {
            throw new BizException("配置不存在");
        }

        // 检查配置是否存在
        GlobalSetting setting = globalSettingRepository.findBySettingId(globalSettingValue.getSettingId());
        if (setting == null) {
            throw new BizException("配置不存在");
        }

        // 检查配置项编码是否在配置属性集合中存在
        Set<String> validItemIds = setting.getSettingItems().stream()
                .map(GlobalSettingItem::getSettingItemId)
                .collect(Collectors.toSet());

        Set<String> itemIdsInValue = new HashSet<>();
        for (GlobalSettingItemResult itemResult : globalSettingValue.getItemResults()) {
            // 检查配置项编码是否重复
            if (itemIdsInValue.contains(itemResult.getSettingItemId())) {
                throw new BizException("配置项编码重复: " + itemResult.getSettingItemId());
            }
            itemIdsInValue.add(itemResult.getSettingItemId());

            // 检查配置项编码是否在配置属性集合中存在
            if (!validItemIds.contains(itemResult.getSettingItemId())) {
                throw new BizException("配置项编码不存在: " + itemResult.getSettingItemId());
            }

            // 检查类型是否匹配
            GlobalSettingItem settingItem = setting.getSettingItems().stream()
                    .filter(item -> item.getSettingItemId().equals(itemResult.getSettingItemId()))
                    .findFirst()
                    .orElse(null);

            if (settingItem != null) {
                try {
                    convertValueByType(itemResult.getSettingItemContent(), settingItem.getSettingItemType());
                } catch (IllegalArgumentException e) {
                    throw new BizException("配置项" + itemResult.getSettingItemId() + "类型不匹配: " + e.getMessage());
                }
            }
        }

        globalSettingValueRepository.save(globalSettingValue);
        return true;
    }

    @Override
    public Boolean delete(String id) {
        GlobalSettingValue value = globalSettingValueRepository.findById(id).orElse(null);
        if (value == null) {
            throw new BizException("id为"+id+"配置值不存在");
        }
        globalSettingValueRepository.delete(value);
        return true;
    }

    @Override
    public GlobalSettingValue getById(String id) {
        GlobalSettingValue value = globalSettingValueRepository.findById(id).orElse(null);
        return value;
    }

    @Override
    public List<GlobalSettingValue> getBySettingId(String settingId) {
        List<GlobalSettingValue> value = globalSettingValueRepository.findAllBySettingId(settingId);
        return value;
    }

    @Override
    public List<GlobalSettingValue> getAll() {
        List<GlobalSettingValue> values = globalSettingValueRepository.findAll();
        return values;
    }

    @Override
    public PaginationGrid<GlobalSettingValue> getByPage(MongoPage pageRequest) {
        // 1. 构建基础查询条件
        Query baseQuery = new Query();
        Query dataQuery = new Query();

        // 添加settingId精准查询条件
        if (StringUtils.hasText(pageRequest.getSettingId())) {
            baseQuery.addCriteria(Criteria.where("setting_id").is(pageRequest.getSettingId()));
            dataQuery.addCriteria(Criteria.where("setting_id").is(pageRequest.getSettingId()));
        }

        // 添加settingItemId和settingItemContent的组合查询条件
        if (StringUtils.hasText(pageRequest.getSettingItemId()) && StringUtils.hasText(pageRequest.getSettingItemContent())) {
            Criteria elemMatchCriteria = Criteria.where("settingItemId").is(pageRequest.getSettingItemId())
                    .and("settingItemContent").is(pageRequest.getSettingItemContent());

            baseQuery.addCriteria(Criteria.where("item_results").elemMatch(elemMatchCriteria));
            dataQuery.addCriteria(Criteria.where("item_results").elemMatch(elemMatchCriteria));
        }
        // 单独查询settingItemId的情况
        else if (StringUtils.hasText(pageRequest.getSettingItemId())) {
            baseQuery.addCriteria(Criteria.where("item_results.settingItemId").is(pageRequest.getSettingItemId()));
            dataQuery.addCriteria(Criteria.where("item_results.settingItemId").is(pageRequest.getSettingItemId()));
        }
        // 单独查询settingItemContent的情况
        else if (StringUtils.hasText(pageRequest.getSettingItemContent())) {
            baseQuery.addCriteria(Criteria.where("item_results.settingItemContent").is(pageRequest.getSettingItemContent()));
            dataQuery.addCriteria(Criteria.where("item_results.settingItemContent").is(pageRequest.getSettingItemContent()));
        }

        // 2. 获取总数（使用基础查询条件）
        long total = mongoTemplate.count(baseQuery, GlobalSettingValue.class);

        // 3. 设置分页和排序
        int skip = (pageRequest.getPageNum() - 1) * pageRequest.getPageSize();
        dataQuery.skip(skip).limit(pageRequest.getPageSize());

        if (StringUtils.hasText(pageRequest.getSortField())) {
            Sort.Direction direction = Sort.Direction.ASC;
            if ("desc".equalsIgnoreCase(pageRequest.getSortOrder())) {
                direction = Sort.Direction.DESC;
            }
            dataQuery.with(Sort.by(direction, pageRequest.getSortField()));
        }

        // 执行查询
        List<GlobalSettingValue> list = mongoTemplate.find(dataQuery, GlobalSettingValue.class);

        // 构建分页结果
        IPage<GlobalSettingValue> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        page.setRecords(list);
        page.setTotal(total);

        return PaginationGrid.success(page);
    }

    @Override
    public void exportTemplate(HttpServletResponse response, String settingId) {
        // 查询配置信息
        GlobalSetting setting = globalSettingRepository.findBySettingId(settingId);
        if (setting == null) {
            throw new BizException("配置不存在: " + settingId);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("配置值模板");

            // ============== 样式定义 ==============
            // 主标题样式
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // 数据行样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setWrapText(true);

            // 配置编码样式（特殊标记）
            CellStyle settingIdStyle = workbook.createCellStyle();
            settingIdStyle.cloneStyleFrom(dataStyle);
            settingIdStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            settingIdStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font settingIdFont = workbook.createFont();
            settingIdFont.setColor(IndexedColors.RED.getIndex());
            settingIdStyle.setFont(settingIdFont);

            // ============== 添加主标题 ==============
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(setting.getSettingName() + "（"  + setting.getSettingId() + "）" + " - 配置值导入模板");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, setting.getSettingItems().size()));

            // ============== 创建表头行 ==============
            Row headerRow = sheet.createRow(1);

            // 配置编码列
//            headerRow.createCell(0).setCellValue("配置编码");
//            headerRow.getCell(0).setCellStyle(headerStyle);

            // 添加配置项名称作为表头
            int colNum = 0;
            for (GlobalSettingItem item : setting.getSettingItems()) {
                Cell headerCell = headerRow.createCell(colNum++);
                headerCell.setCellValue(item.getSettingItemName());
                headerCell.setCellStyle(headerStyle);

                // 添加注释说明配置项类型
                if (item.getSettingItemType() != null ) {
                    CreationHelper factory = workbook.getCreationHelper();
                    ClientAnchor anchor = factory.createClientAnchor();
                    anchor.setCol1(headerCell.getColumnIndex());
                    anchor.setCol2(headerCell.getColumnIndex() + 2);
                    anchor.setRow1(headerRow.getRowNum());
                    anchor.setRow2(headerRow.getRowNum() + 3);

                    Drawing drawing = sheet.createDrawingPatriarch();
                    Comment comment = drawing.createCellComment(anchor);
                    RichTextString str = factory.createRichTextString("配置项类型: " + getTypeName(item.getSettingItemType()));
                    comment.setString(str);
                    headerCell.setCellComment(comment);
                }
            }

            // ============== 设置列宽 ==============
            sheet.setColumnWidth(0, 20 * 256);  // 配置编码列宽
            for (int i = 1; i < colNum; i++) {
                sheet.setColumnWidth(i, 25 * 256);  // 配置项列宽
            }

            // ============== 冻结标题和表头 ==============
            sheet.createFreezePane(0, 2, 0, 2);
            // ============== 设置响应头 ==============
            String fileName = String.format("%s(%s)_配置值模板_%s.xlsx",
                    setting.getSettingName(),
                    setting.getSettingId(),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            );

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出配置值模板失败 - {}", settingId, e);
            throw new RuntimeException("导出配置值模板失败", e);
        }
    }

    @Override
    public Boolean importData(MultipartFile file, String settingId) {
        GlobalSetting setting = globalSettingRepository.findBySettingId(settingId);
        if (setting == null) {
            throw new BizException("配置不存在");
        }

        // 创建配置项名称到编码的映射
        Map<String, String> itemNameToIdMap = setting.getSettingItems().stream()
                .collect(Collectors.toMap(GlobalSettingItem::getSettingItemName, GlobalSettingItem::getSettingItemId));

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            // 跳过表头
            if (!rowIterator.hasNext()) {
                throw new BizException("Excel文件为空");
            }
            rowIterator.next(); // 跳过标题行

            // 现在获取的才是真正的表头行
            if (!rowIterator.hasNext()) {
                throw new BizException("缺少表头行");
            }
            Row headerRow = rowIterator.next();

            // 解析表头，获取配置项名称和对应的列索引
            Map<String, Integer> itemNameToColumnMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String itemName = getCellStringValue(cell);
                    itemNameToColumnMap.put(itemName, i);
                }
            }
            
            // 检查是否有无效的配置项名称
            List<String> invalidItemNames = itemNameToColumnMap.keySet().stream()
                    .filter(name -> !itemNameToIdMap.containsKey(name))
                    .collect(Collectors.toList());
            
            if (!invalidItemNames.isEmpty()) {
                throw new BizException("Excel中包含无效的配置项名称: " + String.join(", ", invalidItemNames));
            }
            
            List<String> errors = new ArrayList<>();
            List<GlobalSettingValue> valuesToSave = new ArrayList<>();
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                GlobalSettingValue value = new GlobalSettingValue();
                value.setSettingId(settingId);
                List<GlobalSettingItemResult> itemResults = new ArrayList<>();
                
                for (Map.Entry<String, Integer> entry : itemNameToColumnMap.entrySet()) {
                    String itemName = entry.getKey();
                    int columnIndex = entry.getValue();
                    
                    String itemId = itemNameToIdMap.get(itemName);
                    String itemContent = getCellStringValue(row.getCell(columnIndex));
                    
                    // 获取配置项类型
                    GlobalSettingItem settingItem = setting.getSettingItems().stream()
                            .filter(item -> item.getSettingItemId().equals(itemId))
                            .findFirst()
                            .orElse(null);
                    
                    if (settingItem != null) {
                        try {
                            // 验证类型并转换
                            String convertedValue = convertValueByType(itemContent, settingItem.getSettingItemType());
                            
                            GlobalSettingItemResult itemResult = new GlobalSettingItemResult();
                            itemResult.setSettingItemId(itemId);
                            itemResult.setSettingItemContent(convertedValue);
                            itemResults.add(itemResult);
                        } catch (IllegalArgumentException e) {
                            errors.add("第" + (row.getRowNum() + 1) + "行: 配置项" + itemName + "类型不匹配 - " + e.getMessage());
                        }
                    }
                }
                
                if (!errors.isEmpty()) {
                    continue;
                }
                
                value.setItemResults(itemResults);
                valuesToSave.add(value);
            }
            
            if (!errors.isEmpty()) {
                throw new BizException("导入失败: "+errors);
            }
            
            // 保存所有配置值
            for (GlobalSettingValue value : valuesToSave) {
                globalSettingValueRepository.save(value);
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("导入失败: " + e.getMessage());
        }
    }

    @Override
    public void exportData(HttpServletResponse response, String settingId) {
        // 查询配置信息
        GlobalSetting setting = globalSettingRepository.findBySettingId(settingId);
        if (setting == null) {
            throw new RuntimeException("配置不存在: " + settingId);
        }

        // 查询配置值
        List<GlobalSettingValue> values = globalSettingValueRepository.findAllBySettingId(settingId);

        if (values == null || values.isEmpty()) {
            throw new RuntimeException("配置值不存在: " + settingId);
        }

        // 创建配置项编码到内容的映射
        List<Map<String, String>> itemIdToContentMapList = values.stream().map(value -> {
                    return value.getItemResults().stream()
                            .collect(Collectors.toMap(
                                    GlobalSettingItemResult::getSettingItemId,
                                    GlobalSettingItemResult::getSettingItemContent,
                                    (existing, replacement) -> existing
                            ));
        }).collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(StringUtils.hasText(setting.getSettingName()) ?
                    setting.getSettingName() : "配置值数据");

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
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
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

            // 配置编码特殊样式
            CellStyle settingIdStyle = workbook.createCellStyle();
            settingIdStyle.cloneStyleFrom(dataStyle);
            settingIdStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            settingIdStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font settingIdFont = workbook.createFont();
            settingIdFont.setBold(true);
            settingIdStyle.setFont(settingIdFont);

            // ============== 添加主标题 ==============
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(setting.getSettingName() + "（" + setting.getSettingId() + "）" + " - 配置值数据");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(
                    0, 0, 0, setting.getSettingItems().size() + 1
            ));

            // ============== 创建表头 ==============
            Row headerRow = sheet.createRow(1);

            // 基础信息列
            headerRow.createCell(0).setCellValue("配置编码");
            headerRow.getCell(0).setCellStyle(headerStyle);

            headerRow.createCell(1).setCellValue("配置名称");
            headerRow.getCell(1).setCellStyle(headerStyle);

            // 配置项列
            int colNum = 2;
            for (GlobalSettingItem item : setting.getSettingItems()) {
                Cell headerCell = headerRow.createCell(colNum++);
                headerCell.setCellValue(item.getSettingItemName());
                headerCell.setCellStyle(headerStyle);

                // 添加注释说明配置项类型
                if (item.getSettingItemType() != null) {
                    CreationHelper factory = workbook.getCreationHelper();
                    ClientAnchor anchor = factory.createClientAnchor();
                    anchor.setCol1(headerCell.getColumnIndex());
                    anchor.setCol2(headerCell.getColumnIndex() + 2);
                    anchor.setRow1(headerRow.getRowNum());
                    anchor.setRow2(headerRow.getRowNum() + 3);

                    Drawing drawing = sheet.createDrawingPatriarch();
                    Comment comment = drawing.createCellComment(anchor);
                    RichTextString str = factory.createRichTextString(
                            "配置项类型: " + getTypeName(item.getSettingItemType())
                    );
                    comment.setString(str);
                    headerCell.setCellComment(comment);
                }
            }

            // ============== 写入数据 ==============
            AtomicReference<Integer> row = new AtomicReference<>(2);
            AtomicReference<Integer> dataNum = new AtomicReference<>(2);
            itemIdToContentMapList.forEach(itemIdToContentMap -> {
                Row dataRow = sheet.createRow(row.get());

                // 配置编码
                Cell settingIdCell = dataRow.createCell(0);
                settingIdCell.setCellValue(settingId);
                settingIdCell.setCellStyle(settingIdStyle);

                // 配置名称
                Cell settingNameCell = dataRow.createCell(1);
                settingNameCell.setCellValue(setting.getSettingName());
                settingNameCell.setCellStyle(dataStyle);

                // 配置项值

                int cellNum = 2;
                for (GlobalSettingItem item : setting.getSettingItems()) {
                    Cell dataCell = dataRow.createCell(cellNum++);
                    String content = itemIdToContentMap.get(item.getSettingItemId());
                    dataCell.setCellValue(content != null ? content : "");
                    dataCell.setCellStyle(dataStyle);

                    // 根据类型设置单元格格式
                    if (item.getSettingItemType() == 1) {
                        dataCell.setCellType(CellType.NUMERIC);
                        if (StringUtils.hasText(content)) {
                            try {
                                dataCell.setCellValue(Integer.parseInt(content));
                            } catch (NumberFormatException e) {
                                // 保持原值
                            }
                        }
                    } else if (item.getSettingItemType() == 2) {
                        dataCell.setCellType(CellType.NUMERIC);
                        if (StringUtils.hasText(content)) {
                            try {
                                dataCell.setCellValue(Double.parseDouble(content));
                            } catch (NumberFormatException e) {
                                // 保持原值
                            }
                        }
                    }
                }
                if(row.get() == 2) {
                    dataNum.set(cellNum);
                }
                row.getAndSet(row.get() + 1);
            });

            // ============== 设置列宽 ==============
            sheet.setColumnWidth(0, 20 * 256);  // 配置编码列
            sheet.setColumnWidth(1, 25 * 256);  // 配置名称列

            for (int i = 2; i < dataNum.get(); i++) {
                sheet.setColumnWidth(i, 30 * 256);  // 配置项值列
            }

            // ============== 冻结标题和表头 ==============
            sheet.createFreezePane(0, 2, 0, 2);

            // ============== 设置响应头 ==============
            String fileName = String.format("%s(%s)_配置值数据_%s.xlsx",
                    setting.getSettingName(),
                    setting.getSettingId(),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            );

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出配置值数据失败 - {}", settingId, e);
            throw new RuntimeException("导出配置值数据失败", e);
        }
    }


    @Override
    public MongoResResult getByCode(String settingId) {
        // 获取配置值和配置定义
        List<GlobalSettingValue> values = globalSettingValueRepository.findAllBySettingId(settingId);
        GlobalSetting globalSetting = globalSettingRepository.findBySettingId(settingId);

        if (values == null || values.isEmpty()) {
            throw new BizException("配置值不存在");
        }

        if (globalSetting == null || globalSetting.getSettingItems() == null) {
            throw new BizException("配置定义不存在");
        }

        // 获取按settingItemSorted排序的settingItemId列表
        List<String> sortedItemIds = globalSetting.getSettingItems().stream()
                .sorted(Comparator.comparingInt(GlobalSettingItem::getSettingItemSorted))
                .map(GlobalSettingItem::getSettingItemId)
                .collect(Collectors.toList());

        // 处理每个配置值
        List<Map<String, Object>> resultList = new ArrayList<>();
        values.forEach(value -> {
            // 使用LinkedHashMap保持插入顺序
            Map<String, Object> orderedResult = new LinkedHashMap<>();

            // 按照预定义的排序顺序处理每个item
            sortedItemIds.forEach(itemId -> {
                // 在当前value中查找对应的itemResult
                value.getItemResults().stream()
                        .filter(item -> itemId.equals(item.getSettingItemId()))
                        .findFirst()
                        .ifPresent(item -> orderedResult.put(itemId, item.getSettingItemContent()));
            });

            resultList.add(orderedResult);
        });

        return MongoResResult.success("查询成功", resultList);
    }


    @Override
    public MongoResResult getByCode(String settingId, String settingItemId, String settingItemContent, String pattern) {
        List<GlobalSettingValue> values = null;
        if("like".equals(pattern)) {
            values = globalSettingValueRepository.findBySettingIdAndItemResultsWithRegex(settingId, settingItemId, settingItemContent);
        }else {
            values = globalSettingValueRepository.findBySettingIdAndItemResults(settingId, settingItemId, settingItemContent);
        }
        GlobalSetting globalSetting = globalSettingRepository.findBySettingId(settingId);

        if (values == null || values.isEmpty()) {
            throw new BizException("配置值不存在");
        }

        if (globalSetting == null || globalSetting.getSettingItems() == null) {
            throw new BizException("配置定义不存在");
        }

        // 获取按settingItemSorted排序的settingItemId列表
        List<String> sortedItemIds = globalSetting.getSettingItems().stream()
                .sorted(Comparator.comparingInt(GlobalSettingItem::getSettingItemSorted))
                .map(GlobalSettingItem::getSettingItemId)
                .collect(Collectors.toList());

        // 处理每个配置值
        List<Map<String, Object>> resultList = new ArrayList<>();
        values.forEach(value -> {
            // 使用LinkedHashMap保持插入顺序
            Map<String, Object> orderedResult = new LinkedHashMap<>();

            // 按照预定义的排序顺序处理每个item
            sortedItemIds.forEach(itemId -> {
                // 在当前value中查找对应的itemResult
                value.getItemResults().stream()
                        .filter(item -> itemId.equals(item.getSettingItemId()))
                        .findFirst()
                        .ifPresent(item -> orderedResult.put(itemId, item.getSettingItemContent()));
            });

            resultList.add(orderedResult);
        });

        return MongoResResult.success("查询成功", resultList);
    }

    private String convertValueByType(String value, Integer type) throws IllegalArgumentException {
        if (value == null || value.isEmpty()) {
            return "";
        }

        switch (type) {
            case 0: // 字符串
                return value;
            case 1: // 整数
                try {
                    Integer.parseInt(value);
                    return value;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("必须是整数");
                }
            case 2: // 小数
                try {
                    Double.parseDouble(value);
                    return value;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("必须是小数");
                }
            default:
                throw new IllegalArgumentException("未知的类型");
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
package com.cccr.sys.entity.excel;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

/**
 * 所有实体必须继承基excel实体
 * <p>
 * 全局设置excel的行高,列宽,有特殊需求,可以在自己的实体上加行高、列宽的限制
 * </p>
 *
 * @author huangsen
 * @date 2023/5/24 12:26
 */
@ContentRowHeight(22)
@HeadRowHeight(26)
@ColumnWidth(19)
public class BaseExcelEntity {
}

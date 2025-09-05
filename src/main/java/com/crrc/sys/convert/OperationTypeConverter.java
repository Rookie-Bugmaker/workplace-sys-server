package com.crrc.sys.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.crrc.sys.enums.OperateType;

public class OperationTypeConverter implements Converter<OperateType> {

    @Override
    public Class supportJavaTypeKey() {
        return null;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public OperateType convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return OperateType.fromDescription(cellData.toString());
    }

    //导出时转换
    @Override
    public WriteCellData<?> convertToExcelData(OperateType value, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new WriteCellData<>(value.getDescription());
    }

}

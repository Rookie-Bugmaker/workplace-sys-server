package com.crrc.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crrc.sys.entity.filedata.FileData;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文件数据表 Mapper 接口
 * </p>
 *
 * @author lijiangtao
 * @since 2025-06-16 11:13:04
 */
@Mapper
public interface FileDataMapper extends BaseMapper<FileData> {

}
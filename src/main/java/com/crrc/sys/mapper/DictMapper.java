package com.crrc.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crrc.sys.entity.dict.Dict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 字典 Mapper 接口
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

}

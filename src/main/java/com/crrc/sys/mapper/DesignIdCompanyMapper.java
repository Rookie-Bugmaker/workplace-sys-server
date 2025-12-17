package com.crrc.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crrc.sys.entity.iddesign.DesignIdCompany;
import com.crrc.sys.entity.iddesign.IdDesign;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 自定义ID-公司隔离 Mapper 接口
 * </p>
 *
 * @author lijiangtao
 * @since 2025-12-16 09:55:04
 */
@Mapper
public interface DesignIdCompanyMapper extends BaseMapper<DesignIdCompany> {

}
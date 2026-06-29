package com.crrc.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.entity.measureunit.MeasureUnitListVo;
import com.crrc.sys.entity.measureunit.MeasureUnitQueryVo;
import com.crrc.sys.entity.measureunit.SysMdBaseMeasureUnit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysMdBaseMeasureUnitMapper extends BaseMapper<SysMdBaseMeasureUnit> {

    Page<MeasureUnitListVo> pageMeasureUnit(Page<?> page,
                                            @Param("vo") MeasureUnitQueryVo vo);

}
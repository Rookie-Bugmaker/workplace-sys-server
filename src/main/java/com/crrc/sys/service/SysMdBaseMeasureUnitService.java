package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.measureunit.MeasureUnitListVo;
import com.crrc.sys.entity.measureunit.MeasureUnitQueryVo;

public interface SysMdBaseMeasureUnitService {

    PaginationGrid<MeasureUnitListVo> page(MeasureUnitQueryVo vo);

}
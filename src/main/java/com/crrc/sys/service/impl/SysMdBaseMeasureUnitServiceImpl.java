package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.measureunit.MeasureUnitListVo;
import com.crrc.sys.entity.measureunit.MeasureUnitQueryVo;
import com.crrc.sys.mapper.SysMdBaseMeasureUnitMapper;
import com.crrc.sys.service.SysMdBaseMeasureUnitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysMdBaseMeasureUnitServiceImpl implements SysMdBaseMeasureUnitService {

    @Resource
    private SysMdBaseMeasureUnitMapper measureUnitMapper;

    @Override
    public PaginationGrid<MeasureUnitListVo> page(MeasureUnitQueryVo vo) {

        if (vo == null) {
            vo = new MeasureUnitQueryVo();
        }

        Page<MeasureUnitListVo> page =
                new Page<>(vo.getPageNum(), vo.getPageSize());

        Page<MeasureUnitListVo> resultPage =
                measureUnitMapper.pageMeasureUnit(page, vo);

        return PaginationGrid.success(resultPage);
    }
}
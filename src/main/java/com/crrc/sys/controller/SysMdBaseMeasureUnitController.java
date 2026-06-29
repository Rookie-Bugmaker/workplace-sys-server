package com.crrc.sys.controller;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.measureunit.MeasureUnitListVo;
import com.crrc.sys.entity.measureunit.MeasureUnitQueryVo;
import com.crrc.sys.service.SysMdBaseMeasureUnitService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/base/measureUnit")
public class SysMdBaseMeasureUnitController {

    @Resource
    private SysMdBaseMeasureUnitService measureUnitService;

    @PostMapping("/page")
    public PaginationGrid<MeasureUnitListVo> page(
            @RequestBody(required = false) MeasureUnitQueryVo vo) {

        return measureUnitService.page(vo);
    }
}
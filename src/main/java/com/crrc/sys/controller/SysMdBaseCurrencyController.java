package com.crrc.sys.controller;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.currency.CurrencyListVo;
import com.crrc.sys.entity.currency.CurrencyQueryVo;
import com.crrc.sys.service.SysMdBaseCurrencyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/base/currency")
public class SysMdBaseCurrencyController {

    @Resource
    private SysMdBaseCurrencyService currencyService;

    @PostMapping("/page")
    public PaginationGrid<CurrencyListVo> page(@RequestBody(required = false) CurrencyQueryVo vo) {
        return currencyService.page(vo);
    }
}
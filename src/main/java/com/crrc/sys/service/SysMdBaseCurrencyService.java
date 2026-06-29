package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.currency.CurrencyListVo;
import com.crrc.sys.entity.currency.CurrencyQueryVo;

public interface SysMdBaseCurrencyService {

    PaginationGrid<CurrencyListVo> page(CurrencyQueryVo vo);
}
package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.currency.CurrencyListVo;
import com.crrc.sys.entity.currency.CurrencyQueryVo;
import com.crrc.sys.mapper.SysMdBaseCurrencyMapper;
import com.crrc.sys.service.SysMdBaseCurrencyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysMdBaseCurrencyServiceImpl implements SysMdBaseCurrencyService {

    @Resource
    private SysMdBaseCurrencyMapper currencyMapper;

    @Override
    public PaginationGrid<CurrencyListVo> page(CurrencyQueryVo vo) {
        if (vo == null) {
            vo = new CurrencyQueryVo();
        }

        Page<CurrencyListVo> page = new Page<>(vo.getPageNum(), vo.getPageSize());
        Page<CurrencyListVo> resultPage = currencyMapper.pageCurrency(page, vo);

        return PaginationGrid.success(resultPage);
    }
}
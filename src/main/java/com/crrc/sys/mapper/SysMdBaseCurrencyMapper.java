package com.crrc.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.entity.currency.CurrencyListVo;
import com.crrc.sys.entity.currency.CurrencyQueryVo;
import com.crrc.sys.entity.currency.SysMdBaseCurrency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysMdBaseCurrencyMapper extends BaseMapper<SysMdBaseCurrency> {

    Page<CurrencyListVo> pageCurrency(Page<?> page, @Param("vo") CurrencyQueryVo vo);
}
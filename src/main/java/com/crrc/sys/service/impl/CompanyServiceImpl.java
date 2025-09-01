package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.company.Company;
import com.crrc.sys.mapper.CompanyMapper;
import com.crrc.sys.service.CompanyService;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
}

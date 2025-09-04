package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.datasource.DataSourceEntity;
import com.crrc.sys.mapper.DataSourceMapper;
import com.crrc.sys.service.DataSourceService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSourceEntity> implements DataSourceService {
}

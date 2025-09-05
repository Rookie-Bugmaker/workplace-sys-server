package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.entity.log.AuditLogEntity;
import com.crrc.sys.mapper.AuditLogMapper;
import com.crrc.sys.service.AuditLogService;
import org.springframework.stereotype.Service;


@Service("AuditLogService")
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLogEntity> implements AuditLogService {
}

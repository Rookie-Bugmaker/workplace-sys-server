package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.mapper.AgentMapper;
import com.crrc.sys.model.agent.SysAgentPO;
import com.crrc.sys.service.AgentService;
import org.springframework.stereotype.Service;

/**
 * @author 谭永强
 * @date 2025-11-13
 */
@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, SysAgentPO> implements AgentService {
}

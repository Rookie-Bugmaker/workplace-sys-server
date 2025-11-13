package com.crrc.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.client.FileClient;
import com.crrc.sys.common.IdKeyGenerator;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.enums.OperationTypeEnum;
import com.crrc.sys.model.agent.SysAgentDTO;
import com.crrc.sys.model.agent.SysAgentPO;
import com.crrc.sys.model.file.SysFileDTO;
import com.crrc.sys.service.AgentService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能体配置
 *
 * @author 谭永强
 * @date 2025-11-12
 */
@RestController
@RequestMapping("/agent")
public class AgentController {

    @Resource
    private IdKeyGenerator idKeyGenerator;
    @Resource
    private AgentService agentService;
    @Resource
    private FileClient fileClient;

    /**
     * 分页查询
     *
     * @param param 查询条件
     */
    @PostMapping("page")
    public PaginationGrid<SysAgentPO> page(@RequestBody SysAgentDTO param) {
        if (ObjectUtils.isEmpty(param.getPage())) {
            throw new ParamValidateException("page不能为空");
        }
        if (ObjectUtils.isEmpty(param.getPageSize())) {
            throw new ParamValidateException("pageSize不能为空");
        }
        Page<SysAgentPO> page = new Page<>(param.getPage(), param.getPageSize());
        LambdaQueryWrapper<SysAgentPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!ObjectUtils.isEmpty(param.getAgentName()), SysAgentPO::getAgentName, param.getAgentName());
        queryWrapper.like(!ObjectUtils.isEmpty(param.getAgentCode()), SysAgentPO::getAgentCode, param.getAgentCode());
        queryWrapper.eq(!ObjectUtils.isEmpty(param.getAgentDesc()), SysAgentPO::getAgentDesc, param.getAgentDesc());
        page = agentService.page(page, queryWrapper);
        List<SysAgentPO> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(o -> o.setApiKey(null));
            List<Long> fileHeaderIdList = records.stream().map(SysAgentPO::getFileHeaderId).filter(o -> !ObjectUtils.isEmpty(o)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(fileHeaderIdList)) {
                return PaginationGrid.success(page);
            }
            ResultData<List<SysFileDTO>> resultData = fileClient.findByFileHeaderIds(fileHeaderIdList);
            if (!resultData.getSuccess()) {
                return PaginationGrid.success(page);
            }
            List<SysFileDTO> fileList = resultData.getResult();
            if (CollectionUtils.isEmpty(fileList)) {
                return PaginationGrid.success(page);
            }
            records.forEach(o -> {
                SysFileDTO sysFileDTO = fileList.stream().filter(f -> f.getFileHeaderId().equals(o.getFileHeaderId())).findFirst().orElse(null);
                if (!ObjectUtils.isEmpty(sysFileDTO)) {
                    o.setFileId(sysFileDTO.getFileId());
                }
            });
        }
        return PaginationGrid.success(page);
    }


    /**
     * 智能体新增
     *
     * @param agent 智能体配置
     */
    @PostMapping("add")
    public void add(@RequestBody SysAgentPO agent) {
        verification(OperationTypeEnum.ADD.getValue(), agent);
        agent.setId(idKeyGenerator.getId());
        boolean result = agentService.save(agent);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 智能体新增
     *
     * @param agent 智能体配置
     */
    @PutMapping("update")
    public void update(@RequestBody SysAgentPO agent) {
        verification(OperationTypeEnum.UPDATE.getValue(), agent);
        boolean result = agentService.saveOrUpdate(agent);
        if (!result) {
            throw new BizException("保存失败!");
        }
    }

    /**
     * 智能体山删除
     *
     * @param idList 智能体id
     */
    @DeleteMapping("delete")
    public void update(@RequestBody List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new ParamValidateException("参数错误");
        }
        boolean result = agentService.removeByIds(idList);
        if (!result) {
            throw new BizException("删除失败!");
        }
    }

    /**
     * 根据ID查询
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("findById")
    public SysAgentPO findById(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ParamValidateException("id不能为空!");
        }
        return agentService.getById(id);
    }

    /**
     * 根据智能体编码查询
     *
     * @param agentCode 智能体编码
     * @return 智能体配置信息
     */
    private SysAgentPO findByCode(String agentCode) {
        if (ObjectUtils.isEmpty(agentCode)) {
            throw new ParamValidateException("agentCode不能为空!");
        }
        LambdaQueryWrapper<SysAgentPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysAgentPO::getAgentCode, agentCode);
        return agentService.getOne(lambdaQueryWrapper);
    }

    /**
     * 公共参数校验
     *
     * @param operation 新增或修改
     * @param agent     智能体信息
     */
    private void verification(String operation, SysAgentPO agent) {
        if (OperationTypeEnum.UPDATE.getValue().equals(operation)) {
            if (ObjectUtils.isEmpty(agent.getId())) {
                throw new ParamValidateException("id不能为空!");
            }
        }
        if (ObjectUtils.isEmpty(agent.getAgentName())) {
            throw new ParamValidateException("名称不能为空");
        }
        if (ObjectUtils.isEmpty(agent.getAgentCode())) {
            throw new ParamValidateException("编码不能为空");
        }
        //智能体编码唯一性校验
        SysAgentPO sysAgent = findByCode(agent.getAgentCode());
        if (!ObjectUtils.isEmpty(sysAgent)) {
            if (OperationTypeEnum.ADD.getValue().equals(operation)) {
                throw new ParamValidateException("编码已存在");
            }
            if (OperationTypeEnum.UPDATE.getValue().equals(operation) && !sysAgent.getId().equals(agent.getId())) {
                throw new ParamValidateException("编码已存在");
            }
        }
        if (ObjectUtils.isEmpty(agent.getBaseUrl())) {
            throw new ParamValidateException("API地址不能为空");
        }
        if (ObjectUtils.isEmpty(agent.getApiKey())) {
            throw new ParamValidateException("API密钥不能为空!");
        }
        if (ObjectUtils.isEmpty(agent.getFileUpload())) {
            throw new ParamValidateException("fileUpload不能为空!");
        }
    }
}

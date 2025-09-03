package com.crrc.sys.service.impl;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.entity.BaseEntity;
import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.mapper.DictMapper;
import com.crrc.sys.model.dict.DictExcelVO;
import com.crrc.sys.model.dict.DictListReqVO;
import com.crrc.sys.model.dict.DictPageReqVO;
import com.crrc.sys.service.DeptService;
import com.crrc.sys.service.DictService;
import com.crrc.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典 服务实现类
 * </p>
 *
 * @author lijiangtao
 * @since 2025-01-10
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private DeptService deptService;

    @Autowired
    private SysUserService userService;

    @Override
    public IPage<Dict> page(DictPageReqVO pageReqVO) {
        pageReqVO = (pageReqVO == null) ? new DictPageReqVO() : pageReqVO;
        IPage<Dict> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pageReqVO.getId() != null, Dict::getId, pageReqVO.getId());
        queryWrapper.like(StringUtils.isNotBlank(pageReqVO.getDictName()), Dict::getDictName, pageReqVO.getDictName());
        queryWrapper.eq(StringUtils.isNotBlank(pageReqVO.getDictType()), Dict::getDictType, pageReqVO.getDictType());
        queryWrapper.like(StringUtils.isNotBlank(pageReqVO.getDictCode()), Dict::getDictCode, pageReqVO.getDictCode());
        queryWrapper.like(StringUtils.isNotBlank(pageReqVO.getRemark()), Dict::getRemark, pageReqVO.getRemark());
        queryWrapper.orderByDesc(BaseEntity::getUpdateTime);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<Dict> query(DictListReqVO queryReqVO) {
        queryReqVO = (queryReqVO == null) ? new DictListReqVO() : queryReqVO;
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryReqVO.getId() != null, Dict::getId, queryReqVO.getId());
        queryWrapper.like(StringUtils.isNotBlank(queryReqVO.getDictName()), Dict::getDictName, queryReqVO.getDictName());
        queryWrapper.eq(StringUtils.isNotBlank(queryReqVO.getDictType()), Dict::getDictType, queryReqVO.getDictType());
        queryWrapper.like(StringUtils.isNotBlank(queryReqVO.getDictCode()), Dict::getDictCode, queryReqVO.getDictCode());
        queryWrapper.like(StringUtils.isNotBlank(queryReqVO.getRemark()), Dict::getRemark, queryReqVO.getRemark());
        queryWrapper.orderByDesc(BaseEntity::getUpdateTime);
        return this.list(queryWrapper);
    }

    @Override
    public Dict getByCode(String dictCode) {
        if (StringUtils.isBlank(dictCode)) {
            return null;
        }

        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getDictCode, dictCode);

        List<Dict> dictList = list(queryWrapper);
        try {
            return dictList != null && dictList.size() > 0 ? dictList.get(0) : null;
        } catch (Exception e) {
            log.error("根据字典编码查询字典异常，dictCode: {}", dictCode, e);
            return null;
        }
    }

    @Override
    public List<Dict> getByName(String dictName) {
        if (StringUtils.isBlank(dictName)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getDictName, dictName);

        try {
            return list(queryWrapper);
        } catch (Exception e) {
            log.error("根据字典名称查询字典异常，dictName: {}", dictName, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Dict> getAll() {
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Dict::getDictCode);

        try {
            return list(queryWrapper);
        } catch (Exception e) {
            log.error("查询所有字典异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void exports(DictExcelVO exportReqVO, HttpServletResponse response) {
        // 1. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = null;
        try {
            fileName = URLEncoder.encode("字典数据", "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new BizException(e.getMessage());
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 2. 查询数据
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        if (exportReqVO != null) {
            queryWrapper.like(StringUtils.isNotBlank(exportReqVO.getDictName()), Dict::getDictName, exportReqVO.getDictName());
            queryWrapper.eq(StringUtils.isNotBlank(exportReqVO.getDictType()), Dict::getDictType, exportReqVO.getDictType());
            queryWrapper.like(StringUtils.isNotBlank(exportReqVO.getDictCode()), Dict::getDictCode, exportReqVO.getDictCode());
            queryWrapper.like(StringUtils.isNotBlank(exportReqVO.getRemark()), Dict::getRemark, exportReqVO.getRemark());
            queryWrapper.orderByDesc(BaseEntity::getUpdateTime);
        }
        List<Dict> dictList = this.list(queryWrapper);

        // 3. 转换为ExcelVO
        List<DictExcelVO> excelData = dictList.stream().map(dict -> {
            DictExcelVO vo = new DictExcelVO();
            vo.setDictCode(dict.getDictCode());
            vo.setDictName(dict.getDictName());
            vo.setDictType(dict.getDictType());
            vo.setRemark(dict.getRemark());
            return vo;
        }).collect(Collectors.toList());

        // 4. 使用EasyExcel导出
        try {
            EasyExcel.write(response.getOutputStream(), DictExcelVO.class)
                    .sheet("字典数据")
                    .doWrite(excelData);
        } catch (IOException e) {
            throw new BizException(e.getMessage());
        }
    }

    @Override
    public List<DictExcelVO> reads(MultipartFile file){
        // 1. 创建监听器
        DictExcelListener listener = new DictExcelListener();

        // 2. 读取Excel文件
        try {
            EasyExcel.read(file.getInputStream(), DictExcelVO.class, listener)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            throw new BizException(e.getMessage());
        }

        // 3. 返回读取到的数据
        return listener.getDataList();
    }

    @Override
    public SysUser getUserRealNameInDictExtends(Long userId, String userName) {
        if(userId != null) {
            return userService.findById(userId, null);
        }
        if(com.alibaba.cloud.commons.lang.StringUtils.isNotBlank(userName)) {
            return userService.findByUserName(userName);
        }
        return null;
    }

    @Override
    public Dept getDeptNameNameInDictExtends(Long deptId, String orgCode) {
        if(deptId != null) {
            return deptService.getById(deptId);
        }
        if(com.alibaba.cloud.commons.lang.StringUtils.isNotBlank(orgCode)) {
            List<Dept> depts = deptService.list(new LambdaQueryWrapper<Dept>().eq(Dept::getOrgCode, orgCode));
            return depts != null && depts.size() > 0 ? depts.get(0) : null;
        }
        return null;
    }

    class DictExcelListener extends AnalysisEventListener<DictExcelVO> {
        private final List<DictExcelVO> dataList = new ArrayList<>();

        @Override
        public void invoke(DictExcelVO data, AnalysisContext context) {
            dataList.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 读取完成后的操作
        }

        public List<DictExcelVO> getDataList() {
            return dataList;
        }
    }
}

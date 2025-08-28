package com.yunling.sys.service.impl;

import com.yunling.sys.common.Arith;
import com.yunling.sys.entity.function.column.SysFunctionColumn;
import com.yunling.sys.entity.function.column.SysFunctionColumnTl;
import com.yunling.sys.enums.LanguageEnum;
import com.yunling.sys.mapper.SysFunctionColumnMapper;
import com.yunling.sys.service.SysFunctionColumnService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-10-31
 */
@Service
public class SysFunctionColumnServiceImpl implements SysFunctionColumnService {

    @Resource
    private SysFunctionColumnMapper functionColumnMapper;

    /**
     * 根据功能ID查询列权限信息
     *
     * @param functionIdList 功能ID
     * @param language       语言
     * @return 结果
     */
    @Override
    public List<SysFunctionColumn> findByFunctionIds(List<Long> functionIdList, String language) {
        //每页查询数据条数
        long pageSize = 500;
        //总页数
        long page = 1;
        List<SysFunctionColumn> allBlockList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(functionIdList)) {
            if (functionIdList.size() > pageSize) {
                page = (long) Math.ceil(Arith.div(functionIdList.size(), pageSize, 2));
            }
            for (long i = 0; i < page; i++) {
                //获取当前页的数据集
                List<Long> isPageList = functionIdList.stream().skip(i * pageSize).limit(pageSize).collect(Collectors.toList());
                //根据功能ID查询块信息
                List<SysFunctionColumn> blockList = functionColumnMapper.findByFunctionIds(isPageList, language);
                if (!CollectionUtils.isEmpty(blockList)) {
                    List<Long> blockIdList = blockList.stream().map(SysFunctionColumn::getFcId).collect(Collectors.toList());
                    //根据块ID查询列权限集合
                    List<SysFunctionColumn> columnList = functionColumnMapper.findByParentIds(blockIdList, language);
                    if (!CollectionUtils.isEmpty(columnList)) {
                        blockList.forEach(o -> {
                            for (SysFunctionColumn column : columnList) {
                                if (o.getFcId().equals(column.getParentId())) {
                                    if (CollectionUtils.isEmpty(o.getChildren())) {
                                        List<SysFunctionColumn> children = new ArrayList<>();
                                        children.add(column);
                                        o.setChildren(children);
                                        continue;
                                    }
                                    o.getChildren().add(column);
                                }
                            }
                        });
                    }
                    allBlockList.addAll(blockList);
                }

            }
        }
        return allBlockList;
    }

    /**
     * 列权限新增
     *
     * @param functionColumn 列权限信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int add(SysFunctionColumn functionColumn) {
        //列权限-多语言
        List<SysFunctionColumnTl> functionColumnTlList = new ArrayList<>();
        SysFunctionColumnTl fctl = new SysFunctionColumnTl();
        BeanUtils.copyProperties(functionColumn, fctl);
        fctl.setLanguage(LanguageEnum.ZH_CN.getValue());
        functionColumnTlList.add(fctl);
        fctl = new SysFunctionColumnTl();
        BeanUtils.copyProperties(functionColumn, fctl);
        fctl.setLanguage(LanguageEnum.EN_US.getValue());
        functionColumnTlList.add(fctl);
        functionColumnMapper.insertTl(functionColumnTlList);
        return functionColumnMapper.insert(functionColumn);
    }

    /**
     * 根据块编码查询块信息
     *
     * @param blockCode 块编码
     * @param language  语言
     * @return 结果
     */
    @Override
    public SysFunctionColumn findByBlockCode(String blockCode, String language) {
        return functionColumnMapper.findByBlockCode(blockCode, language);
    }

    /**
     * 根据列编码查询列信息
     *
     * @param columnCode 列编码
     * @param language   语言
     * @param blockId    块ID
     * @return 结果
     */
    @Override
    public SysFunctionColumn findByColumnCode(String columnCode, String language, long blockId) {
        return functionColumnMapper.findByColumnCode(columnCode, language, blockId);
    }

    /**
     * 列权限修改
     *
     * @param functionColumn 列权限信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SysFunctionColumn functionColumn) {
        //列权限-多语言修改
        functionColumnMapper.updateTl(functionColumn);
        //列权限修改
        return functionColumnMapper.update(functionColumn);
    }

    /**
     * 列权限删除
     *
     * @param ids (块/列)权限ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(List<Long> ids) {
        //根据块ID查询列权限
        List<SysFunctionColumn> columnIdList = functionColumnMapper.findByParentIds(ids, LanguageEnum.ZH_CN.getValue());
        if (!CollectionUtils.isEmpty(columnIdList)) {
            ids.addAll(columnIdList.stream().map(SysFunctionColumn::getFcId).collect(Collectors.toList()));
        }
        //列权限-多语言删除
        functionColumnMapper.deleteTlByIds(ids);
        //列权限删除
        return functionColumnMapper.deleteByIds(ids);
    }

    /**
     * 根据列ID查询列信息(批量)
     *
     * @param columnIdList 列ID集合
     * @param language     语言
     * @return 结果
     */
    @Override
    public List<SysFunctionColumn> findByColumnIds(List<Long> columnIdList, String language) {
        return functionColumnMapper.findByColumnIds(columnIdList, language);
    }
}

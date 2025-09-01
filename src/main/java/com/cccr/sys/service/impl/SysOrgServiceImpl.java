package com.cccr.sys.service.impl;

import com.cccr.sys.common.PaginationGrid;
import com.cccr.sys.enums.LanguageEnum;
import com.cccr.sys.service.SysOrgService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.cccr.sys.entity.org.SysOrg;
import com.cccr.sys.entity.org.SysOrgFindByPageParam;
import com.cccr.sys.entity.org.SysOrgTl;
import com.cccr.sys.entity.role.SysRoleOrg;
import com.cccr.sys.mapper.SysOrgMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author 谭永强
 * @date 2022-10-15
 */
@Service
public class SysOrgServiceImpl implements SysOrgService {

    @Resource
    private SysOrgMapper orgMapper;

    /**
     * 组织信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    @Override
    public PaginationGrid<SysOrg> findByPage(SysOrgFindByPageParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<SysOrg> rows = orgMapper.findByFuzzy(param);
        PageInfo<SysOrg> pageInfo = new PageInfo<>(rows);
        return PaginationGrid.success(pageInfo);
    }

    /**
     * 组织新增
     *
     * @param org 组织信息
     * @return 结果
     */
    @Override
    public int add(SysOrg org) {
        List<SysOrgTl> orgTlList = new ArrayList<>();
        SysOrgTl orgTl = new SysOrgTl();
        BeanUtils.copyProperties(org, orgTl);
        orgTl.setLanguage(LanguageEnum.ZH_CN.getValue());
        orgTlList.add(orgTl);
        orgTl = new SysOrgTl();
        BeanUtils.copyProperties(org, orgTl);
        orgTl.setLanguage(LanguageEnum.EN_US.getValue());
        orgTlList.add(orgTl);
        //1.组织多语言新增
        orgMapper.insertTl(orgTlList);
        //2.组织新增
        return orgMapper.insert(org);
    }

    /**
     * 组织修改
     *
     * @param org 组织信息
     * @return 结果
     */
    @Override
    public int update(SysOrg org) {
        orgMapper.updateTl(org);
        return orgMapper.update(org);
    }

    /**
     * 根据角色ID查询组织列表
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 结果
     */
    @Override
    public List<SysOrg> findByRoleId(long roleId, String language) {
        return orgMapper.findByRoleId(roleId, language);
    }

    /**
     * 根据组织编码查询组织
     *
     * @param orgCode  组织编码
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysOrg findByCode(String orgCode, String language) {
        return orgMapper.findByCode(orgCode, language);
    }

    /**
     * 根据组织ID查询组织
     *
     * @param orgId    组织ID
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysOrg findById(long orgId, String language) {
        return orgMapper.findById(orgId, language);
    }

    /**
     * 根据组织ID集合查询组织
     *
     * @param orgIds   组织ID集合
     * @param language 语言
     * @return 结果
     */
    @Override
    public List<SysOrg> findByIds(List<Long> orgIds, String language) {
        return ObjectUtils.isEmpty(orgIds) ? Collections.emptyList() : orgMapper.findByIds(orgIds, language);
    }

    /**
     * modify by liuqiang
     * 新增组织（erp同步）
     *
     * @param org 组织实体
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertOrg(SysOrg org) {
        if (Objects.isNull(org.getCreateBy())) {
            org.setCreateBy(-1L);
        }
        List<SysOrgTl> orgTlList = new ArrayList<>();
        SysOrgTl orgTl = new SysOrgTl();
        BeanUtils.copyProperties(org, orgTl);
        orgTl.setLanguage(LanguageEnum.ZH_CN.getValue());
        orgTlList.add(orgTl);
        orgTl = new SysOrgTl();
        BeanUtils.copyProperties(org, orgTl);
        orgTl.setLanguage(LanguageEnum.EN_US.getValue());
        orgTlList.add(orgTl);
        //1.组织多语言新增
        orgMapper.insertTl(orgTlList);
        //2.组织新增
        return orgMapper.insertOrg(org);

    }

    /**
     * modify by liuqiang
     * 修改组织（erp使用）
     *
     * @param org 组织实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrg(SysOrg org) {
        orgMapper.updateTl(org);
        orgMapper.updateOrg(org);
    }

    /**
     * 根据组织编码列表查询组织
     *
     * @param orgCodeList 组织编码列表
     * @param language    语言
     * @return 结果
     */
    @Override
    public List<SysOrg> findByCodeList(List<String> orgCodeList, String language) {
        if (CollectionUtils.isEmpty(orgCodeList)) {
            return Collections.emptyList();
        }
        return orgMapper.findByCodeList(orgCodeList, language);
    }

    /**
     * 根据角色ID集合查询组织ID
     *
     * @param roleIdList 角色ID集合
     * @return 结果
     */
    @Override
    public List<SysRoleOrg> findByRoleIds(List<Long> roleIdList) {
        return orgMapper.findByRoleIds(roleIdList);
    }
}

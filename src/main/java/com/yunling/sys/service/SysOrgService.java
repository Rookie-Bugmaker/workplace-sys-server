package com.yunling.sys.service;

import com.yunling.sys.common.PaginationGrid;
import com.yunling.sys.entity.org.SysOrg;
import com.yunling.sys.entity.org.SysOrgFindByPageParam;
import com.yunling.sys.entity.role.SysRoleOrg;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-10-15
 */
public interface SysOrgService {

    /**
     * 组织信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    PaginationGrid<SysOrg> findByPage(SysOrgFindByPageParam param);

    /**
     * 组织新增
     *
     * @param org 组织信息
     * @return 结果
     */
    int add(SysOrg org);

    /**
     * 组织修改
     *
     * @param org 组织信息
     * @return 结果
     */
    int update(SysOrg org);

    /**
     * 根据角色ID查询组织列表
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 结果
     */
    List<SysOrg> findByRoleId(long roleId, String language);

    /**
     * 根据组织编码查询组织
     *
     * @param orgCode  组织编码
     * @param language 语言
     * @return 结果
     */
    SysOrg findByCode(String orgCode, String language);

    /**
     * 根据组织ID查询组织
     *
     * @param orgId    组织ID
     * @param language 语言
     * @return 结果
     */
    SysOrg findById(long orgId, String language);

    /**
     * 根据组织ID集合查询组织
     *
     * @param orgIds   组织ID集合
     * @param language 语言
     * @return 结果
     */
    List<SysOrg> findByIds(List<Long> orgIds, String language);

    /**
     * modify by liuqiang
     * 新增组织（erp同步）
     *
     * @param org 组织实体
     * @return 结果
     */
    Long insertOrg(SysOrg org);


    /**
     * modify by liuqiang
     * 修改组织（erp使用）
     *
     * @param org 组织实体
     */
    void updateOrg(SysOrg org);

    /**
     * 根据组织编码列表查询组织
     *
     * @param orgCodeList 组织编码列表
     * @param language    语言
     * @return 结果
     */
    List<SysOrg> findByCodeList(List<String> orgCodeList, String language);

    /**
     * 根据角色ID集合查询组织ID
     *
     * @param roleIdList 角色ID集合
     * @return 结果
     */
    List<SysRoleOrg> findByRoleIds(List<Long> roleIdList);
}

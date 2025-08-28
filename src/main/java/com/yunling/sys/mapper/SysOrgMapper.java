package com.yunling.sys.mapper;

import com.yunling.sys.entity.org.SysOrg;
import com.yunling.sys.entity.org.SysOrgFindByPageParam;
import com.yunling.sys.entity.org.SysOrgTl;
import com.yunling.sys.entity.role.SysRoleOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-10-15
 */
public interface SysOrgMapper {

    /**
     * 组织列表查询
     *
     * @param param 参数
     * @return 结果
     */
    List<SysOrg> findByFuzzy(SysOrgFindByPageParam param);

    /**
     * 组织多语言新增
     * @param orgTlList 组织-多语言
     */
    void insertTl(@Param("orgTlList") List<SysOrgTl> orgTlList);

    /**
     * 组织新增
     * @param org 组织
     * @return 结果
     */
    int insert(SysOrg org);

    /**
     * 组织-多语言修改
     * @param org 组织
     */
    void updateTl(SysOrg org);

    /**
     * 组织修改
     * @param org 组织
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
    List<SysOrg> findByRoleId(@Param("roleId") long roleId, @Param("language") String language);

	/**
	 * 根据组织编码查询组织
	 *
	 * @param orgCode  组织编码
	 * @param language 语言
	 * @return 结果
	 */
    SysOrg findByCode(@Param("orgCode") String orgCode, @Param("language") String language);

	/**
	 * 根据组织ID查询组织
	 *
	 * @param orgId  组织ID
	 * @param language 语言
	 * @return 结果
	 */
	SysOrg findById(@Param("orgId") long orgId, @Param("language") String language);

	/**
	 * 根据组织ID集合查询组织
	 *
	 * @param orgIdList 组织ID集合
	 * @param language  语言
	 * @return 结果
	 */
	List<SysOrg> findByIds(@Param("orgIdList") List<Long> orgIdList, @Param("language") String language);

	/**
	 * modify by liuqiang
	 * 组织新增（erp使用）
	 * @param org 组织
	 * @return ID
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
	 * @param orgCodeList  组织编码列表
	 * @param language     语言
	 * @return 结果
	 */
	List<SysOrg> findByCodeList(@Param("orgCodeList") List<String> orgCodeList, @Param("language") String language);

	/**
	 * 根据角色ID集合查询组织ID
	 *
	 * @param roleIdList 角色ID集合
	 * @return 结果
	 */
	List<SysRoleOrg> findByRoleIds(@Param("roleIdList") List<Long> roleIdList);
}

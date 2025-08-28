package com.yunling.sys.service;

import com.yunling.sys.common.PaginationGrid;
import com.yunling.sys.entity.api.SysApiAuth;
import com.yunling.sys.entity.api.SysApiAuthPageDTO;

import java.util.List;

/**
 * @author 谭永强
 * @date 2024-01-03
 */
public interface SysApiAuthService {

    /**
     * 分页查询
     *
     * @param param 参数
     * @return 分页结果
     */
    PaginationGrid<SysApiAuth> findByPage(SysApiAuthPageDTO param);

    /**
     * 权限(白/黑)名单新增
     *
     * @param apiAuth 参数
     * @return 结果
     */
    int add(SysApiAuth apiAuth);

    /**
     * 根据权限类型查询
     *
     * @param authType 权限类型(传null则查询全部类型)
     * @return 结果
     */
    List<SysApiAuth> findByAuthType(String authType);

    /**
     * 权限(白/黑)名单新增
     *
     * @param apiAuth 参数
     * @return 结果
     */
    int update(SysApiAuth apiAuth);

    /**
     * 删除
     *
     * @param ids 主键
     * @return 结果
     */
    int delete(List<Long> ids);

    /**
     * 根据名单ID查询名单信息
     *
     * @param authId 主键
     * @return 结果
     */
    SysApiAuth findById(long authId);

    /**
     * 根据权限ID列表查询(批量)
     *
     * @param authIdList 主键集合
     * @return 结果
     */
    List<SysApiAuth> findByAuthIdList(List<Long> authIdList);

    /**
     * 批量新增
     *
     * @param apiAuthList 参数
     * @return 结果
     */
    int insertList(List<SysApiAuth> apiAuthList);
}

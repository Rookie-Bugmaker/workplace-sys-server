package com.yunling.sys.mapper;

import com.yunling.sys.entity.api.SysApiAuth;
import com.yunling.sys.entity.api.SysApiAuthPageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2024-01-03
 */
public interface SysApiAuthMapper {

    /**
     * API权限模糊查询
     *
     * @param param 查询条件
     * @return 结果
     */
    List<SysApiAuth> findByFuzzy(SysApiAuthPageDTO param);

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
    List<SysApiAuth> findByAuthType(@Param("authType") String authType);

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
    int delete(@Param("ids") List<Long> ids);

    /**
     * 根据名单ID查询名单信息
     *
     * @param authId 主键
     * @return 结果
     */
    SysApiAuth findById(@Param("authId") long authId);

    /**
     * 根据权限ID列表查询(批量)
     *
     * @param authIdList 主键集合
     * @return 结果
     */
    List<SysApiAuth> findByAuthIdList(@Param("authIdList") List<Long> authIdList);
}

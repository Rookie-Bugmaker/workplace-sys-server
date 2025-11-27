package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.api.SysApiAuth;
import com.crrc.sys.entity.api.SysApiAuthPageDTO;
import com.crrc.sys.entity.profile.SysProfile;
import com.crrc.sys.mapper.mapperImpl.SysApiAuthMapperImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.crrc.sys.service.SysApiAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 谭永强
 * @date 2024-01-03
 */
@Service
public class SysApiAuthServiceImpl implements SysApiAuthService {

    @Autowired
    private SysApiAuthMapperImpl apiAuthMapper;


    /**
     * 分页查询
     *
     * @param param 参数
     * @return 分页结果
     */
    @Override
    public PaginationGrid<SysApiAuth> findByPage(SysApiAuthPageDTO param) {
//        PageHelper.startPage(param.getPage(), param.getPageSize());
        IPage<SysApiAuth> midPage = apiAuthMapper.findByFuzzyPage(param);
        PaginationGrid<SysApiAuth> page = new PaginationGrid<>(
                Math.toIntExact(midPage.getCurrent()), Math.toIntExact(midPage.getSize()));
        page.setTotal(midPage.getTotal());
        page.setRows(midPage.getRecords());
        return page;
//        PageInfo<SysApiAuth> pageInfo = new PageInfo<>(rows);
//        return PaginationGrid.success(pageInfo);
    }

    /**
     * 权限(白/黑)名单新增
     *
     * @param apiAuth 参数
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int add(SysApiAuth apiAuth) {
        return apiAuthMapper.add(apiAuth);
    }

    /**
     * 根据权限类型查询
     *
     * @param authType 权限类型(传null则查询全部类型)
     * @return 结果
     */
    @Override
    public List<SysApiAuth> findByAuthType(String authType) {
        return apiAuthMapper.findByAuthType(authType);
    }

    /**
     * 权限(白/黑)名单新增
     *
     * @param apiAuth 参数
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SysApiAuth apiAuth) {
        return apiAuthMapper.update(apiAuth);
    }

    /**
     * 删除
     *
     * @param ids 主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int delete(List<Long> ids) {
        return apiAuthMapper.delete(ids);
    }

    /**
     * 根据名单ID查询名单信息
     *
     * @param authId 主键
     * @return 结果
     */
    @Override
    public SysApiAuth findById(long authId) {
        return apiAuthMapper.findById(authId);
    }

    /**
     * 根据权限ID列表查询(批量)
     *
     * @param authIdList 主键集合
     * @return 结果
     */
    @Override
    public List<SysApiAuth> findByAuthIdList(List<Long> authIdList) {
        return apiAuthMapper.findByAuthIdList(authIdList);
    }

    /**
     * 批量新增
     *
     * @param apiAuthList 参数
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertList(List<SysApiAuth> apiAuthList) {
        int result = 0;
        for (SysApiAuth sysApiAuth : apiAuthList) {
            result += apiAuthMapper.add(sysApiAuth);
        }
        return result;
    }
}

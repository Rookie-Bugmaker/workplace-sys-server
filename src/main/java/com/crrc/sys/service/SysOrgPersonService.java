package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;

import java.util.Date;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-12-09
 */
public interface SysOrgPersonService {

    /**
     * 人员分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    PaginationGrid<SysOrgPerson> findByPage(SysOrgPersonFindByPageParam param);

    /**
     * 新增
     *
     * @param person 人员信息
     * @return 结果
     */
    int add(SysOrgPerson person);

    /**
     * 人员修改
     *
     * @param person 人员信息
     * @return 结果
     */
    int update(SysOrgPerson person);

    /**
     * 根据人员ID查询人员信息
     *
     * @param personId 人员ID
     * @param language 语言
     * @return 结果
     */
    SysOrgPerson findById(long personId, String language);

    /**
     * 根据人员ID查询人员信息(批量)
     *
     * @param personIdList 人员ID集合
     * @return 结果
     */
    List<SysOrgPerson> findByIds(List<Long> personIdList);

    /**
     * 人员删除
     *
     * @param personIdList 人员ID集合
     * @return 结果
     */
    int delete(List<Long> personIdList);

    /**
     * 根据工号查询人员信息
     *
     * @param jobNo 工号
     * @return 人员实体
     */
    SysOrgPerson findByJobNo(String jobNo);

    /**
     * 根据工号列表查询人员信息
     *
     * @param jobNos 工号列表
     * @return 人员实体
     */
    List<SysOrgPerson> findByJobNos(List<String> jobNos);

    /**
     * 人员列表查询
     *
     * @param param 查询条件
     * @return 结果
     */
    List<SysOrgPerson> findList(SysOrgPersonFindByPageParam param);

    /**
     * 人员新增(列表)
     *
     * @param persons 人员信息
     * @return 结果
     */
    boolean addList(List<SysOrgPerson> persons);

    /**
     * 查询[离职时间<=当前时间]的人员
     *
     * @param nowDate 当前时间
     * @return 人员集合
     */
    List<SysOrgPerson> findByQuitDate(Date nowDate);

    /**
     * 修改人员状态
     *
     * @param personIdList 人员ID
     * @param status       状态
     */
    void updateStatus(List<Long> personIdList, int status);

    /**
     * 判断手机号是否存在
     *
     * @param phoneList 手机号集合
     * @return 已存在的手机号
     */
    List<SysOrgPerson> findByPhoneList(List<String> phoneList);

    Page<SysOrgPerson> findPersonPageWithoutCreatedUser(Long personId, String jobNo, String personType, Integer page, Integer pageSize);
}

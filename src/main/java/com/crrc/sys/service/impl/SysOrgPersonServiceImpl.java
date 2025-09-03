package com.crrc.sys.service.impl;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.enums.LanguageEnum;
import com.crrc.sys.service.SysOrgPersonService;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import com.crrc.sys.entity.org.person.SysOrgPersonFindByPageParam;
import com.crrc.sys.entity.org.person.SysOrgPersonTl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.crrc.sys.mapper.SysOrgPersonMapper;
import com.crrc.sys.mapper.SysOrgPersonTlMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 谭永强
 * @date 2022-12-09
 */
@Service
public class SysOrgPersonServiceImpl implements SysOrgPersonService {

    @Resource
    private SysOrgPersonMapper personMapper;
    @Resource
    private SysOrgPersonTlMapper personTlMapper;

    /**
     * 人员分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    @Override
    public PaginationGrid<SysOrgPerson> findByPage(SysOrgPersonFindByPageParam param) {
        if (!ObjectUtils.isEmpty(param.getFullName()) && !ObjectUtils.isEmpty(param.getJobNo()) && ObjectUtils.isEmpty(param.getMontage())) {
            param.setMontage("and");
        }
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<SysOrgPerson> rows = personMapper.findByFuzzy(param);
        PageInfo<SysOrgPerson> pageInfo = new PageInfo<>(rows);
        return PaginationGrid.success(pageInfo);
    }

    /**
     * 新增
     *
     * @param person 人员信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int add(SysOrgPerson person) {
        List<SysOrgPersonTl> personTlList = new ArrayList<>(2);
        SysOrgPersonTl personTl = new SysOrgPersonTl();
        BeanUtils.copyProperties(person, personTl);
        personTl.setLanguage(LanguageEnum.EN_US.getValue());
        personTlList.add(personTl);
        personTl = new SysOrgPersonTl();
        BeanUtils.copyProperties(person, personTl);
        personTl.setLanguage(LanguageEnum.ZH_CN.getValue());
        personTlList.add(personTl);
        //人员-国际化新增
        personTlMapper.insertList(personTlList);
        //人员新增
        return personMapper.insert(person);
    }

    /**
     * 人员修改
     *
     * @param person 人员信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int update(SysOrgPerson person) {
        //人员-国际化修改
        personTlMapper.update(person);
        //人员修改
        return personMapper.update(person);
    }

    /**
     * 根据人员ID查询人员信息
     *
     * @param personId 人员ID
     * @param language 语言
     * @return 结果
     */
    @Override
    public SysOrgPerson findById(long personId, String language) {
        if (ObjectUtils.isEmpty(language)) {
            language = "zh-CN";
        }
        return personMapper.findById(personId, language);
    }

    /**
     * 根据人员ID查询人员信息(批量)
     *
     * @param personIdList 人员ID集合
     * @return 结果
     */
    @Override
    public List<SysOrgPerson> findByIds(List<Long> personIdList) {
        if (CollectionUtils.isEmpty(personIdList)) {
            return null;
        }
        return personMapper.findByIds(personIdList, "zh-CN");
    }

    /**
     * 人员删除
     *
     * @param personIdList 人员ID集合
     * @return 结果
     */
    @Override
    public int delete(List<Long> personIdList) {
        personTlMapper.delete(personIdList);
        return personMapper.delete(personIdList);
    }

    /**
     * 根据工号查询人员信息
     *
     * @param jobNo 工号
     * @return 人员实体
     */
    @Override
    public SysOrgPerson findByJobNo(String jobNo) {
        return personMapper.findByJobNo(jobNo);
    }

    /**
     * 根据工号列表查询人员信息
     *
     * @param jobNos 工号列表
     * @return 人员实体 列表
     */
    @Override
    public List<SysOrgPerson> findByJobNos(List<String> jobNos) {
        return personMapper.findByJobNos(jobNos);
    }

    /**
     * 人员列表查询
     *
     * @param param 查询条件
     * @return 结果
     */
    @Override
    public List<SysOrgPerson> findList(SysOrgPersonFindByPageParam param) {
        return personMapper.findByFuzzy(param);
    }

    /**
     * 人员新增(批量)
     *
     * @param persons 人员信息集合
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addList(List<SysOrgPerson> persons) {
        // 构建国际化数据
        List<SysOrgPersonTl> personTlList = persons.stream().map(o -> {
            List<SysOrgPersonTl> tlList = new ArrayList<>(2);
            SysOrgPersonTl personTl = new SysOrgPersonTl();
            BeanUtils.copyProperties(o, personTl);
            personTl.setLanguage(LanguageEnum.EN_US.getValue());
            tlList.add(personTl);
            personTl = new SysOrgPersonTl();
            BeanUtils.copyProperties(o, personTl);
            personTl.setLanguage(LanguageEnum.ZH_CN.getValue());
            tlList.add(personTl);
            return tlList;
        }).flatMap(List::stream).collect(Collectors.toList());
        //人员-国际化新增
        personTlMapper.insertList(personTlList);
        //人员新增
        return personMapper.insertBatch(persons);
    }

    /**
     * 查询[离职时间<=当前时间]的人员
     *
     * @param nowDate 当前时间
     * @return 人员集合
     */
    @Override
    public List<SysOrgPerson> findByQuitDate(Date nowDate) {
        return personMapper.findByQuitDate(nowDate);
    }

    /**
     * 修改人员状态
     *
     * @param personIdList 人员ID
     * @param status       状态
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(List<Long> personIdList, int status) {
        int count = personIdList.size();
        int pageSize = 100;
        if (count <= pageSize) {
            personMapper.updateStatus(personIdList, status);
            return;
        }
        int pageCount = (int) Math.ceil(count / (double) pageSize);
        for (int page = 0; page < pageCount; page++) {
            int minIndex = (pageSize * page);
            int maxIndex = (pageSize * (page + 1));
            if (maxIndex >= count) {
                maxIndex = count;
            }
            List<Long> pageList = personIdList.subList(minIndex, maxIndex);
            personMapper.updateStatus(pageList, status);
        }
    }

    /**
     * 判断手机号是否存在
     *
     * @param phoneList 手机号集合
     * @return 已存在的手机号
     */
    @Override
    public List<SysOrgPerson> findByPhoneList(List<String> phoneList) {
        return personMapper.findByPhoneList(phoneList);
    }
}

package com.crrc.sys.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.admindivision.AdminDivisionListVo;
import com.crrc.sys.entity.admindivision.AdminDivisionQueryVo;
import com.crrc.sys.entity.admindivision.AdminDivisionTreeQueryVo;
import com.crrc.sys.entity.admindivision.AdminDivisionTreeVo;
import com.crrc.sys.mapper.SysMdBaseAdminDivisionMapper;
import com.crrc.sys.service.SysMdBaseAdminDivisionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysMdBaseAdminDivisionServiceImpl implements SysMdBaseAdminDivisionService {

    @Resource
    private SysMdBaseAdminDivisionMapper adminDivisionMapper;

    @Override
    public List<AdminDivisionTreeVo> tree(AdminDivisionTreeQueryVo vo) {
        if (vo == null) {
            vo = new AdminDivisionTreeQueryVo();
        }

        List<AdminDivisionTreeVo> countryList = adminDivisionMapper.selectCountryTreeList(vo);
        List<AdminDivisionTreeVo> divisionList = adminDivisionMapper.selectDivisionTreeList();

        if (countryList == null || countryList.isEmpty()) {
            return new ArrayList<>();
        }

        if (divisionList == null) {
            divisionList = new ArrayList<>();
        }

        Map<String, List<AdminDivisionTreeVo>> countryDivisionMap = divisionList.stream()
                .filter(item -> StringUtils.isNotBlank(item.getCountryNumber()))
                .collect(Collectors.groupingBy(AdminDivisionTreeVo::getCountryNumber));

        for (AdminDivisionTreeVo country : countryList) {
            List<AdminDivisionTreeVo> currentCountryDivisions =
                    countryDivisionMap.get(country.getCountryNumber());

            if (currentCountryDivisions == null || currentCountryDivisions.isEmpty()) {
                country.setChildren(new ArrayList<>());
            } else {
                country.setChildren(buildDivisionTree(null, currentCountryDivisions));
            }
        }

        return countryList;
    }

    private List<AdminDivisionTreeVo> buildDivisionTree(String parentNumber,
                                                        List<AdminDivisionTreeVo> list) {
        return list.stream()
                .filter(item -> {
                    if (parentNumber == null) {
                        return StringUtils.isBlank(item.getParentNumber());
                    }
                    return parentNumber.equals(item.getParentNumber());
                })
                .map(item -> {
                    item.setChildren(buildDivisionTree(item.getNumber(), list));
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PaginationGrid<AdminDivisionListVo> page(AdminDivisionQueryVo vo) {
        if (vo == null) {
            vo = new AdminDivisionQueryVo();
        }

        Integer pageNum = vo.getPageNum() == null ? 1 : vo.getPageNum();
        Integer pageSize = vo.getPageSize() == null ? 10 : vo.getPageSize();

        Page<AdminDivisionListVo> page = new Page<>(pageNum, pageSize);

        List<String> divisionNumbers = null;

        if (StringUtils.isNotBlank(vo.getTreeDivisionNumber())) {
            divisionNumbers = new ArrayList<>();
            divisionNumbers.add(vo.getTreeDivisionNumber());
            getAllChildDivisionNumbers(vo.getTreeDivisionNumber(), divisionNumbers);
        }

        Page<AdminDivisionListVo> resultPage =
                adminDivisionMapper.pageAdminDivision(page, vo, divisionNumbers);

        return PaginationGrid.success(resultPage);
    }

    private void getAllChildDivisionNumbers(String parentNumber, List<String> result) {
        List<String> children = adminDivisionMapper.selectChildDivisionNumbers(parentNumber);

        if (children == null || children.isEmpty()) {
            return;
        }

        for (String child : children) {
            result.add(child);
            getAllChildDivisionNumbers(child, result);
        }
    }

    @Override
    public AdminDivisionListVo detail(String divisionNumber) {
        return adminDivisionMapper.detail(divisionNumber);
    }
}
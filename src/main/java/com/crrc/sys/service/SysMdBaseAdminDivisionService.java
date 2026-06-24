package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.admindivision.AdminDivisionListVo;
import com.crrc.sys.entity.admindivision.AdminDivisionQueryVo;
import com.crrc.sys.entity.admindivision.AdminDivisionTreeQueryVo;
import com.crrc.sys.entity.admindivision.AdminDivisionTreeVo;

import java.util.List;

public interface SysMdBaseAdminDivisionService {

    List<AdminDivisionTreeVo> tree(AdminDivisionTreeQueryVo vo);

    PaginationGrid<AdminDivisionListVo> page(AdminDivisionQueryVo vo);

    AdminDivisionListVo detail(String divisionNumber);
}
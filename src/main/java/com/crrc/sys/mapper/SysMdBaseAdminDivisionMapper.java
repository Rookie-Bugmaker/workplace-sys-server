package com.crrc.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crrc.sys.entity.admindivision.AdminDivisionListVo;
import com.crrc.sys.entity.admindivision.AdminDivisionQueryVo;
import com.crrc.sys.entity.admindivision.AdminDivisionTreeQueryVo;
import com.crrc.sys.entity.admindivision.AdminDivisionTreeVo;
import com.crrc.sys.entity.admindivision.SysMdBaseAdminDivision;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMdBaseAdminDivisionMapper extends BaseMapper<SysMdBaseAdminDivision> {

    List<AdminDivisionTreeVo> selectCountryTreeList(@Param("vo") AdminDivisionTreeQueryVo vo);

    List<AdminDivisionTreeVo> selectDivisionTreeList();

    List<String> selectChildDivisionNumbers(@Param("parentNumber") String parentNumber);

    Page<AdminDivisionListVo> pageAdminDivision(Page<?> page,
                                                @Param("vo") AdminDivisionQueryVo vo,
                                                @Param("divisionNumbers") List<String> divisionNumbers);

    AdminDivisionListVo detail(@Param("divisionNumber") String divisionNumber);
}
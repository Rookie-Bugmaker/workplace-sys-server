package com.crrc.sys.convert;

import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.model.dept.DeptInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author 谭永强
 * @date 2025-09-08
 */
@Mapper
public abstract class DeptConvert {

    public static final DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    /**
     * Dept转DeptInfoDTO
     *
     * @param dept Dept
     * @return DeptInfoDTO
     */
    public abstract DeptInfoDTO deptToInfoDto(Dept dept);

    public abstract List<DeptInfoDTO> deptToInfoDto(List<Dept> deptList);
}

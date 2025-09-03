package com.crrc.sys.convert;

/**
 * @author 谭永强
 * @date 2024-01-30
 */

import com.crrc.sys.model.dept.person.SysDeptPersonDTO;
import com.crrc.sys.model.dept.person.SysDeptPersonPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class SysDeptPersonConvert {

    /**
     * 获取转换器实例
     */
    public static final SysDeptPersonConvert INSTANCE = Mappers.getMapper(SysDeptPersonConvert.class);

    /**
     * SysDeptPersonPO 转 SysDeptPersonDTO
     *
     * @param po SysDeptPersonPO
     * @return SysDeptPersonDTO
     */
    public abstract SysDeptPersonDTO poToDto(SysDeptPersonPO po);

    /**
     * SysDeptPersonPO 转 SysDeptPersonDTO(批量)
     *
     * @param poList SysDeptPersonPO集合
     * @return List<SysDeptPersonDTO>
     */
    public abstract List<SysDeptPersonDTO> poListToDtoList(List<SysDeptPersonPO> poList);

}

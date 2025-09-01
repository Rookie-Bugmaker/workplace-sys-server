package com.cccr.sys.convert;

import com.cccr.sys.model.entity.SysEntityPO;
import com.cccr.sys.model.entity.SysEntityVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author 谭永强
 * @date 2024-11-14
 */
@Mapper
public abstract class SysEntityConvert {

    /**
     * 获取转换器实例
     */
    public static SysEntityConvert INSTANCE = Mappers.getMapper(SysEntityConvert.class);

    /**
     * SysEntityPO转换为SysEntityVO
     *
     * @param entityPO 实体对象
     * @return SysEntityVO
     */
    public abstract SysEntityVO poToVo(SysEntityPO entityPO);
}

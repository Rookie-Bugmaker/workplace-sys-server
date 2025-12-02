package com.crrc.sys.convert;

import com.crrc.sys.model.sso.SysSsoConfigPO;
import com.crrc.sys.model.sso.SysSsoConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author 谭永强
 * @date 2025-12-02
 */
@Mapper
public abstract class SysSsoConfigConvert {

    /**
     * 获取转换器实例
     */
    public static final SysSsoConfigConvert INSTANCE = Mappers.getMapper(SysSsoConfigConvert.class);

    /**
     * PO 转 VO
     *
     * @param po SysSsoConfigPO
     * @return SysSsoConfigVO
     */
    public abstract SysSsoConfigVO poToVo(SysSsoConfigPO po);

    /**
     * PO 转 VO
     *
     * @param poList SysSsoConfigPO
     * @return SysSsoConfigVO
     */
    public abstract List<SysSsoConfigVO> poToVoList(List<SysSsoConfigPO> poList);
}

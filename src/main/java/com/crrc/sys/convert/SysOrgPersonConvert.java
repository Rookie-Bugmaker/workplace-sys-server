package com.crrc.sys.convert;

import com.crrc.sys.model.org.person.SysOrgPersonBO;
import com.crrc.sys.entity.org.person.SysOrgPerson;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author 谭永强
 * @date 2024-01-30
 */
@Mapper
public abstract class SysOrgPersonConvert {

    public static final SysOrgPersonConvert INSTANCE = Mappers.getMapper(SysOrgPersonConvert.class);

    /**
     * SysOrgPerson -> SysOrgPersonBO
     *
     * @param orgPerson SysOrgPerson
     * @return SysOrgPersonBO
     */
    public abstract SysOrgPersonBO orgPersonToOrgPersonBO(SysOrgPerson orgPerson);
}

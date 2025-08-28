package com.yunling.sys.convert;

/**
 * @author 谭永强
 * @date 2024-01-30
 */

import com.yunling.sys.entity.user.SysUser;
import com.yunling.sys.model.user.SysUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class SysUserConvert {

    /**
     * 获取转换器实例
     */
    public static final SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    /**
     * SysUser 转 SysUserVO
     *
     * @param user SysUser
     * @return SysUserVO
     */
    public abstract SysUserVO userToUserVO(SysUser user);

}

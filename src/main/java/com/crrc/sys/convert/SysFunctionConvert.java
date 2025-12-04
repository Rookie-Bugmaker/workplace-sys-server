package com.crrc.sys.convert;

import com.crrc.sys.entity.function.SysFunction;
import com.crrc.sys.entity.function.SysFunctionBDBEntity;
import com.crrc.sys.entity.function.SysFunctionBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author 谭永强
 * @date 2024-01-11
 */
@Mapper
public abstract class SysFunctionConvert {

    /**
     * 获取转换器实例
     */
    public static SysFunctionConvert INSTANCE = Mappers.getMapper(SysFunctionConvert.class);

    /**
     * SysFunction转换为SysFunctionBO
     *
     * @param function 功能实体
     * @return SysFunctionBO
     */
    public abstract SysFunctionBO functionToBo(SysFunction function);

    /**
     * SysFunctionBO转换为SysFunction
     *
     * @param functionBo 功能对象
     * @return SysFunction
     */
    public abstract SysFunction boToFunction(SysFunctionBO functionBo);

    /**
     * SysFunctionBDBEntity转换为SysFunction
     *
     * @param entity 功能实体
     * @return SysFunction
     */
    public abstract SysFunction functionToBo(SysFunctionBDBEntity entity);

    /**
     * SysFunctionBDBEntity转换为SysFunction
     *
     * @param entityList 功能实体
     * @return SysFunction
     */
    public abstract List<SysFunction> functionToBoList(List<SysFunctionBDBEntity> entityList);
}

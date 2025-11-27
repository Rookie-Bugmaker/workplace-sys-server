package com.crrc.sys.mapper.mapperImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crrc.sys.common.BeanUtils;
import com.crrc.sys.entity.api.SysApiDBEntity;
import com.crrc.sys.entity.function.SysFunctionApi;
import com.crrc.sys.entity.function.SysFunctionApiBO;
import com.crrc.sys.entity.function.SysFunctionApiDBEntity;
import com.crrc.sys.entity.function.SysFunctionApiPO;
import com.crrc.sys.entity.function.column.SysFunctionColumn;
import com.crrc.sys.entity.function.column.SysFunctionColumnBDBEntity;
import com.crrc.sys.entity.function.column.SysFunctionColumnTl;
import com.crrc.sys.entity.function.column.SysFunctionColumnTlDBEntity;
import com.crrc.sys.entity.role.SysRoleFunctionColumnDBEntity;
import com.crrc.sys.mapper.SysFunctionColumnMapper;
import com.crrc.sys.mapper.mapperService.SysFunctionApiMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionColumnBMapperService;
import com.crrc.sys.mapper.mapperService.SysFunctionColumnTlMapperService;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 列权限表-mapper实现类，通过MyBatisPlus方案重写SQL
 * @author 李江涛
 * @date 2025-11-17
 */
@Repository
public class SysFunctionColumnMapperImpl extends ServiceImpl<SysFunctionColumnTlMapperService, SysFunctionColumnTlDBEntity> {

    @Resource
    private SysFunctionColumnBMapperService sysFunctionColumnBMapperService;

    @Resource
    private SysFunctionColumnTlMapperService sysFunctionColumnTlMapperService;


    public List<SysFunctionColumn> findByParentIds(List<Long> blockIdList, String language) {
        MPJLambdaWrapper<SysFunctionColumnBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionColumnBDBEntity.class)
                .selectAll(SysFunctionColumnBDBEntity.class)
                .select(SysFunctionColumnTlDBEntity::getFcName)
                .select(SysFunctionColumnTlDBEntity::getFcDesc)
                .leftJoin(SysFunctionColumnTlDBEntity.class,
                        fn -> fn.eq(SysFunctionColumnTlDBEntity::getFcId, SysFunctionColumnBDBEntity::getFcId)
                                .eq(SysFunctionColumnTlDBEntity::getLanguage, language)
                )
                .in(SysFunctionColumnBDBEntity::getParentId, blockIdList)
                .orderByAsc(SysFunctionColumnBDBEntity::getFcId);
        return sysFunctionColumnBMapperService.selectJoinList(SysFunctionColumn.class, wrapper);
    }


    public void insertTl(List<SysFunctionColumnTl> functionColumnTlList) {
        List<SysFunctionColumnTlDBEntity> sysFunctionColumnTlDBEntities = convertToDB(functionColumnTlList);
        super.saveBatch(sysFunctionColumnTlDBEntities);
    }


    public int insert(SysFunctionColumn functionColumn) {
        SysFunctionColumnBDBEntity sysFunctionColumnBDBEntity = convertToDB(functionColumn);
        return sysFunctionColumnBMapperService.insert(sysFunctionColumnBDBEntity);
    }


    public SysFunctionColumn findByBlockCode(String blockCode, String language) {
        MPJLambdaWrapper<SysFunctionColumnBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionColumnBDBEntity.class)
                .selectAll(SysFunctionColumnBDBEntity.class)
                .select(SysFunctionColumnTlDBEntity::getFcName)
                .select(SysFunctionColumnTlDBEntity::getFcDesc)
                .leftJoin(SysFunctionColumnTlDBEntity.class,
                        fn -> fn.eq(SysFunctionColumnTlDBEntity::getFcId, SysFunctionColumnBDBEntity::getFcId)
                                .eq(SysFunctionColumnTlDBEntity::getLanguage, language)
                )
                .isNull(SysFunctionColumnBDBEntity::getParentId)
                .eq(SysFunctionColumnBDBEntity::getFcCode, blockCode);
        return sysFunctionColumnBMapperService.selectJoinOne(SysFunctionColumn.class, wrapper);
    }


    public SysFunctionColumn findByColumnCode(String columnCode, String language, long blockId) {
        MPJLambdaWrapper<SysFunctionColumnBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionColumnBDBEntity.class)
                .selectAll(SysFunctionColumnBDBEntity.class)
                .select(SysFunctionColumnTlDBEntity::getFcName)
                .select(SysFunctionColumnTlDBEntity::getFcDesc)
                .leftJoin(SysFunctionColumnTlDBEntity.class,
                        fn -> fn.eq(SysFunctionColumnTlDBEntity::getFcId, SysFunctionColumnBDBEntity::getFcId)
                                .eq(SysFunctionColumnTlDBEntity::getLanguage, language)
                )
                .eq(SysFunctionColumnBDBEntity::getParentId, blockId)
                .eq(SysFunctionColumnBDBEntity::getFcCode, columnCode);
        return sysFunctionColumnBMapperService.selectJoinOne(SysFunctionColumn.class, wrapper);
    }


    public void updateTl(SysFunctionColumn functionColumn) {
        LambdaUpdateWrapper<SysFunctionColumnTlDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysFunctionColumnTlDBEntity::getFcName, functionColumn.getFcName())
                .set(SysFunctionColumnTlDBEntity::getFcDesc, functionColumn.getFcDesc())
                .set(SysFunctionColumnTlDBEntity::getLastUpdateBy, functionColumn.getLastUpdateBy())
                .set(SysFunctionColumnTlDBEntity::getLastUpdateDate, functionColumn.getLastUpdateDate())
                .set(SysFunctionColumnTlDBEntity::getLastUpdateLogin, functionColumn.getLastUpdateLogin())
                .eq(SysFunctionColumnTlDBEntity::getFcId, functionColumn.getFcId())
                .eq(SysFunctionColumnTlDBEntity::getLanguage, functionColumn.getLanguage());
        sysFunctionColumnTlMapperService.update(wrapper);
    }


    public int update(SysFunctionColumn functionColumn) {
        LambdaUpdateWrapper<SysFunctionColumnBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysFunctionColumnBDBEntity::getFcCode, functionColumn.getFcCode())
                .set(SysFunctionColumnBDBEntity::getFcDisplayFlag, functionColumn.getFcDisplayFlag())
                .set(SysFunctionColumnBDBEntity::getFcEditableFlag, functionColumn.getFcEditableFlag())
                .set(SysFunctionColumnBDBEntity::getVsId, functionColumn.getVsId())
                .set(SysFunctionColumnBDBEntity::getFcType, functionColumn.getFcType())
                .set(SysFunctionColumnBDBEntity::getLastUpdateBy, functionColumn.getLastUpdateBy())
                .set(SysFunctionColumnBDBEntity::getLastUpdateDate, functionColumn.getLastUpdateDate())
                .set(SysFunctionColumnBDBEntity::getLastUpdateLogin, functionColumn.getLastUpdateLogin())
                .eq(SysFunctionColumnBDBEntity::getFcId, functionColumn.getFcId());
        return sysFunctionColumnBMapperService.update(wrapper);
    }


    public void deleteTlByIds(List<Long> ids) {
        LambdaUpdateWrapper<SysFunctionColumnTlDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionColumnTlDBEntity::getFcId, ids);
        sysFunctionColumnTlMapperService.delete(wrapper);
    }


    public int deleteByIds(List<Long> ids) {
        LambdaUpdateWrapper<SysFunctionColumnBDBEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysFunctionColumnBDBEntity::getFcId, ids);
        return sysFunctionColumnBMapperService.delete(wrapper);
    }


    public List<SysFunctionColumn> findByFunctionIds(List<Long> functionIdList, String language) {
        MPJLambdaWrapper<SysFunctionColumnBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionColumnBDBEntity.class)
                .selectAll(SysFunctionColumnBDBEntity.class)
                .select(SysFunctionColumnTlDBEntity::getFcName)
                .select(SysFunctionColumnTlDBEntity::getFcDesc)
                .leftJoin(SysFunctionColumnTlDBEntity.class,
                        fn -> fn.eq(SysFunctionColumnTlDBEntity::getFcId, SysFunctionColumnBDBEntity::getFcId)
                                .eq(SysFunctionColumnTlDBEntity::getLanguage, language)
                )
                .in(SysFunctionColumnBDBEntity::getFunctionId, functionIdList)
                .orderByAsc(SysFunctionColumnBDBEntity::getFcId);
        return sysFunctionColumnBMapperService.selectJoinList(SysFunctionColumn.class, wrapper);
    }


    public List<SysFunctionColumn> getHaveNoColumnCode(long roleId, String blockCode) {
        MPJLambdaWrapper<SysFunctionColumnBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionColumnBDBEntity.class)
                .select(SysFunctionColumnBDBEntity::getFcId)
                .select(SysFunctionColumnBDBEntity::getFcCode)
                .innerJoin(SysFunctionColumnBDBEntity.class, SysFunctionColumnBDBEntity::getFcId, SysFunctionColumnBDBEntity::getParentId)
                .leftJoin(SysRoleFunctionColumnDBEntity.class,
                        fn -> fn.eq(SysRoleFunctionColumnDBEntity::getFcId, SysFunctionColumnBDBEntity::getFcId)
                                .eq(SysRoleFunctionColumnDBEntity::getRoleId, roleId))
                .eq(SysFunctionColumnBDBEntity::getFcCode, blockCode)
                .isNull(SysRoleFunctionColumnDBEntity::getRoleId);
        return sysFunctionColumnBMapperService.selectJoinList(SysFunctionColumn.class, wrapper);
    }


    public List<SysFunctionColumn> findByColumnIds(List<Long> columnIdList, String language) {
        MPJLambdaWrapper<SysFunctionColumnBDBEntity> wrapper = JoinWrappers.lambda(SysFunctionColumnBDBEntity.class)
                .selectAll(SysFunctionColumnBDBEntity.class)
                .select(SysFunctionColumnTlDBEntity::getFcName)
                .select(SysFunctionColumnTlDBEntity::getFcDesc)
                .leftJoin(SysFunctionColumnTlDBEntity.class,
                        fn -> fn.eq(SysFunctionColumnTlDBEntity::getFcId, SysFunctionColumnBDBEntity::getFcId)
                                .eq(SysFunctionColumnTlDBEntity::getLanguage, language)
                )
                .in(SysFunctionColumnBDBEntity::getFcId, columnIdList);
        return sysFunctionColumnBMapperService.selectJoinList(SysFunctionColumn.class, wrapper);
    }

    private List<SysFunctionColumnTlDBEntity> convertToDB(List<SysFunctionColumnTl> dataList) {
        List<SysFunctionColumnTlDBEntity> entityList = BeanUtils.toBean(dataList, SysFunctionColumnTlDBEntity.class);
        return entityList;
    }

    private SysFunctionColumnBDBEntity convertToDB(SysFunctionColumn data) {
        SysFunctionColumnBDBEntity entity = BeanUtils.toBean(data, SysFunctionColumnBDBEntity.class);
        return entity;
    }
}
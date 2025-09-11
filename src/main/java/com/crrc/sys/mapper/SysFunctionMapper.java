package com.crrc.sys.mapper;

import com.crrc.sys.entity.function.SysFunction;
import com.crrc.sys.entity.function.SysFunctionFindByPageParam;
import com.crrc.sys.entity.function.SysFunctionTl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-15
 */
public interface SysFunctionMapper {

    /**
     * 功能查询
     *
     * @param param 查询条件
     * @return 结果
     */
    List<SysFunction> findByFuzzy(SysFunctionFindByPageParam param);

    /**
     * 功能新增
     *
     * @param function 功能信息
     * @return 结果
     */
    int insert(SysFunction function);

    /**
     * 根据功能ID查询子功能集合
     *
     * @param parentIdList 父级ID集合,为null则查询所有
     * @param language     语言
     * @return 子功能集合
     */
    List<SysFunction> findByParentIds(@Param("parentIdList") List<Long> parentIdList, @Param("language") String language);

    /**
     * 修改功能语言信息
     *
     * @param functionTl 功能语言
     * @return 结果
     */
    int updateTl(SysFunctionTl functionTl);

    /**
     * 功能修改
     *
     * @param function 功能信息
     * @return 结果
     */
    int update(SysFunction function);

    /**
     * 功能删除
     *
     * @param functionIds 功能ID集合
     * @return 结果
     */
    int delete(@Param("functionIds") List<Long> functionIds);

    /**
     * 根据功能ID查询功能信息
     *
     * @param functionId 功能ID
     * @param language   语言
     * @return 功能信息
     */
    SysFunction findById(@Param("functionId") long functionId, @Param("language") String language);

    /**
     * 根据功能编码查询功能信息
     *
     * @param functionCode 功能编码
     * @param language     语言
     * @return 结果
     */
    SysFunction findByCode(@Param("functionCode") String functionCode, @Param("language") String language);

    /**
     * 根据功能ID查询功能信息(批量)
     *
     * @param functionIdList 功能ID集合
     * @param language       语言
     * @return 结果
     */
    List<SysFunction> findByIds(@Param("functionIdList") List<Long> functionIdList, @Param("language") String language);

    /**
     * 查询所有功能数据
     *
     * @param language 语言
     * @return 结果
     */
    List<SysFunction> findAll(@Param("language") String language);

    /**
     * 查询角色拥有的功能
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 结果
     */
    List<SysFunction> findByRoleId(@Param("roleId") long roleId, @Param("language") String language);

    /**
     * 根据页面ID查询功能信息
     *
     * @param pageId   页面ID
     * @param language 语言
     * @return 结果
     */
    SysFunction findByPageId(@Param("pageId") long pageId, @Param("language") String language);

    /**
     * 根据功能ID查询按钮权限列表
     *
     * @param functionId 功能ID
     * @return 按钮权限集合
     */
    List<SysFunction> findButtonById(@Param("functionId") long functionId);

    /**
     * 根据路由名称查询功能信息
     *
     * @param routeName 路由名称
     * @return 结果
     */
    SysFunction findByRouteName(@Param("routeName") String routeName);

    /**
     * 查询功能总条数
     *
     * @return 总条数
     */
    int findCount();

    /**
     * 分页查询菜单列表
     *
     * @param language 语言
     * @param offset   起始条数
     * @param endset   终止条数
     * @return 结果
     */
    List<SysFunction> findByPage(@Param("language") String language, @Param("offset") int offset, @Param("endset") int endset);

    /**
     * 查询角色拥有的功能集合总条数
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int findCountByRoleId(@Param("roleId") long roleId);

    /**
     * 查询角色拥有的功能集合(分页查询)
     *
     * @param roleId   角色ID
     * @param language 语言
     * @param offset   起始条数
     * @param endset   终止条数
     * @return 结果
     */
    List<SysFunction> findPageByRoleId(@Param("roleId") long roleId, @Param("language") String language, @Param("offset") int offset, @Param("endset") int endset);

    /**
     * 查询所有子功能条数
     *
     * @param language 语言
     * @return 结果
     */
    int findNotParent(String language);

    /**
     * 分页查询子功能
     *
     * @param language 语言
     * @param offset   起
     * @param endset   止
     * @return 结果
     */
    List<SysFunction> findPageByNotParent(@Param("language") String language, @Param("offset") Integer offset, @Param("endset") Integer endset);

    /**
     * 查询所有使用工作流的功能
     *
     * @return 结果
     */
    List<SysFunction> findByWorkflow();

    /**
     * 修改子功能的项目ID
     *
     * @param functionId 功能ID
     * @param projectId  项目ID
     */
    void updateProjectId(@Param("functionId") long functionId, @Param("projectId") Long projectId);
}

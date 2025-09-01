package com.cccr.sys.mapper;

import com.cccr.sys.entity.menu.SysMenu;
import com.cccr.sys.entity.menu.SysMenuFindByPageParam;
import com.cccr.sys.entity.menu.SysMenuPO;
import com.cccr.sys.entity.menu.SysMenuTl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-21
 */
public interface SysMenuMapper {

    /**
     * 菜单查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    List<SysMenu> findByFuzzy(SysMenuFindByPageParam param);

    /**
     * 菜单(多语言)新增
     *
     * @param menuTlList 菜单(多语言)
     */
    int insertTl(@Param("menuTlList") List<SysMenuTl> menuTlList);

    /**
     * 菜单新增
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insert(SysMenuPO menu);

    /**
     * 菜单新增(批量)
     *
     * @param menuList 菜单信息集合
     * @return 结果
     */
    int insertList(@Param("menuList") List<SysMenu> menuList);

    /**
     * 查询所有子菜单
     *
     * @param language 语言
     * @return 结果
     */
    List<SysMenu> findBySubmenu(@Param("language") String language);

    /**
     * 菜单(多语言)修改
     *
     * @param menu 菜单信息
     */
    int updateTl(SysMenuTl menu);

    /**
     * 菜单修改
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int update(SysMenuPO menu);

    /**
     * 根据菜单ID查询菜单信息(批量)
     *
     * @param menuIds  菜单ID集合
     * @param language 语言
     * @return 结果
     */
    List<SysMenu> findByIds(@Param("menuIds") List<Long> menuIds, @Param("language") String language);

    /**
     * 根据菜单ID查询菜单信息
     *
     * @param menuId   菜单ID集合
     * @param language 语言
     * @return 结果
     */
    SysMenu findById(@Param("menuId") long menuId, @Param("language") String language);

    /**
     * 菜单(多语言)删除
     *
     * @param menuIdList 菜单ID集合
     */
    void deleteTl(@Param("menuIdList") List<Long> menuIdList);

    /**
     * 菜单删除
     *
     * @param menuIdList 菜单ID集合
     * @return 结果
     */
    int delete(@Param("menuIdList") List<Long> menuIdList);

    /**
     * 根据父菜单ID查询子菜单(parentId=null则查询根菜单)
     *
     * @param parentId 父菜单ID
     * @param language 语言
     * @return 结果
     */
    List<SysMenu> findByParentId(@Param("parentId") Long parentId, @Param("language") String language);

    /**
     * 查询所有菜单
     *
     * @param language 语言
     * @return 结果
     */
    List<SysMenu> findAll(@Param("language") String language);

    /**
     * 根据菜单编码查询菜单信息
     *
     * @param codeList 菜单编码集合
     * @param language 语言
     * @return 结果
     */
    List<SysMenu> findByCodes(@Param("codeList") List<String> codeList, @Param("language") String language);

    /**
     * 查询角色拥有的菜单
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 结果
     */
    List<SysMenu> findByRoleId(@Param("roleId") long roleId, @Param("language") String language);

    /**
     * 根据菜单ID查询绑定的功能ID
     *
     * @param menuIds 菜单ID
     * @return 结果
     */
    List<Long> findBindFunctionId(@Param("menuIds") List<Long> menuIds);

    /**
     * 根据菜单ID查询绑定的子菜单ID
     *
     * @param menuIds 菜单ID
     * @return 结果
     */
    List<Long> findBindChildId(@Param("menuIds") List<Long> menuIds);

    /**
     * 根据父菜单ID查询子菜单ID集合(批量)
     *
     * @param parentIdList 上级菜单ID集合
     * @return 下级菜单ID集合
     */
    List<Long> findByParentIds(@Param("parentIdList") List<Long> parentIdList);

    /**
     * 查询所有菜单的总数
     *
     * @return 总数
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
    List<SysMenu> findByPage(@Param("language") String language, @Param("offset") int offset, @Param("endset") int endset);

    /**
     * 查询角色拥有的菜单集合总条数
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int findCountByRoleId(@Param("roleId") long roleId);

    /**
     * 查询角色拥有的菜单(根据起止编号查询)
     *
     * @param roleId   角色ID
     * @param language 语言
     * @param offset   起始条数
     * @param endset   终止条数
     * @return 结果
     */
    List<SysMenu> findPageByRoleId(@Param("roleId") long roleId, @Param("language") String language, @Param("offset") int offset, @Param("endset") int endset);
}

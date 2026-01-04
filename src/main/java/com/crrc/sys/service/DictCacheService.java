package com.crrc.sys.service;


import com.crrc.sys.entity.dept.Dept;
import com.crrc.sys.entity.dict.Dict;
import com.crrc.sys.entity.dict.DictItem;
import com.crrc.sys.entity.user.SysUser;
import com.crrc.sys.model.dict.DictItemExcelVO;
import com.crrc.sys.model.dict.DictItemVO;
import com.crrc.sys.model.dict.DictVO;

import java.util.List;

public interface DictCacheService {
    /**
     * 检查字典是否需要更新（值有变化时返回true）
     */
    boolean needUpdateDict(Dict dict);

    /**
     * 检查字典项是否需要更新（值有变化时返回true）
     */
    boolean needUpdateItem(DictItem item, Dict dict);
    // 字典缓存操作
    void setDictCache(Dict dict);
    void deleteDictCache(Dict dict);

    // 字典项缓存操作
    void setDictItemCache(DictItem dictItem, Dict dict);
    void deleteDictItemCache(DictItem dictItem, Dict dict);
    void loadDictItemCache(Dict dict, List<DictItem> items);

    // 批量操作
    void batchSetDictItemCache(List<DictItemExcelVO> items);
    void clearDictCache(String dictCode);

    // 全局查询相关方法
    DictVO getDictFromCache(String dictCode);
    String getDictNameFromCache(String dictCode);
    List<String> getDictCodesFromCache(String dictName);
    List<DictVO> getAllDictsFromCache();

    DictItemVO getDictItemFromCache(String dictCode, String itemCode);
    String getDictItemNameFromCache(String dictCode, String itemCode);
    String getDictItemCodeFromCache(String dictCode, String itemName);

    void cacheDict(DictVO dictVO);
    void cacheDictItem(String dictCode, DictItemVO itemVO);

//    String getUserRealNameInDictExtendsFromCache(Long userId, String userName);

//    String getDeptNameNameInDictExtendsFromCache(Long deptId, String orgCode);

//    void cacheUserRealInDictExtends(SysUser user);

//    void cacheDeptNameInDictExtends(Dept dept);
}
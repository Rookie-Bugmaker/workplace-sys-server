package com.crrc.sys.cache;

public class CacheKeyGenerator {
    // 字典索引缓存前缀
    private static final String DICT_INDEX_PREFIX = "sys:dict:index";
    // 字典数据缓存前缀
    private static final String DICT_DATA_PREFIX = "sys:dict:data:";
    // 字典项缓存前缀
    private static final String DICT_ITEM_PREFIX = ":items:";
    // API缓存前缀
    private static final String SYS_API_PREFIX = "sys:api:list";
    private static final String SYS_API_BY_URI_PREFIX = "sys:api:uri:";

    public static String getDictIndexKey() {
        return DICT_INDEX_PREFIX;
    }

    public static String getDictKey(String dictCode) {
        return DICT_DATA_PREFIX + dictCode;
    }

    public static String getDictItemKey(String dictCode, String itemCode) {
        return DICT_DATA_PREFIX + dictCode + DICT_ITEM_PREFIX + itemCode;
    }

    public static String getLockKey(String key) {
        return key + ":lock";
    }

    public static String getSysApiListKey() {
        return SYS_API_PREFIX;
    }

    public static String getSysApiByUriKey(String apiUri) {
        return SYS_API_BY_URI_PREFIX + apiUri;
    }

//    public static String getUserExtendKey(String userId) {
//        return "sys:dict:extend:user:" + userId;
//    }

//    public static String getDeptExtendKey(String deptId) {
//        return "sys:dict:extend:dept:" + deptId;
//    }
}
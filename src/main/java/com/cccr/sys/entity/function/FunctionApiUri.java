package com.cccr.sys.entity.function;

import java.util.Map;
import java.util.Set;

/**
 * 功能API集合
 *
 * @author 谭永强
 * @date 2024-03-07
 */
public class FunctionApiUri {

    /**
     * 功能ID
     */
    private Long functionId;
    /**
     * uri集合
     * key: 请求方式(get/post/put/delete)
     * value: uri集合
     */
    private Map<String, Set<String>> uriMap;

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Map<String, Set<String>> getUriMap() {
        return uriMap;
    }

    public void setUriMap(Map<String, Set<String>> uriMap) {
        this.uriMap = uriMap;
    }
}

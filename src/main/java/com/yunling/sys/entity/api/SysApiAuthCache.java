package com.yunling.sys.entity.api;

import java.util.List;

/**
 * @author 谭永强
 * @date 2024-01-03
 */
public class SysApiAuthCache {

    /**
     * 白名单
     */
    private List<String> whiteList;
    /**
     * 黑名单
     */
    private List<String> blackList;

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }
}

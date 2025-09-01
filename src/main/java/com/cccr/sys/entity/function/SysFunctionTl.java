package com.cccr.sys.entity.function;

import com.cccr.sys.entity.BaseEntity;

/**
 * 功能-多语言
 *
 * @author 谭永强
 * @date 2022-09-15
 */
public class SysFunctionTl extends BaseEntity {

    /**
     * 功能ID
     */
    private Long functionId;
    /**
     * 功能名称
     */
    private String functionName;
    /**
     * 功能描述
     */
    private String functionDescription;
    /**
     * 语言:LanguageEnum
     * zh-CN:中文、en-US:英文
     */
    private String language;

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionDescription() {
        return functionDescription;
    }

    public void setFunctionDescription(String functionDescription) {
        this.functionDescription = functionDescription;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

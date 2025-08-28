package com.yunling.sys.enums;

/**
 * 1 * @Author: fucheng
 * 2 * @Description:参数类型枚举值
 * 3 * @Date: 2023/8/25
 */
public enum ParameterTypeEnum {

	JSON(1,"application/json"),
	X_WWW_FORM(2,"application/x-www-form-urlencoded");

	private Integer code;

	private String name;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	ParameterTypeEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}
}

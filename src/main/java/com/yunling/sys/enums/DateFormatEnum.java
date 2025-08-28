package com.yunling.sys.enums;

import java.util.Arrays;

/**
 * @ClassName DateFormatEnum
 * @Description: 日期格式枚举类
 * @Author jiangshitao
 * @Date 2022/12/29
 */
public enum DateFormatEnum {

	/**
	 * 格式YY
	 */
	YY("YY", 0),
	/**
	 * 格式YYYY
	 */
	YYYY("YYYY", 1),
	/**
	 * 格式YYMM
	 */
	YYMM("YYMM", 2),
	/**
	 * 格式YY
	 */
	YYYYMM("YYYYMM", 3),
	/**
	 * 格式YYMMDD
	 */
	YYMMDD("YYMMdd", 4),
	/**
	 * 格式YYYYMMDD
	 */
	YYYYMMDD("YYYYMMdd", 5);

	/**
	 * 描述
	 */
	private String text;
	/**
	 * 值
	 */
	private int value;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public static DateFormatEnum valueOf(int value) {
		for (DateFormatEnum format : values()) {
			if (format.getValue() == value) {
				return format;
			}
		}
		throw new RuntimeException("没有找到对应的枚举");
	}


	DateFormatEnum(String text, int value) {
		this.text = text;
		this.value = value;
	}

	/**
	 * 判断是否是枚举值
	 *
	 * @param value 值
	 * @return true/false
	 */
	public static boolean contains(int value) {
		return Arrays.stream(DateFormatEnum.values())
				.anyMatch(o -> o.getValue() == value);
	}

}

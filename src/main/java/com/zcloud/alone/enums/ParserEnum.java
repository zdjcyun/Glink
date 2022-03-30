package com.zcloud.alone.enums;

/**
 * 解析指令结果枚举类
 */
public enum ParserEnum {

	/** 回复指令长度异常 */
	LENGTH(Constant.LENGTH_ERROR, "回复指令长度异常"),
	/** 回复指令CRC16校验异常 */
	CRC16(Constant.CRC16_ERROR, "回复指令CRC16校验异常"),
	/** 数据超量程异常 */
	RANG(Constant.RANGE_ERROR, "数据超量程异常"),
	/** 数据正常 */
	NORMAL(Constant.NORMAL, "数据正常");

	/** 状态码 */
	private String code;

	/** 状态描述 */
	private String value;

	/**
	 * 构造方法
	 */
	ParserEnum(String code, String value) {
		this.code = code;
		this.value = value;
	}

	/**
	 * 普通方法
	 */
	public static ParserEnum getParserEnumByCode(String code) {
		for (ParserEnum parserEnum : ParserEnum.values()) {
			if (parserEnum.getCode().equals(code)) {
				return parserEnum;
			}
		}
		return null;
	}

	/**
	 * 普通方法
	 */
	public static String getValue(String code) {
		ParserEnum parserEnum = getParserEnumByCode(code);
		return parserEnum == null ? null : parserEnum.value;
	}

	/**
	 * 普通方法
	 */
	public static ParserEnum getParserEnumByValue(String value) {
		for (ParserEnum parserEnum : ParserEnum.values()) {
			if (parserEnum.getValue().equals(value)) {
				return parserEnum;
			}
		}
		return null;
	}

	/**
	 * 普通方法
	 */
	public static String getCode(String value) {
		ParserEnum parserEnum = getParserEnumByValue(value);
		return parserEnum == null ? null : parserEnum.code;
	}

	/** get set 方法 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public interface Constant {

		/** 回复指令长度异常 */
		String LENGTH_ERROR = "ERROR01";

		/** 回复指令CRC16校验异常 */
		String CRC16_ERROR = "ERROR02";

		/** 数据超量程异常 */
		String RANGE_ERROR = "ERROR03";

        /** 数据正常 */
		String NORMAL = "SUCCESS";
	}
}

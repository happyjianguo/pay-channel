package com.dream.pay.channel.service.enums;

/**
 * 银行返回错误码
 * 
 * @author mengzhenbin
 *
 */
public enum BankRtnCodeEnum {
	S00000("成功"), P00000("处理中"), F00000("失败"),U00000("未知状态");

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String value;

	BankRtnCodeEnum(String value) {
		this.value = value;
	}
}

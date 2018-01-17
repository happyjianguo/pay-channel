package com.dream.pay.channel.service.enums;

public enum BizLine {

	MAIN(1, "主站"), 
	DEPOSIT(2, "拍卖"),
	EXCHANGE(3,"19e礼品卡兑换"),
	CHARGE(4,"缴费中心"),
	AGREE(5,"签约");
	
	private int value;
	private String desc;

	private BizLine(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	/**
	 * 通过业务线ID获取业务线枚举
	 * 
	 * @param value
	 * @return
	 */
	public static BizLine valueOf(Integer value) {
		if (value == null) {
			return null;
		}
		for (BizLine e : BizLine.values()) {
			if (e.value == value.intValue()) {
				return e;
			}
		}
		return null;
	}

	public static String toDesc(Integer value) {
		BizLine bizLine = valueOf(value);
		return bizLine == null ? null : bizLine.desc;
	}
}

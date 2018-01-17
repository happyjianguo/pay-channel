package com.dream.pay.channel.service.enums;

public enum CardType {
	DEBIT(1, "借记卡"), CREDIT(2, "贷记卡");
	private int value;
	private String desc;

	private CardType(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public static CardType valueOf(Integer value) {
		if (value == null) {
			return null;
		}
		for (CardType e : CardType.values()) {
			if (e.value == value.intValue()) {
				return e;
			}
		}
		return null;
	}

	public static String toDesc(Integer value) {
		CardType cardType = valueOf(value);
		return cardType == null ? null : cardType.desc;
	}
}

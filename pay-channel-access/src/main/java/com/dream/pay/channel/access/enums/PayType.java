package com.dream.pay.channel.access.enums;

import lombok.Getter;

@Getter
public enum PayType {

    ALIPAY(51, "支付宝"), ALIPAYWAP(79, "支付宝WAP"), ALIPAYAPP(11, "支付宝APP"), ALIPAYB2B(98, "支付宝企业级"),

    WECHATSCANN(84, "微信扫码"), WECHATPUB(85, "微信公账"), WECHATAPP(10, "微信APP"), WECHATWAP(15, "微信WAP(H5)"),

    TENPAY(44, "财付通"), TENPAYWAP(77, "财付通WAP"),

    BILLPAY(36, "快钱"), BILLB2BPAY(52, "快钱企业级"),

    UNIONPAY(46, "银联在线"),

    CHINAPAY(55, "银联在线"),

    APPLEPAY(94, "ApplePay"),

    CMBPAY(4, "招商银行"),;

    private int value;
    private String desc;

    private PayType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 通过支付方式ID获取支付方式枚举
     *
     * @param value
     * @return
     */
    public static PayType valueOf(Integer value) {
        if (value == null) {
            return null;
        }
        for (PayType e : PayType.values()) {
            if (e.value == value.intValue()) {
                return e;
            }
        }
        return null;
    }

    public static String toDesc(Integer value) {
        PayType payType = valueOf(value);
        return payType == null ? null : payType.desc;
    }
}

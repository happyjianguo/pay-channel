package com.dream.pay.channel.service.channel.alipay;

import com.dream.pay.utils.PropUtil;

/**
 * 支付宝参数常量类
 *
 * @author mengzhenbin
 */
public class AlipayConfigContants {

    public static final String SUBJECT_VALUE = PropUtil.get("alipay.productName", "普通订单");

    public static final String REFUND_CHARSET = PropUtil.get("alipay.charset");// 退款统一使用的编码

    public static final String PAY_TEMPLATE_PATH = "gateway/alipay/pay.ftl";
    public static final String WAP_PAY_TEMPLATE_PATH = "gateway/alipay/wappay.ftl";

    public static final String DECIMAL_FORMAT = "#0.00";
}

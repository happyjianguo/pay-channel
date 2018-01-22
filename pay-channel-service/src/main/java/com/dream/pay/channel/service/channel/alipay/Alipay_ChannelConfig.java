package com.dream.pay.channel.service.channel.alipay;

import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class Alipay_ChannelConfig extends ChannelConfig {


    private String partner; // 商户号
    private String payApplyService; // 接口名称 固定值create_direct_pay_by_user
    private String wapPayApplyService; // 接口名称 固定值alipay.wap.create.direct.pay.by.user
    private String appPayApplyService; // 接口名称 固定值 mobile.securitypay.pay
    private String notifyService; // 回调验证 - notify_verify
    private String payQueryService; // 查询接口 固定值single_trade_query
    private String refundApplyService; // 退款接口 固定值refund_fastpay_by_platform_nopwd
    private String refundQueryServcie; // 退款查询接口 固定值refund_fastpay_query

    private String postUrl; // PC支付、查询、退款、退款查询路径
    private String wapPostUrl; // WAP支付、查询、退款、退款查询路径

    private String sellerId;
    private String sellerEmail; // 卖家支付宝账号sellerEmail
    private String sellerAccountName;

    private String signType; // 签名方式
    private String signKey; // 签名秘钥
    private String sign; // 签名信息

    private String paymentType; // 支付类型
    private String payMethod; // 支付方式 默认bankPay
    private String bankCode; // 支付宝支付

    private String outTradeNo; // 商户订单号
    private String subject; // 商品名称

    private String payNotifyUrl; // 异步通知地址
    private String payReturnUrl; // 同步通知地址
    private String refundNotifyUrl;// 退款通知地址
    private String showUrl; // 返回按钮跳转页面
}

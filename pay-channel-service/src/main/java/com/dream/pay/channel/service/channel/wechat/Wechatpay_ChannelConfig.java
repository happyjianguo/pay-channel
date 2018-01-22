package com.dream.pay.channel.service.channel.wechat;

import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Wechatpay_ChannelConfig extends ChannelConfig {
	/**
	 * 加密类型
	 */
	private String signType;
	/**
	 * 发送方式
	 */
	private String sendType;
	/**
	 * 微信公众账号ID
	 */
	private String appId;
	/**
	 * 加密秘钥
	 */
	private String signKey;
	/**
	 * 支付申请地址
	 */
	private String payApplyUrl;
	/**
	 * 支付查询地址
	 */
	private String payQueryUrl;
	/**
	 * 支付通知地址
	 */
	private String payNotifyUrl;
	/**
	 * 退款申请地址
	 */
	private String refundApplyUrl;
	/**
	 * 退款查询地址
	 */
	private String refundQueryUrl;
	/**
	 * 退款通知地址
	 */
	private String refundNotifyUrl;

}

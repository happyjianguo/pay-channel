package com.dream.pay.channel.service.core.handler.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ChannelConfig {
	/**
	 * 银行url
	 */
	private String bankURL;

	/**
	 * 私钥路径
	 */
	private String pxfPath;
	/**
	 * 证书路径
	 */
	private String cerPath;
	/**
	 * 发送报文地址
	 */
	private String postUrl;
	/**
	 * 连接超时
	 */
	private String connTimeOut;

	/**
	 * 读超时
	 */
	private String readTimeOut;

	/**
	 * 报文编码
	 */
	private String charset;
	/**
	 * 商户号
	 */
	private String merchantNo;
	/**
	 * 支付方式
	 */
	private String payType;
	/**
	 * 产品名称
	 */
	private String productName;
}

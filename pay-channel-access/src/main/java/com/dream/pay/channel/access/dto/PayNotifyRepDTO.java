package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PayNotifyRepDTO extends BaseRep {
	private static final long serialVersionUID = 1L;
	/**
	 * 交易金额
	 */
	private BigDecimal payAmount;
	/**
	 * 原业务订单号
	 */
	private String bizOrderNo;
	/**
	 * 返回第三方支付单号
	 */
	private String platOrderNo;
	/**
	 * 返回银行支付单号
	 */
	private String bankOrderNo;
	/**
	 * 商户号
	 */
	private String merchantId;
	/**
	 * 用户号
	 */
	private String buyerId;
	/**
	 * 银行编码
	 */
	private String bankCode;
	/**
	 * 返回报文信息
	 */
	private String responseBody;
	/**
	 * 是否需要进行到账通知到业务系统，默认通知
	 */
	private boolean isNoNoticeBusinessSystem;
	/**
	 */
	private Long outPaymetnId;
}

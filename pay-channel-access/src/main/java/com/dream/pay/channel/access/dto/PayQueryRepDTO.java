package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PayQueryRepDTO extends BaseRep {
	private static final long serialVersionUID = 1L;
	/**
	 * 交易金额
	 */
	private BigDecimal payAmount;

	/**
	 * 业务线订单号
	 */
	private String bizOrderNo;

	/**
	 * 第三方订单号
	 */
	private String platOrderNo;

	/**
	 * 银行订单号
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
	 * 	第三方特殊字段
	 */
	private Long outPaymentId;

}

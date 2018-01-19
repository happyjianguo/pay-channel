package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class RefundQueryRepDTO extends BaseRep {
	/**
	 * 退款批次号
	 */
	private String refundBatchNo;

	/**
	 * 退款业务订单号
	 */
	private String refundDetailNo;

	/**
	 * 第三方退款订单号
	 */
	private String BankRefundDetailNo;

	/**
	 * 原业务订单号
	 */
	private String payDetailNo;

	/**
	 * 第三方支付流水号
	 */
	private String bankPayDetailNo;

	/**
	 * 退款金额
	 */
	private BigDecimal refundAmount;

}

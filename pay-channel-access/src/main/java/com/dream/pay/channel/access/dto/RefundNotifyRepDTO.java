package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class RefundNotifyRepDTO extends BaseRep {
	/**
	 * 退款批次号
	 */
	private String refundBatchNo;

	/**
	 * 原业务线支付单号
	 */
	private String payDetailNo;

	/**
	 * 第三方支付流水号
	 */
	private String bankPayDetailNo;

	/**
	 * 业务线退款单号
	 */
	private String refundDetailNo;
	/**
	 * 银行退款单号
	 */
	private String bankRefundDetailNo;

	/**
	 * 退款金额
	 */
	private BigDecimal refundAmount;

	/**
	 * 返回报文信息
	 */
	private String responseBody;
}

package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RefundNotifyRepDTO extends BaseRep {
	private static final long serialVersionUID = 1L;
	/**
	 * 退款批次号
	 */
	private String refundBatchNo;
	/**
	 * 退款订单号
	 */
	private String bizRefundNo;
	/**
	 * 第三方退款订单号
	 */
	private String bankRefundNo;
	/**
	 * 原业务订单号
	 */
	private String bizOrderNo;
	/**
	 * 第三方支付流水号
	 */
	private String bankOrderNo;
	/**
	 * 退款金额
	 */
	private BigDecimal refundAmount;
	/**
	 * 返回报文信息
	 */
	private String responseBody;
}

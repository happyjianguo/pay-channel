package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RefundApplyReqDTO extends BaseReq {

	private static final long serialVersionUID = 1L;
	/**
	 * 退款批次号
	 */
	private String refundBatchNo;
	/**
	 * 退款单号
	 */
	private String bizRefundNo;
	/**
	 * 原业务线订单号
	 */
	private String bizOrderNo;
	/**
	 * 原银行订单号
	 */
	private String bankOrderNo;
	/**
	 * 退款金额
	 */
	private BigDecimal refundAmount;
	/**
	 * 支付金额
	 */
	private BigDecimal payAmount;

	/**
	 * 原支付订单日期
	 */
	private Date bizOrderDate;
}

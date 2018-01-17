package com.dream.pay.channel.access.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RefundQueryReqDTO extends BaseReq {
	private static final long serialVersionUID = 1L;
	/**
	 * 退款批次号
	 */
	private String refundBatchNo;
	/**
	 * 退款业务订单号
	 */
	private String bizRefundNo;
	/**
	 * 支付业务订单号
	 */
	private String bizOrderNo;
	/**
	 * 第三方支付订单号
	 */
	private String bankOrderNo;
	/**
	 * 第三方退款订单号
	 */
	private String bankRefundNo;
	/**
	 * 开始时间
	 */
	private Date startDate;
	/**
	 * 结束时间
	 */
	private Date endDate;
	/**
	 * 页码
	 */
	private String pageNo;
	/**
	 * 订单交易日期
	 */
	private Date bizOrderDate;
}
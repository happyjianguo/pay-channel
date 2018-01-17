package com.dream.pay.channel.access.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PayQueryReqDTO extends BaseReq {
	private static final long serialVersionUID = 1L;

	/**
	 * 业务线订单号
	 */
	private String bizOrderNo;

	/**
	 * 银行订单号
	 */
	private String bankOrderNo;
	/**
	 * 查询开始日期
	 */
	private Date startDate;

	/**
	 * 查询结束日期
	 */
	private Date endDate;

	/**
	 * 订单交易日期
	 */
	private Date bizOrderDate;
}

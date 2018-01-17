package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PayApplyRepDTO extends BaseRep {
	private static final long serialVersionUID = 1L;
	/**
	 * 支付业务单号
	 */
	private String bizOrderNo;
	/**
	 * 银行/第三方的交易流水号
	 */
	private String bankOrderNo;
	/**
	 * 支付业务单号对应支付金额
	 */
	private BigDecimal payAmount;
	/**
	 * 渠道返回的报文
	 */
	private String repContent;
}

package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PayApplyReqDTO extends BaseReq {
	private static final long serialVersionUID = 1L;

	/**
	 * 支付业务单号
	 */
	private String bizOrderNo;
	/**
	 * 支付业务单号对应支付金额
	 */
	private BigDecimal payAmount;
}

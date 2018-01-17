package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefundApplyRepDTO extends BaseRep {
	private static final long serialVersionUID = 1L;
	/**
	 * 退款金额
	 */
	private BigDecimal refundAmount;
	/**
	 * 原业务线支付单号
	 */
	private String bizOrderNo;
	/**
	 * 业务线退款单号
	 */
	private String bizRefundNo;
	/**
	 * 银行退款单号
	 */
	private String bankRefundNo;
	/**
	 * 渠道返回的报文
	 */
	private String repContent;

	@Override
	public String toString() {
		return "BaseRep[" + super.toString() + "],RefundApplyRepDTO [refundAmount=" + refundAmount + ", bizOrderNo="
				+ bizOrderNo + ", bizRefundNo=" + bizRefundNo + ", bankRefundNo=" + bankRefundNo + ", repContent="
				+ repContent + "]";
	}

}

package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RefundQueryRepDTO extends BaseRep {
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
	 * 第三方退款订单号
	 */
	private String BankRefundNo;
	/**
	 * 原业务订单号
	 */
	private String bizOrderNo;
	/**
	 * 第三方支付流水号
	 */
	private String platOrderNo;
	/**
	 * 银行支付流水号
	 */
	private String BankOrderNo;
	/**
	 * 退款金额
	 */
	private BigDecimal refundAmount;

	@Override
	public String toString() {
		return "BaseRep[" + super.toString() + "],RefundQueryRepDTO [refundBatchNo=" + refundBatchNo + ", bizRefundNo="
				+ bizRefundNo + ", BankRefundNo=" + BankRefundNo + ", bizOrderNo=" + bizOrderNo + ", platOrderNo="
				+ platOrderNo + ", BankOrderNo=" + BankOrderNo + ", refundAmount=" + refundAmount + "]";
	}

}

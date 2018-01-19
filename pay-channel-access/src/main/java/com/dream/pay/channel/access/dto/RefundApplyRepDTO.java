package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class RefundApplyRepDTO extends BaseRep {
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 原业务线支付单号
     */
    private String payDetailNo;

    /**
     * 业务线退款单号
     */
    private String refundDetailNo;

    /**
     * 银行退款单号
     */
    private String bankRefundDetailNo;

    /**
     * 渠道返回的报文
     */
    private String repContent;
}

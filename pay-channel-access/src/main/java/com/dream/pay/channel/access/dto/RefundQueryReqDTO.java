package com.dream.pay.channel.access.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString(callSuper = true)
public class RefundQueryReqDTO extends BaseReq {
    /**
     * 退款批次号
     */
    private String refundBatchNo;

    /**
     * 原业务线订单号
     */
    private String payDetailNo;

    /**
     * 退款单号
     */
    @NotNull(message = "退款单号不能为空")
    private String refundDetailNo;

    /**
     * 原银行订单号
     */
    private String bankPayDetailNo;

    /**
     * 第三方退款订单号
     */
    private String bankRefundDetailNo;
}
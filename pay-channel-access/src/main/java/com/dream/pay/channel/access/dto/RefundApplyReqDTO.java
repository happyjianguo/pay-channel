package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString(callSuper = true)
public class RefundApplyReqDTO extends BaseReq {

    /**
     * 退款批次号
     */
    private String refundBatchNo;

    /**
     * 退款单号
     */
    @NotNull(message = "退款单号不能为空")
    private String refundDetailNo;

    /**
     * 原业务线订单号
     */
    private String payDetailNo;

    /**
     * 原银行订单号
     */
    private String bankPayDetailNo;

    /**
     * 退款金额
     */
    @NotNull(message = "退款金额不能为空")
    private BigDecimal refundAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

}

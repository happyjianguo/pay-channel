package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class PayNotifyRepDTO extends BaseRep {
    private static final long serialVersionUID = 1L;
    /**
     * 交易金额
     */
    private BigDecimal payAmount;

    /**
     * 收单支付明细号
     */
    private String payDetailNo;

    /**
     * 银行支付流水号
     */
    private String bankPayDetailNo;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 用户号
     */
    private String userNo;

    /**
     * 返回报文信息
     */
    private String responseBody;
}

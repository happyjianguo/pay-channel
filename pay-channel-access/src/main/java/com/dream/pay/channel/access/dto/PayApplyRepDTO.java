package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class PayApplyRepDTO extends BaseRep {
    /**
     * 支付业务单号
     */
    private String payDetailNo;

    /**
     * 银行/第三方的交易流水号
     */
    private String bankPayDetailNo;

    /**
     * 支付业务单号对应支付金额
     */
    private BigDecimal payAmount;

    /**
     * 渠道返回的报文
     */
    private String repContent;
}

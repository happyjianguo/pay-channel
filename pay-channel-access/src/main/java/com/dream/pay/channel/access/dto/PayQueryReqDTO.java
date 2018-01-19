package com.dream.pay.channel.access.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString(callSuper = true)
public class PayQueryReqDTO extends BaseReq {
    /**
     * 收单支付明细号
     */
    @NotNull(message = "支付单号不能为空")
    private String payDetailNo;

    /**
     * 银行支付流水号,若渠道有流水则存渠道流水号
     */
    private String bankPayDetailNo;

}

package com.dream.pay.channel.access.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString(callSuper = true)
public class PayApplyReqDTO extends BaseReq {

    /**
     * 支付业务单号
     */
    @NotNull(message = "支付单号不能为空")
    private String payDetailNo;

    /**
     * 支付单号对应支付金额
     */
    @NotNull(message = "支付金额不能为空")
    private BigDecimal payAmount;
}

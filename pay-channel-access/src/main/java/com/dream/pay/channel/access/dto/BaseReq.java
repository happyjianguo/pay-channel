package com.dream.pay.channel.access.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.enums.PartnerIdEnum;
import com.dream.pay.enums.PayTool;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class BaseReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 交易类型-支付申请，支付查询，支付通知，退款申请，退款查询，退款通知，提现申请，提现查询
     */
    @NotNull(message = "交易类型不能为空")
    private TradeType tradeType;

    /**
     * 业务线编码
     */
    @NotNull(message = "交易业务线编码不能为空")
    private PartnerIdEnum partnerId;

    /**
     * 商户号 - 商户平台的商户号
     */
    @NotNull(message = "交易商户号不能为空")
    private String merchantNo;

    /**
     * 支付方式
     */
    @NotNull(message = "支付方式不能为空")
    private PayTool payType;

    /**
     * 请求时间
     */
    @NotNull(message = "请求时间不能为空")
    private Date reqDateTime;

    /**
     * 用户IP地址
     */
    private String userIp;

    /**
     * 备注
     */
    private String memo;

    /**
     * 其他字段(扩展)
     */
    private Map<String, String> ext = new HashMap<String, String>();


}

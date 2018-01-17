package com.dream.pay.channel.access.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dream.pay.channel.access.enums.PayType;
import com.dream.pay.channel.access.enums.TradeType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BaseReq implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 支付方式
     */
    private PayType payType;
    /**
     * 交易类型-支付申请，支付查询，支付通知，退款申请，退款查询，退款通知
     */
    private TradeType tradeType;
    /**
     * 业务线编码
     */
    private String bizCode;
    /**
     * 支付来源
     * 0:pc
     * 1:无线
     * 9 当读(星空)
     * 10 当读5.0
     * 11 深圳全民阅读
     * 12 快牙
     */
    private String payFrom;
    /**
     * 请求时间
     */
    private Date reqDateTime;

    /**
     * 用户IP地址
     */
    private String userIp;
    /**
     * 其他字段(扩展)
     */
    private Map<String, String> map = new HashMap<String, String>();

    /**
     * 直连银行指定
     */
    private String bankCode;
}

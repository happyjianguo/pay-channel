package com.dream.pay.channel.access.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.enums.BizChannelEnum;
import com.dream.pay.enums.PayTool;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BaseRep implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否调用成功
     */
    private boolean success;

    /**
     * 渠道返回码(针对银行返回码的再次封装，银行返回码或透传或包装)
     */
    private String chlRtnCode;
    /**
     * 渠道返回描述
     */
    private String chlRtnMsg;
    /**
     * 交易状态(渠道按照银行返回状态封装后返回给上层)
     */
    private TradeStatus tradeStatus;
    /**
     * 银行返回码(银行接口返回的成功标示或者错误码)［暂时由渠道返回码封装］
     */
    private String bankRtnCode;
    /**
     * 渠道响应时间
     */
    private Date chlFinishTime;
    /**
     * 银行返回时间
     */
    private Date bankFinishTime;
    /**
     * 支付方式
     */
    private PayTool payType;

    /**
     * 支付通道
     */
    private BizChannelEnum bizChannel;
    /**
     * 扩展字段
     */
    private Map<String, String> map = new HashMap<String, String>();
}

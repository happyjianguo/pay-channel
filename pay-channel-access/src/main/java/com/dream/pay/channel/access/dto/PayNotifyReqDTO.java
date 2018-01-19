package com.dream.pay.channel.access.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class PayNotifyReqDTO extends BaseReq {

    /**
     * 第三方通知报文
     */
    private String callBackContent;

    /**
     * 是否校验签名
     */
    private boolean checkSign;
}

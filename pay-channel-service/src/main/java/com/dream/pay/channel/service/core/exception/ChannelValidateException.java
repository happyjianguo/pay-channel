package com.dream.pay.channel.service.core.exception;

import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;

public class ChannelValidateException extends BaseException {

    private static final long serialVersionUID = 1L;

    public ChannelValidateException(String errorCode, String errorMsg, TradeStatus tradeStatusEnum) {
        super(errorCode, errorMsg, tradeStatusEnum);
    }

    public ChannelValidateException(ChannelRtnCodeEnum channelRtnCodeEnum, TradeStatus tradeStatusEnum) {
        super(channelRtnCodeEnum, tradeStatusEnum);
    }
}

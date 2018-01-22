package com.dream.pay.channel.service.core.exception;

import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;

public class ChannelMsgException extends BaseException {

    private static final long serialVersionUID = 1L;

    public ChannelMsgException(String errorCode, String errorMsg, TradeStatus tradeStatusEnum) {
        super(errorCode, errorMsg, tradeStatusEnum);
    }

    public ChannelMsgException(ChannelRtnCodeEnum channelRtnCodeEnum, TradeStatus tradeStatusEnum) {
        super(channelRtnCodeEnum, tradeStatusEnum);
    }
}

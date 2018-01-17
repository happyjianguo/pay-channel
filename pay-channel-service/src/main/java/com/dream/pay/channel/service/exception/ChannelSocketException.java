package com.dream.pay.channel.service.exception;


import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;

public class ChannelSocketException extends BaseException {

    private static final long serialVersionUID = 1L;

    public ChannelSocketException(String errorCode, String errorMsg, TradeStatus tradeStatusEnum) {
        super(errorCode, errorMsg, tradeStatusEnum);
    }

    public ChannelSocketException(ChannelRtnCodeEnum channelRtnCodeEnum, TradeStatus tradeStatusEnum) {
        super(channelRtnCodeEnum, tradeStatusEnum);
    }
}

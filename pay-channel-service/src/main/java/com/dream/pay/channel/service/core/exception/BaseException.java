package com.dream.pay.channel.service.core.exception;

import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.enums.BankRtnCodeEnum;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends Exception {

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private String errorMsg;
    private TradeStatus tradeStatusEnum;
    private BankRtnCodeEnum bankRtnCodeEnum;
    private ChannelRtnCodeEnum channelRtnCodeEnum;

    public BaseException() {
        super();
    }

    public BaseException(String errorCode, String errorMsg) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BaseException(String errorCode, String errorMsg, TradeStatus tradeStatusEnum) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.tradeStatusEnum = tradeStatusEnum;
    }

    public BaseException(String errorCode, String errorMsg, TradeStatus tradeStatusEnum,
                         BankRtnCodeEnum bankRtnCodeEnum, ChannelRtnCodeEnum channelRtnCodeEnum) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.tradeStatusEnum = tradeStatusEnum;
        this.bankRtnCodeEnum = bankRtnCodeEnum;
        this.channelRtnCodeEnum = channelRtnCodeEnum;
    }

    public BaseException(ChannelRtnCodeEnum channelRtnCodeEnum, TradeStatus tradeStatusEnum) {
        super();
        this.tradeStatusEnum = tradeStatusEnum;
        this.channelRtnCodeEnum = channelRtnCodeEnum;
    }
}

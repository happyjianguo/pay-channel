package com.dream.pay.channel.service.handler.validate.impl;

import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.exception.ChannelValidateException;
import com.dream.pay.channel.service.handler.validate.ChannelValidateHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 接口公共验证参数 Created by mengzhenbin on 16/06/17
 */
@Service
public class BaseChannelValidateImpl<T extends BaseReq> implements ChannelValidateHandler<T> {

    @Override
    public void validate(T t) throws ChannelValidateException {
        if (null == t) {
            throw new ChannelValidateException(ChannelRtnCodeEnum.V10000, TradeStatus.FAIL);
        }
        if (StringUtils.isEmpty(t.getBizCode())) {
            throw new ChannelValidateException(ChannelRtnCodeEnum.V10001, TradeStatus.FAIL);
        }
        if (null == t.getPayType()) {
            throw new ChannelValidateException(ChannelRtnCodeEnum.V10002, TradeStatus.FAIL);
        }
        if (null == t.getReqDateTime()) {
            throw new ChannelValidateException(ChannelRtnCodeEnum.V10003, TradeStatus.FAIL);
        }

    }
}
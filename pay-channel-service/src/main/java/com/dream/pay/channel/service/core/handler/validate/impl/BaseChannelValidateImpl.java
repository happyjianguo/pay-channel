package com.dream.pay.channel.service.core.handler.validate.impl;

import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.core.exception.ChannelValidateException;
import com.dream.pay.channel.service.core.handler.validate.ChannelValidateHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 接口公共验证参数
 *
 * @author mengzhenbin
 * @since 16/06/17
 */
@Service
@Slf4j
public class BaseChannelValidateImpl<T extends BaseReq> implements ChannelValidateHandler<T> {

    @Override
    public void validate(T t) throws ChannelValidateException {
        if (null == t) {
            log.error("[基础校验组件]-[校验数据为空]");
            throw new ChannelValidateException(ChannelRtnCodeEnum.V10000, TradeStatus.FAIL);
        }
        if (null == t.getPayType()) {
            log.error("[基础校验组件]-[支付方式为空]");
            throw new ChannelValidateException(ChannelRtnCodeEnum.V10002, TradeStatus.FAIL);
        }
        if (null == t.getReqDateTime()) {
            log.error("[基础校验组件]-[请求时间为空]");
            throw new ChannelValidateException(ChannelRtnCodeEnum.V10003, TradeStatus.FAIL);
        }

    }
}

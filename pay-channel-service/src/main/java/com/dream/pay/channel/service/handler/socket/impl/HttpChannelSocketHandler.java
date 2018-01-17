package com.dream.pay.channel.service.handler.socket.impl;

import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.exception.ChannelSocketException;
import com.dream.pay.channel.service.handler.config.ChannelConfig;
import com.dream.pay.channel.service.handler.socket.ChannelSocketHandler;
import com.dream.pay.channel.service.util.HttpRep;
import com.dream.pay.channel.service.util.HttpReq;
import com.dream.pay.channel.service.util.HttpRequester;
import org.springframework.stereotype.Service;

/**
 * http通信类组件 Created by mengzhenbin on 16/06/16
 */
@Service
public class HttpChannelSocketHandler<REQ extends BaseReq> implements ChannelSocketHandler<REQ> {

    @Override
    public byte[] send(BaseReq req, byte[] reqMsg, ChannelConfig channelConfig) throws ChannelSocketException {
        try {
            HttpReq httpReq = new HttpReq();
            httpReq.setUrl(channelConfig.getBankURL());
            httpReq.setCharset(channelConfig.getCharset());
            httpReq.setRequestBody(new String(reqMsg, channelConfig.getCharset()));
            HttpRequester httpRequester = new HttpRequester();
            HttpRep httpRep = httpRequester.sendPostString(httpReq);
            return httpRep.getContent().getBytes(channelConfig.getCharset());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ChannelSocketException(ChannelRtnCodeEnum.S10000.name(), ChannelRtnCodeEnum.S10000.getMessage(),
                    TradeStatus.UNKNOW);
        }
    }
}

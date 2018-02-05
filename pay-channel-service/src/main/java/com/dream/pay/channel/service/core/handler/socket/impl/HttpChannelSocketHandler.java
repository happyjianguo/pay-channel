package com.dream.pay.channel.service.core.handler.socket.impl;

import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.core.exception.ChannelSocketException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.socket.ChannelSocketHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.util.HttpRep;
import com.dream.pay.channel.service.util.HttpReq;
import com.dream.pay.channel.service.util.HttpRequester;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * http通信类组件
 *
 * @author mengzhenbin
 * @since 16/06/16
 */
@Service
@Slf4j
public class HttpChannelSocketHandler<REQ extends BaseReq> implements ChannelSocketHandler<REQ> {

    @Override
    public byte[] send(BaseReq req, byte[] reqMsg, ChannelConfig channelConfig) throws ChannelSocketException {
        try {
            log.info("[HTTP通信组件]-[报文发送]|开始,{}", new String(reqMsg, channelConfig.getCharset()));
            HttpReq httpReq = new HttpReq();
            httpReq.setUrl(channelConfig.getBankURL());
            httpReq.setCharset(channelConfig.getCharset());
            httpReq.setRequestBody(new String(reqMsg, channelConfig.getCharset()));
            HttpRequester httpRequester = new HttpRequester();
            HttpRep httpRep = httpRequester.sendPostString(httpReq);
            log.info("[HTTP通信组件]-[报文发送]|结束,{}", httpRep.getContent());
            return httpRep.getContent().getBytes(channelConfig.getCharset());
        } catch (Exception e) {
            log.error("[HTTP通信组件]-[报文发送]|[出现异常]", e);
            throw new ChannelSocketException(ChannelRtnCodeEnum.S10000, TradeStatus.UNKNOW);
        }
    }
}

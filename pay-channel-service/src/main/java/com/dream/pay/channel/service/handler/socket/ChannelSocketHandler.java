package com.dream.pay.channel.service.handler.socket;

import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.service.exception.ChannelSocketException;
import com.dream.pay.channel.service.handler.config.ChannelConfig;

/**
 * 平台通信组件抽象接口 Created by mengzhenbin on 16/06/16
 */
public interface ChannelSocketHandler<REQ extends BaseReq> {

    /**
     * 通讯
     *
     * @param req    req 请求对象
     * @param reqMsg reqMsg 要发送的报文
     * @return byte[]
     * @throws ChannelSocketException
     */
    public byte[] send(REQ req, byte[] reqMsg, ChannelConfig channelConfig) throws ChannelSocketException;

}

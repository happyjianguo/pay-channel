package com.dream.pay.channel.service.core.handler.msg;

import com.dream.pay.channel.access.dto.BaseRep;
import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;

/**
 * 平台报文拼解组件 Created by mengzhenbin on 16/06/16
 */
public interface ChannelMsgHandler<REQ extends BaseReq, REP extends BaseRep> {
    /**
     * 创建报文之前
     *
     * @param req
     * @param channelConfig
     * @return
     * @throws ChannelMsgException
     */
    public REQ beforBuildMsg(REQ req, ChannelConfig channelConfig) throws ChannelMsgException;

    /**
     * 创建报文
     *
     * @param req
     * @param channelConfig
     * @return
     * @throws ChannelMsgException
     */
    public byte[] builderMsg(REQ req, ChannelConfig channelConfig) throws ChannelMsgException;

    /**
     * 创建报文之后
     *
     * @param req
     * @param reqMsg
     * @param channelConfig
     * @return
     * @throws ChannelMsgException
     */
    public byte[] afterBuildMsg(REQ req, byte[] reqMsg, ChannelConfig channelConfig) throws ChannelMsgException;

    /**
     * 解析报文之前
     *
     * @param req
     * @param repMsg
     * @param channelConfig
     * @return
     * @throws ChannelMsgException
     */
    public byte[] beforResolveMsg(REQ req, byte[] repMsg, ChannelConfig channelConfig) throws ChannelMsgException;

    /**
     * 解析报文
     *
     * @param req
     * @param rtnMsg
     * @param channelConfig
     * @return
     * @throws ChannelMsgException
     */
    public REP resolveMsg(REQ req, byte[] rtnMsg, ChannelConfig channelConfig) throws ChannelMsgException;

    /**
     * 解析报文之后
     *
     * @param req
     * @param rep
     * @param channelConfig
     * @return
     * @throws ChannelMsgException
     */
    public REP afterResolveMsg(REQ req, REP rep, ChannelConfig channelConfig) throws ChannelMsgException;

    /**
     * 解析回调报文（专用）
     *
     * @param req
     * @param channelConfig
     * @return
     * @throws ChannelMsgException
     */
    public REP resolveCallBackMsg(REQ req, ChannelConfig channelConfig) throws ChannelMsgException;
}

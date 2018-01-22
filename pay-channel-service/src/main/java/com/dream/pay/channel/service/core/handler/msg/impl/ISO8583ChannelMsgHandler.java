package com.dream.pay.channel.service.core.handler.msg.impl;

import com.dream.pay.channel.access.dto.BaseRep;
import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.service.core.handler.msg.ChannelMsgHandler;

/**
 * ISO8583拼装报文实现组件
 * 
 * @author mengzhenbin
 *
 * @param <REQ>
 * @param <REP>
 */
public abstract class ISO8583ChannelMsgHandler<REQ extends BaseReq, REP extends BaseRep>
		implements ChannelMsgHandler<REQ, REP> {

}

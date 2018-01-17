package com.dream.pay.channel.service.handler.msg.impl;

import com.dream.pay.channel.access.dto.BaseRep;
import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.exception.ChannelMsgException;
import com.dream.pay.channel.service.handler.config.ChannelConfig;
import com.dream.pay.channel.service.handler.msg.ChannelMsgHandler;
import com.dream.pay.utils.ParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * url直接拼接报文实现组件
 *
 * @param <REQ>
 * @param <REP>
 * @author mengzhenbin
 */
public abstract class UrlChannelMsgHandler<REQ extends BaseReq, REP extends BaseRep>
        implements ChannelMsgHandler<REQ, REP> {
    private static final Logger logger = LoggerFactory.getLogger(UrlChannelMsgHandler.class);

    @Override
    public REQ beforBuildMsg(REQ req, ChannelConfig channelConfig) throws ChannelMsgException {
        Map<String, String> paramMap = new HashMap<String, String>();
        this.paramMap.set(paramMap);
        return req;
    }

    @Override
    public byte[] builderMsg(REQ t, ChannelConfig channelConfig) throws ChannelMsgException {
        logger.info("##################请求报文,无签名｜map＝" + this.getParamMap());
        String reqString = "";
        reqString = ParamUtil.createSortParamString(this.getParamMap());
        byte[] result = null;
        try {
            result = reqString.getBytes(channelConfig.getCharset());
        } catch (Exception e) {
            logger.error("####UrlChannelMsgHandler.builderMsg#出现异常.", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return result;
    }

    @Override
    public byte[] afterBuildMsg(REQ req, byte[] reqMsg, ChannelConfig channelConfig) throws ChannelMsgException {
        return reqMsg;
    }

    @Override
    public byte[] beforResolveMsg(REQ req, byte[] repMsg, ChannelConfig channelConfig) throws ChannelMsgException {
        return repMsg;
    }

    @Override
    public REP resolveMsg(REQ req, byte[] rtnMsg, ChannelConfig channelConfig) throws ChannelMsgException {
        return null;
    }

    @Override
    public REP afterResolveMsg(REQ req, REP rep, ChannelConfig channelConfig) throws ChannelMsgException {
        rep.setChlRepDateTime(new Date());
        this.paramMap.remove();
        return rep;
    }

    @Override
    public REP resolveCallBackMsg(REQ req, ChannelConfig channelConfig) throws ChannelMsgException {
        return null;
    }

    protected ThreadLocal<Map<String, String>> paramMap = new ThreadLocal<Map<String, String>>();

    protected Map<String, String> getParamMap() {
        return this.paramMap.get();
    }

    protected void setParam(String key, String value) {
        if (value == null) {
            value = "";
        }
        Map<String, String> dataMap = paramMap.get();
        dataMap.put(key, value);
    }

    protected Object getParam(String key) {
        if (key == null) {
            return null;
        }
        return paramMap.get().get(key);
    }

}

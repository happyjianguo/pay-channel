package com.dream.pay.channel.service.context;

import com.dream.pay.channel.access.enums.PayType;
import com.dream.pay.channel.service.come.GateWayService;
import com.dream.pay.channel.service.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 渠道
 *
 * @author chenjianchunjs
 */
@Component
public class Channel {

    @Autowired
    ChannelContext channelContext;

    /**
     * 选择渠道
     *
     * @param payType 支付方式
     */
    public void select(PayType payType) {
        ChannelContext.setPayType(payType);
    }

    /**
     * 获取网关渠道服务
     *
     * @return
     * @throws BaseException
     */
    public GateWayService getGateWayService() throws BaseException {
        return channelContext.getGateWayService();
    }
}
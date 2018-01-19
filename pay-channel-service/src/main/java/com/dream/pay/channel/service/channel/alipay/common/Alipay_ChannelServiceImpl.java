package com.dream.pay.channel.service.channel.alipay.common;

import com.dream.pay.channel.access.dto.*;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.access.AbsGateWayServiceImpl;
import com.dream.pay.channel.service.exception.BaseException;
import com.dream.pay.channel.service.process.ChannelActionProcess;

/**
 * @author 孟振滨: mengzhenbin
 * @version 创建时间：2016年6月23日 下午3:38:07
 */
public class Alipay_ChannelServiceImpl extends AbsGateWayServiceImpl {

    @Override
    public PayApplyRepDTO payApply(PayApplyReqDTO payApplyReqDTO) throws BaseException {
        ChannelActionProcess channelActionProcess = new ChannelActionProcess();
        channelActionProcess.setChannelMsgHandler(super.getChannelMsgHandler().get(TradeType.PAY_APPLY));
        channelActionProcess.setChannelValidateHandler(super.getChannelValidateHandler());
        channelActionProcess.setChannelConfig(super.getChannelConfig());
        return (PayApplyRepDTO) channelActionProcess.doProcess(payApplyReqDTO);
    }

    @Override
    public PayQueryRepDTO payQuery(PayQueryReqDTO payQueryReqDTO) throws BaseException {
        ChannelActionProcess channelActionProcess = new ChannelActionProcess();
        channelActionProcess.setChannelMsgHandler(super.getChannelMsgHandler().get(TradeType.PAY_QUERY));
        channelActionProcess.setChannelSocketHandler(super.getChannelSocketHandler());
        channelActionProcess.setChannelValidateHandler(super.getChannelValidateHandler());
        channelActionProcess.setChannelConfig(super.getChannelConfig());
        return (PayQueryRepDTO) channelActionProcess.doProcess(payQueryReqDTO);
    }

    @Override
    public PayNotifyRepDTO payNotify(PayNotifyReqDTO payNotifyReqDTO) throws BaseException {
        ChannelActionProcess channelActionProcess = new ChannelActionProcess();
        channelActionProcess.setChannelMsgHandler(super.getChannelMsgHandler().get(TradeType.PAY_NOTIFY));
        channelActionProcess.setChannelConfig(super.getChannelConfig());
        return (PayNotifyRepDTO) channelActionProcess.doCallbackProcess(payNotifyReqDTO);
    }

    @Override
    public RefundApplyRepDTO refundApply(RefundApplyReqDTO refundApplyReqDTO) throws BaseException {
        ChannelActionProcess channelActionProcess = new ChannelActionProcess();
        channelActionProcess.setChannelMsgHandler(super.getChannelMsgHandler().get(TradeType.REFUND_APPLY));
        channelActionProcess.setChannelSocketHandler(super.getChannelSocketHandler());
        channelActionProcess.setChannelValidateHandler(super.getChannelValidateHandler());
        channelActionProcess.setChannelConfig(super.getChannelConfig());
        return (RefundApplyRepDTO) channelActionProcess.doProcess(refundApplyReqDTO);
    }

    @Override
    public RefundQueryRepDTO refundQuery(RefundQueryReqDTO refundQueryReqDTO) throws BaseException {
        ChannelActionProcess channelActionProcess = new ChannelActionProcess();
        channelActionProcess.setChannelMsgHandler(super.getChannelMsgHandler().get(TradeType.REFUND_QUERY));
        channelActionProcess.setChannelSocketHandler(super.getChannelSocketHandler());
        channelActionProcess.setChannelValidateHandler(super.getChannelValidateHandler());
        channelActionProcess.setChannelConfig(super.getChannelConfig());
        return (RefundQueryRepDTO) channelActionProcess.doProcess(refundQueryReqDTO);
    }

}

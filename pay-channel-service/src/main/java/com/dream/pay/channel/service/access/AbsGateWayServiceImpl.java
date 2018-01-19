package com.dream.pay.channel.service.access;

import com.dream.pay.channel.access.dto.*;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.exception.BaseException;
import com.dream.pay.channel.service.handler.config.ChannelConfig;
import com.dream.pay.channel.service.handler.msg.ChannelMsgHandler;
import com.dream.pay.channel.service.handler.socket.ChannelSocketHandler;
import com.dream.pay.channel.service.handler.validate.ChannelValidateHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Setter
@Getter
public class AbsGateWayServiceImpl implements GateWayService {

    /**
     * 验证组件
     */
    private ChannelValidateHandler channelValidateHandler;

    /**
     * 报文组件
     */
    private Map<TradeType, ChannelMsgHandler> channelMsgHandler;

    /**
     * 通信组件
     */
    private ChannelSocketHandler channelSocketHandler;

    /**
     * 配置组件
     */
    private ChannelConfig channelConfig;


    @Override
    public PayApplyRepDTO payApply(PayApplyReqDTO payApplyReqDTO) throws BaseException {
        return null;
    }

    @Override
    public PayQueryRepDTO payQuery(PayQueryReqDTO payQueryReqDTO) throws BaseException {
        return null;
    }

    @Override
    public PayNotifyRepDTO payNotify(PayNotifyReqDTO payNotifyReqDTO) throws BaseException {
        return null;
    }

    @Override
    public RefundApplyRepDTO refundApply(RefundApplyReqDTO refundApplyReqDTO) throws BaseException {
        return null;
    }

    @Override
    public RefundQueryRepDTO refundQuery(RefundQueryReqDTO refundQueryReqDTO) throws BaseException {

        return null;
    }

    @Override
    public WithdrawApplyRepDTO withdrawApply(WithdrawApplyReqDTO withdrawApplyReqDTO) throws BaseException {
        return null;
    }

    @Override
    public WithdrawQueryRepDTO withdrawQuery(WithdrawQueryReqDTO withdrawQueryReqDTO) throws BaseException {
        return null;
    }

}

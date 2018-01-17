package com.dream.pay.channel.service.come;

import com.dream.pay.channel.access.dto.PayQueryRepDTO;
import com.dream.pay.channel.access.dto.PayQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeType;
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
public abstract class AbsGateWayServiceImpl implements GateWayService {

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
}

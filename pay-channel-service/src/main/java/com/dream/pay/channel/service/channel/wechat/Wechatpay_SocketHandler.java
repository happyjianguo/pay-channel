package com.dream.pay.channel.service.channel.wechat;

import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.core.exception.ChannelSocketException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.socket.ChannelSocketHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Wechatpay_SocketHandler implements ChannelSocketHandler {

    public byte[] send(BaseReq req, byte[] reqMsg, ChannelConfig channelConfig) throws ChannelSocketException {
        Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
        String repContent = "";
        try {
            String sendreqMsg = new String(reqMsg, config.getCharset().trim());
            log.info("[微信通信组件]发送报文" + sendreqMsg);
            String postUrl = "";
            if (req.getTradeType().equals(TradeType.REFUND_APPLY)) {
                postUrl = config.getRefundApplyUrl();
                WechatPubPayHttpClient httpclient = new WechatPubPayHttpClient(config.getMerchantNo(),
                        config.getPxfPath());
                log.info("[微信通信组件]请求地址:" + postUrl);
                repContent = httpclient.postWithCert(postUrl, reqMsg);
                log.info("[微信通信组件]接收报文" + repContent);
                return repContent.getBytes();
            } else {
                if (req.getTradeType().equals(TradeType.PAY_APPLY)) {
                    postUrl = config.getPayApplyUrl();
                } else if (req.getTradeType().equals(TradeType.PAY_QUERY)) {
                    postUrl = config.getPayQueryUrl();
                } else if (req.getTradeType().equals(TradeType.REFUND_QUERY)) {
                    postUrl = config.getRefundQueryUrl();
                }
                log.info("[微信通信组件]请求地址:" + postUrl);
                WechatPubPayHttpClient httpclient = new WechatPubPayHttpClient();
                repContent = httpclient.postWithNoCert(postUrl, reqMsg);
                log.info("[微信通信组件]接收报文" + repContent);
                return repContent.getBytes();
            }
        } catch (Exception e) {
            log.error("[微信通信组件]发送报文[出现异常]", e);
            throw new ChannelSocketException(ChannelRtnCodeEnum.S10000, TradeStatus.FAIL);
        }
    }
}
package com.dream.pay.channel.access.impl.notify;

import java.util.Date;

import com.dream.pay.channel.access.dto.PayNotifyRepDTO;
import com.dream.pay.channel.access.dto.PayNotifyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.notify.GatePayNotifyFacade;
import com.dream.pay.channel.service.context.Channel;
import com.dream.pay.channel.service.exception.BaseException;
import com.youzan.platform.util.lang.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 支付渠道服务
 * <p>
 * 网关支付通知
 *
 * @author mengzhenbin
 */
@Slf4j
public class GatePayNotifyFacadeImpl implements GatePayNotifyFacade {
    @Autowired
    private Channel channel;

    @Override
    public PayNotifyRepDTO payNotify(PayNotifyReqDTO payNotifyReqDTO) {
        log.info("[支付通知][开始]-[{}]-[{}]-[{}]", payNotifyReqDTO.getPayType().name(), payNotifyReqDTO.getCallBackContent(),
                DateUtil.currentDate());
        channel.select(payNotifyReqDTO.getPayType());
        PayNotifyRepDTO payNotifyRepDTO = new PayNotifyRepDTO();
        try {
            payNotifyRepDTO = channel.getGateWayService().payNotify(payNotifyReqDTO);
            log.info("[支付通知][结束]-[{}]-[{}]-[{}]", payNotifyRepDTO.getTradeStatus(), payNotifyRepDTO.getChlRtnMsg(),
                    payNotifyRepDTO.getChlRepDateTime());
        } catch (BaseException e) {
            payNotifyRepDTO.setTradeStatus(TradeStatus.FAIL);
            payNotifyRepDTO.setChlRtnCode(e.getErrorCode());
            payNotifyRepDTO.setChlRtnMsg(e.getErrorMsg());
            log.error("[支付通知][发生异常]-[{}]-[{}]", payNotifyReqDTO.getPayType().name(), payNotifyRepDTO.getBizOrderNo(),
                    e);
        }
        return payNotifyRepDTO;
    }
}

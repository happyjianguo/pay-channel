package com.dream.pay.channel.access.impl.notify;

import com.dream.pay.channel.access.dto.RefundNotifyRepDTO;
import com.dream.pay.channel.access.dto.RefundNotifyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.notify.GateRefundNotifyFacade;
import com.dream.pay.channel.service.context.Channel;
import com.dream.pay.channel.service.exception.BaseException;
import com.youzan.platform.util.lang.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 支付渠道服务
 * <p>
 * 网关退款通知
 *
 * @author mengzhenbin
 */
@Slf4j
public class GateRefundNotifyFacadeImpl implements GateRefundNotifyFacade {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GateRefundNotifyFacadeImpl.class);
    @Autowired
    private Channel channel;

    @Override
    public RefundNotifyRepDTO refundNotify(RefundNotifyReqDTO refundNotifyReqDTO) {
        logger.info("[退款通知][开始]-[{}]-[{}]-[{}]", refundNotifyReqDTO.getPayType().name(),
                refundNotifyReqDTO.getMap(), DateUtil.currentDate());
        channel.select(refundNotifyReqDTO.getPayType());
        RefundNotifyRepDTO refundNotifyRepDTO = new RefundNotifyRepDTO();
        try {
            refundNotifyRepDTO = channel.getGateWayService().refundNotify(refundNotifyReqDTO);
            logger.info("[退款通知][结束]-[{}]-[{}]-[{}]", refundNotifyRepDTO.getTradeStatus(), refundNotifyRepDTO.getChlRtnMsg(),
                    refundNotifyRepDTO.getChlRepDateTime());
        } catch (BaseException e) {
            refundNotifyRepDTO.setTradeStatus(TradeStatus.FAIL);
            refundNotifyRepDTO.setChlRtnCode(e.getErrorCode());
            refundNotifyRepDTO.setChlRtnMsg(e.getErrorMsg());
            logger.error("[支付通知][发生异常]-[{}]-[{}]", refundNotifyReqDTO.getPayType().name(), "", e);
        }
        return refundNotifyRepDTO;
    }
}

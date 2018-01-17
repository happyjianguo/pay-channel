package com.dream.pay.channel.access.impl.repair;

import com.dream.pay.channel.access.dto.RefundQueryRepDTO;
import com.dream.pay.channel.access.dto.RefundQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.repair.GateRefundQueryFacade;
import com.dream.pay.channel.service.context.Channel;
import com.dream.pay.channel.service.exception.BaseException;
import com.youzan.platform.util.lang.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 支付渠道服务
 * <p>
 * 网关退款
 *
 * @author mengzhenbin
 */
@Service("gateRefundQueryFacadeImpl")
@Slf4j
public class GateRefundQueryFacadeImpl implements GateRefundQueryFacade {
    @Autowired
    private Channel channel;

    @Override
    public RefundQueryRepDTO refundQuery(RefundQueryReqDTO refundQueryReqDTO) {
        log.info("[退款查询][开始]-[{}]-[{}]-[{}]", refundQueryReqDTO.getPayType().name(), refundQueryReqDTO.getBizRefundNo(),
                DateUtil.currentDate());
        channel.select(refundQueryReqDTO.getPayType());
        RefundQueryRepDTO refundQueryRepDTO = new RefundQueryRepDTO();
        try {
            refundQueryRepDTO = channel.getGateWayService().refundQuery(refundQueryReqDTO);
            log.info("[退款查询][结束]-[{}]-[{}]-[{}]-[{}]", refundQueryReqDTO.getBizRefundNo(),
                    refundQueryRepDTO.getTradeStatus(), refundQueryRepDTO.getChlRtnMsg(),
                    refundQueryRepDTO.getChlRepDateTime());

        } catch (BaseException e) {
            refundQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
            refundQueryRepDTO.setChlRtnCode(e.getErrorCode());
            refundQueryRepDTO.setChlRtnMsg(e.getErrorMsg());
            refundQueryRepDTO.setBizOrderNo(refundQueryReqDTO.getBizOrderNo());
            refundQueryRepDTO.setRefundBatchNo(refundQueryReqDTO.getRefundBatchNo());
            log.error("[退款查询][发生异常]-[{}]-[{}]", refundQueryReqDTO.getPayType().name(),
                    refundQueryReqDTO.getBizRefundNo(), e);
        }
        return refundQueryRepDTO;
    }
}
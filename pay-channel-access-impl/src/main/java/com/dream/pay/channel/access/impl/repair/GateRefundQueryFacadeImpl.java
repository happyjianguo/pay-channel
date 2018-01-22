package com.dream.pay.channel.access.impl.repair;

import com.dream.pay.channel.access.dto.RefundQueryRepDTO;
import com.dream.pay.channel.access.dto.RefundQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.repair.GateRefundQueryFacade;
import com.dream.pay.channel.service.core.context.Channel;
import com.dream.pay.channel.service.core.exception.BaseException;
import com.youzan.platform.util.lang.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

/**
 * 支付渠道服务
 * <p>
 * 网关退款
 *
 * @author mengzhenbin
 */
@Slf4j
public class GateRefundQueryFacadeImpl implements GateRefundQueryFacade {
    @Autowired
    private Channel channel;

    @Override
    public RefundQueryRepDTO refundQuery(RefundQueryReqDTO refundQueryReqDTO) {
        log.info("[退款查询][开始]-[{}]-[{}]-[{}]", refundQueryReqDTO.getPayType().name(), refundQueryReqDTO.getRefundDetailNo(),
                DateUtil.currentDate());
        channel.select(refundQueryReqDTO.getPayType());
        RefundQueryRepDTO refundQueryRepDTO;
        try {
            refundQueryRepDTO = channel.getGateWayService().refundQuery(refundQueryReqDTO);
            log.info("[退款查询][结束]-[{}]-[{}]-[{}]-[{}]", refundQueryReqDTO.getRefundDetailNo(),
                    refundQueryRepDTO.getTradeStatus(), refundQueryRepDTO.getChlRtnMsg(),
                    refundQueryRepDTO.getChlFinishTime());

        } catch (BaseException e) {
            refundQueryRepDTO = new RefundQueryRepDTO();
            refundQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
            refundQueryRepDTO.setChlRtnCode(e.getErrorCode());
            refundQueryRepDTO.setChlRtnMsg(e.getErrorMsg());
            refundQueryRepDTO.setPayDetailNo(refundQueryReqDTO.getPayDetailNo());
            refundQueryRepDTO.setRefundDetailNo(refundQueryReqDTO.getRefundDetailNo());
            refundQueryRepDTO.setRefundBatchNo(refundQueryReqDTO.getRefundBatchNo());
            log.error("[退款查询][发生异常]-[{}]-[{}]", refundQueryReqDTO.getPayType().name(),
                    refundQueryReqDTO.getRefundDetailNo(), e);
        }
        return refundQueryRepDTO;
    }
}
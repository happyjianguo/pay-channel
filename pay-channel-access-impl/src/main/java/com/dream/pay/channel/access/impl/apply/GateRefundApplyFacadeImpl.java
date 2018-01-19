package com.dream.pay.channel.access.impl.apply;

import com.dream.pay.channel.access.apply.GateRefundApplyFacade;
import com.dream.pay.channel.access.dto.RefundApplyRepDTO;
import com.dream.pay.channel.access.dto.RefundApplyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.context.Channel;
import com.dream.pay.channel.service.exception.BaseException;
import com.youzan.platform.util.lang.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 支付渠道服务
 * <p>
 * 网关退款
 *
 * @author mengzhenbin
 */
@Slf4j
public class GateRefundApplyFacadeImpl implements GateRefundApplyFacade {
    @Autowired
    private Channel channel;

    @Override
    public RefundApplyRepDTO refundApply(RefundApplyReqDTO refundApplyReqDTO) {
        log.info("[退款申请][开始]-[{}]-[{}]-[{}]", refundApplyReqDTO.getPayType().name(), refundApplyReqDTO.getRefundDetailNo(),
                DateUtil.currentDate());
        channel.select(refundApplyReqDTO.getPayType());
        RefundApplyRepDTO refundApplyRepDTO;
        try {
            refundApplyRepDTO = channel.getGateWayService().refundApply(refundApplyReqDTO);
            log.info("[退款申请][结束]-[{}]-[{}]-[{}]", refundApplyRepDTO.getTradeStatus(), refundApplyRepDTO.getChlRtnMsg(),
                    refundApplyRepDTO.getChlFinishTime());
        } catch (BaseException e) {
            refundApplyRepDTO = new RefundApplyRepDTO();
            refundApplyRepDTO.setTradeStatus(TradeStatus.UNKNOW);
            refundApplyRepDTO.setChlRtnCode(e.getErrorCode());
            refundApplyRepDTO.setChlRtnMsg(e.getErrorMsg());
            log.error("[退款申请][发生异常]-[{}]-[{}]", refundApplyReqDTO.getPayType().name(),
                    refundApplyReqDTO.getRefundDetailNo(), e);
        }
        return refundApplyRepDTO;
    }

}

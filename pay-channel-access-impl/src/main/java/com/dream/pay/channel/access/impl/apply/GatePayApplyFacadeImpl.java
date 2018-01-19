package com.dream.pay.channel.access.impl.apply;

import com.dream.pay.channel.access.apply.GatePayApplyFacade;
import com.dream.pay.channel.access.dto.PayApplyRepDTO;
import com.dream.pay.channel.access.dto.PayApplyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.context.Channel;
import com.dream.pay.channel.service.exception.BaseException;
import com.youzan.platform.util.lang.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 支付渠道服务
 * <p>
 * 网关支付
 *
 * @author mengzhenbin
 */
@Slf4j
public class GatePayApplyFacadeImpl implements GatePayApplyFacade {
    @Autowired
    private Channel channel;

    @Override
    public PayApplyRepDTO payApply(PayApplyReqDTO payApplyReqDTO) {
        log.info("[支付申请]-[{}]-[{}]-[{}]", payApplyReqDTO.getPayType().name(), payApplyReqDTO.getPayDetailNo(),
                DateUtil.currentDate());
        channel.select(payApplyReqDTO.getPayType());
        PayApplyRepDTO payApplyRepDTO;
        try {
            payApplyRepDTO = channel.getGateWayService().payApply(payApplyReqDTO);
            log.info("[支付申请][结束]-[{}]-[{}]-[{}]\n{}", payApplyRepDTO.getTradeStatus(), payApplyRepDTO.getChlRtnMsg(),
                    payApplyRepDTO.getChlFinishTime(), payApplyRepDTO.getRepContent());
        } catch (BaseException e) {
            payApplyRepDTO = new PayApplyRepDTO();
            payApplyRepDTO.setTradeStatus(TradeStatus.FAIL);
            payApplyRepDTO.setChlRtnCode(e.getErrorCode());
            payApplyRepDTO.setChlRtnMsg(e.getErrorMsg());
            log.error("[支付申请][发生异常]-[{}]-[{}]", payApplyReqDTO.getPayType().name(), payApplyReqDTO.getPayDetailNo(), e);
        }
        return payApplyRepDTO;
    }

}

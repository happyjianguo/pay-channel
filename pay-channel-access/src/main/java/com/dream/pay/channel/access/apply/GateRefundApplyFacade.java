package com.dream.pay.channel.access.apply;

import com.dream.pay.channel.access.dto.RefundApplyRepDTO;
import com.dream.pay.channel.access.dto.RefundApplyReqDTO;

import javax.ws.rs.Path;


/**
 * 支付渠道服务
 * <p>
 * 网关退款申请
 *
 * @author mengzhenbin
 */
@Path("channel")
public interface GateRefundApplyFacade {
    /**
     * 网关退款申请
     *
     * @param refundApplyReqDTO
     * @return RefundApplyRepDTO
     */
    public RefundApplyRepDTO refundApply(RefundApplyReqDTO refundApplyReqDTO);
}

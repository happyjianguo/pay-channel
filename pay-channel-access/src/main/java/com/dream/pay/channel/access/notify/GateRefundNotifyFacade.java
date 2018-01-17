package com.dream.pay.channel.access.notify;

import com.dream.pay.channel.access.dto.RefundNotifyRepDTO;
import com.dream.pay.channel.access.dto.RefundNotifyReqDTO;

import javax.ws.rs.Path;

/**
 * 支付渠道服务
 * <p>
 * 网关退款通知
 *
 * @author mengzhenbin
 */
@Path("channel")
public interface GateRefundNotifyFacade {
    /**
     * 网关退款通知
     *
     * @param refundNotifyReqDTO
     * @return RefundNotifyRepDTO
     */
    public RefundNotifyRepDTO refundNotify(RefundNotifyReqDTO refundNotifyReqDTO);
}

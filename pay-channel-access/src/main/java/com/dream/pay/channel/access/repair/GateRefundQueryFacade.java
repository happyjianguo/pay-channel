package com.dream.pay.channel.access.repair;

import com.dream.pay.channel.access.dto.RefundQueryRepDTO;
import com.dream.pay.channel.access.dto.RefundQueryReqDTO;

import javax.ws.rs.*;

/**
 * 支付渠道服务
 * <p>
 * 网关退款查询
 *
 * @author mengzhenbin
 */
@Path("channel")
public interface GateRefundQueryFacade {
    /**
     * 网关退款查询
     *
     * @param refundQueryReqDTO
     * @return RefundQueryRepDTO
     */
    public RefundQueryRepDTO refundQuery(RefundQueryReqDTO refundQueryReqDTO);

}

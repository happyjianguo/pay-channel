package com.dream.pay.channel.access.repair;

import com.dream.pay.channel.access.dto.PayQueryRepDTO;
import com.dream.pay.channel.access.dto.PayQueryReqDTO;

import javax.ws.rs.Path;

/**
 * 支付渠道服务
 * <p>
 * 网关支付查询
 *
 * @author mengzhenbin
 */
@Path("channel")
public interface GatePayQueryFacade {
    /**
     * 网关支付查询
     *
     * @param payQueryReqDTO
     * @return PayQueryRepDTO
     */
    public PayQueryRepDTO payQuery(PayQueryReqDTO payQueryReqDTO);
}

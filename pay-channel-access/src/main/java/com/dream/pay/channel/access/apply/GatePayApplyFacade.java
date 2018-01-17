package com.dream.pay.channel.access.apply;

import com.dream.pay.channel.access.dto.PayApplyRepDTO;
import com.dream.pay.channel.access.dto.PayApplyReqDTO;

import javax.ws.rs.Path;

/**
 * 支付渠道服务
 * <p>
 * 网关支付申请
 *
 * @author mengzhenbin
 */
@Path("channel")
public interface GatePayApplyFacade {
    /**
     * 网关支付申请
     *
     * @param payApplyReqDTO
     * @return PayApplyRepDTO
     */
    public PayApplyRepDTO payApply(PayApplyReqDTO payApplyReqDTO);
}

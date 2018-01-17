package com.dream.pay.channel.access.notify;

import com.dream.pay.channel.access.dto.PayNotifyRepDTO;
import com.dream.pay.channel.access.dto.PayNotifyReqDTO;

import javax.ws.rs.Path;

/**
 * 支付渠道服务
 * <p>
 * 网关支付回调
 *
 * @author mengzhenbin
 */
@Path("channel")
public interface GatePayNotifyFacade {
    /**
     * 网关支付回调
     *
     * @param payNotifyReqDTO
     * @return PayNotifyRepDTO
     */
    public PayNotifyRepDTO payNotify(PayNotifyReqDTO payNotifyReqDTO);

}

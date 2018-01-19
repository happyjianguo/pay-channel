package com.dream.pay.channel.access.repair;

import com.dream.pay.channel.access.dto.WithdrawQueryRepDTO;
import com.dream.pay.channel.access.dto.WithdrawQueryReqDTO;

import javax.ws.rs.Path;

/**
 * 支付渠道服务
 * <p>
 * 网关提现查询
 *
 * @author mengzhenbin
 */
@Path("channel")
public interface GateWithdrawQueryFacade {
    /**
     * 网关提现查询
     *
     * @param withdrawQueryReqDTO
     * @return WithdrawQueryRepDTO
     */
    public WithdrawQueryRepDTO withdrawQuery(WithdrawQueryReqDTO withdrawQueryReqDTO);
}

package com.dream.pay.channel.access.apply;

import com.dream.pay.channel.access.dto.WithdrawApplyRepDTO;
import com.dream.pay.channel.access.dto.WithdrawApplyReqDTO;

import javax.ws.rs.Path;

/**
 * 支付渠道服务
 * <p>
 * 网关提现申请
 *
 * @author mengzhenbin
 */
@Path("channel")
public interface GateWithdrawApplyFacade {
    /**
     * 网关提现申请
     *
     * @param withdrawApplyReqDTO
     * @return WithdrawApplyRepDTO
     */
    public WithdrawApplyRepDTO withdrawApply(WithdrawApplyReqDTO withdrawApplyReqDTO);
}

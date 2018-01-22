package com.dream.pay.channel.access.impl.apply;

import com.dream.pay.channel.access.apply.GateWithdrawApplyFacade;
import com.dream.pay.channel.access.dto.WithdrawApplyRepDTO;
import com.dream.pay.channel.access.dto.WithdrawApplyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.core.context.Channel;
import com.dream.pay.channel.service.core.exception.BaseException;
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
public class GateWithdrawApplyFacadeImpl implements GateWithdrawApplyFacade {
    @Autowired
    private Channel channel;

    @Override
    public WithdrawApplyRepDTO withdrawApply(WithdrawApplyReqDTO withdrawApplyReqDTO) {
        log.info("[提现申请]-[{}]-[{}]-[{}]", withdrawApplyReqDTO.getPayType().name(), withdrawApplyReqDTO.getWithdrawNo(),
                DateUtil.currentDate());
        channel.select(withdrawApplyReqDTO.getPayType());
        WithdrawApplyRepDTO withdrawApplyRepDTO;
        try {
            withdrawApplyRepDTO = channel.getGateWayService().withdrawApply(withdrawApplyReqDTO);
            log.info("[提现申请][结束]-[{}]-[{}]-[{}]", withdrawApplyRepDTO.getTradeStatus(), withdrawApplyRepDTO.getChlRtnMsg(),
                    withdrawApplyRepDTO.getChlFinishTime());
        } catch (BaseException e) {
            withdrawApplyRepDTO = new WithdrawApplyRepDTO();
            withdrawApplyRepDTO.setTradeStatus(TradeStatus.FAIL);
            withdrawApplyRepDTO.setChlRtnCode(e.getErrorCode());
            withdrawApplyRepDTO.setChlRtnMsg(e.getErrorMsg());
            log.error("[提现申请][发生异常]-[{}]-[{}]", withdrawApplyReqDTO.getPayType().name(), withdrawApplyReqDTO.getWithdrawNo(), e);
        }
        return withdrawApplyRepDTO;
    }
}

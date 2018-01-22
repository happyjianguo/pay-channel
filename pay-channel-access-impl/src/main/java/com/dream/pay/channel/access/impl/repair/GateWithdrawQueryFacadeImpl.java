package com.dream.pay.channel.access.impl.repair;

import com.dream.pay.channel.access.dto.WithdrawQueryRepDTO;
import com.dream.pay.channel.access.dto.WithdrawQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.repair.GateWithdrawQueryFacade;
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
public class GateWithdrawQueryFacadeImpl implements GateWithdrawQueryFacade {
    @Autowired
    private Channel channel;

    @Override
    public WithdrawQueryRepDTO withdrawQuery(WithdrawQueryReqDTO withdrawQueryReqDTO) {
        log.info("[提现查询][开始]-[{}]-[{}]-[{}]", withdrawQueryReqDTO.getPayType().name(), withdrawQueryReqDTO.getWithdrawNo(),
                DateUtil.currentDate());
        channel.select(withdrawQueryReqDTO.getPayType());
        WithdrawQueryRepDTO withdrawQueryRepDTO;
        try {
            withdrawQueryRepDTO = channel.getGateWayService().withdrawQuery(withdrawQueryReqDTO);
            log.info("[提现查询][结束]-[{}]-[{}]-[{}]-[{}]", withdrawQueryRepDTO.getWithdrawChannelNo(), withdrawQueryRepDTO.getTradeStatus(),
                    withdrawQueryRepDTO.getChlRtnMsg(), withdrawQueryRepDTO.getChlFinishTime());
        } catch (BaseException e) {
            withdrawQueryRepDTO = new WithdrawQueryRepDTO();
            withdrawQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
            withdrawQueryRepDTO.setChlRtnCode(e.getErrorCode());
            withdrawQueryRepDTO.setChlRtnMsg(e.getErrorMsg());
            log.error("[支付查询][发生异常]-[{}]-[{}]", withdrawQueryReqDTO.getPayType().name(), withdrawQueryReqDTO.getWithdrawNo(), e);
        }
        return withdrawQueryRepDTO;
    }
}

package com.dream.pay.channel.service.access;

import com.dream.pay.channel.access.dto.*;
import com.dream.pay.channel.service.exception.BaseException;

public interface GateWayService {
    /**
     * 支付
     *
     * @param payApplyReqDTO
     * @return PayApplyRepDTO
     * @throws BaseException
     */
    public PayApplyRepDTO payApply(PayApplyReqDTO payApplyReqDTO) throws BaseException;

    /**
     * 支付查询
     *
     * @param payQueryReqDTO
     * @return PayQueryRepDTO
     * @throws BaseException
     */
    public PayQueryRepDTO payQuery(PayQueryReqDTO payQueryReqDTO) throws BaseException;

    /**
     * 支付回调
     *
     * @param payNotifyReqDTO
     * @return PayNotifyRepDTO
     * @throws BaseException
     */
    public PayNotifyRepDTO payNotify(PayNotifyReqDTO payNotifyReqDTO) throws BaseException;

    /**
     * 退款
     *
     * @param refundApplyReqDTO
     * @return RefundApplyRepDTO
     * @throws BaseException
     */
    public RefundApplyRepDTO refundApply(RefundApplyReqDTO refundApplyReqDTO) throws BaseException;

    /**
     * 退款查询
     *
     * @param refundReqDTO
     * @return RefundQueryRepDTO
     * @throws BaseException
     */
    public RefundQueryRepDTO refundQuery(RefundQueryReqDTO refundReqDTO) throws BaseException;

    /**
     * 提现申请
     *
     * @param withdrawApplyReqDTO
     * @return WithdrawApplyRepDTO
     * @throws BaseException
     */
    public WithdrawApplyRepDTO withdrawApply(WithdrawApplyReqDTO withdrawApplyReqDTO) throws BaseException;

    /**
     * 提现查询
     *
     * @param withdrawQueryReqDTO
     * @return WithdrawQueryRepDTO
     * @throws BaseException
     */
    public WithdrawQueryRepDTO withdrawQuery(WithdrawQueryReqDTO withdrawQueryReqDTO) throws BaseException;
}

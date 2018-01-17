package com.dream.pay.channel.service.come;

import com.dream.pay.channel.access.dto.*;
import com.dream.pay.channel.service.exception.BaseException;

public interface GateWayService {
    /**
     * 支付
     *
     * @param payApplyReqDTO
     * @return
     * @throws BaseException
     */
    public PayApplyRepDTO payApply(PayApplyReqDTO payApplyReqDTO) throws BaseException;

    /**
     * 支付查询
     *
     * @param payQueryReqDTO
     * @return
     * @throws BaseException
     */
    public PayQueryRepDTO payQuery(PayQueryReqDTO payQueryReqDTO) throws BaseException;

    /**
     * 签约查询
     *
     * @param payQueryReqDTO
     * @return
     * @throws BaseException
     */
    public PayQueryRepDTO agreeQuery(PayQueryReqDTO payQueryReqDTO) throws BaseException;

    /**
     * 公钥查询
     *
     * @param payQueryReqDTO
     * @return
     * @throws BaseException
     */
    public PayQueryRepDTO pubKeyQuery(PayQueryReqDTO payQueryReqDTO) throws BaseException;

    /**
     * 支付回调
     *
     * @param payNotifyReqDTO
     * @return
     * @throws BaseException
     */
    public PayNotifyRepDTO payNotify(PayNotifyReqDTO payNotifyReqDTO) throws BaseException;

    /**
     * 协议回调
     *
     * @param payNotifyReqDTO
     * @return
     * @throws BaseException
     */
    public PayNotifyRepDTO agreeNotify(PayNotifyReqDTO payNotifyReqDTO) throws BaseException;

    /**
     * 退款
     *
     * @param refundApplyReqDTO
     * @return
     * @throws BaseException
     */
    public RefundApplyRepDTO refundApply(RefundApplyReqDTO refundApplyReqDTO) throws BaseException;

    /**
     * 退款查询
     *
     * @param refundReqDTO
     * @return
     * @throws BaseException
     */
    public RefundQueryRepDTO refundQuery(RefundQueryReqDTO refundReqDTO) throws BaseException;

    /**
     * 退款回调
     *
     * @param refundNotifyReqDTO
     * @return
     * @throws BaseException
     */
    public RefundNotifyRepDTO refundNotify(RefundNotifyReqDTO refundNotifyReqDTO) throws BaseException;

    /**
     * 对账
     *
     * @param checkFileReqDTO
     * @return
     * @throws BaseException
     */
    public CheckFileRepDTO checkFile(CheckFileReqDTO checkFileReqDTO) throws BaseException;

}

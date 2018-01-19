package com.dream.pay.channel.service.channel.alipay.common;

import com.dream.pay.channel.access.dto.RefundQueryRepDTO;
import com.dream.pay.channel.access.dto.RefundQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.channel.alipay.AlipayUtil;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.channel.service.exception.ChannelMsgException;
import com.dream.pay.channel.service.handler.config.ChannelConfig;
import com.dream.pay.channel.service.handler.msg.impl.UrlChannelMsgHandler;
import com.dream.pay.utils.DESUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 退款查询报文解析处理类
 */
@Slf4j
public class Alipay_RefundQueryMsgHandler extends UrlChannelMsgHandler<RefundQueryReqDTO, RefundQueryRepDTO> {

    @Override
    public RefundQueryReqDTO beforBuildMsg(RefundQueryReqDTO req, ChannelConfig channelConfig)
            throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
        try {
            super.setParam("service", StringUtils.trim(config.getRefundQueryServcie()));
            super.setParam("partner", StringUtils.trim(config.getPartner()));
            super.setParam("_input_charset", StringUtils.trim(config.getCharset()));
            super.setParam("sign_type", StringUtils.trim(config.getSignType()));
            // 两个不能同时用,否则查出来的结果为null
            String batch_no = StringUtils.trim(req.getRefundBatchNo());
            String trade_no = StringUtils.trim(req.getBankPayDetailNo());
            if (StringUtils.isNotBlank(batch_no)) {
                super.setParam("batch_no", batch_no);// 原退款单号
            } else if (StringUtils.isNotBlank(trade_no)) {
                super.setParam("trade_no", trade_no);// 支付宝交易号
            }
            String sign = AlipayUtil.createSign(super.getParamMap(), SignType.valueOf(config.getSignType()),
                    DESUtil.decryptModeBase64(config.getSignKey()), Charset.forName(config.getCharset()));
            super.setParam("sign", sign);
            req.setTradeType(TradeType.REFUND_QUERY);
        } catch (Exception e) {
            log.error("[支付宝]-[退款查询]-[报文拼装前设置参数]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return req;
    }

    @SuppressWarnings({"deprecation", "rawtypes"})
    @Override
    public RefundQueryRepDTO resolveMsg(RefundQueryReqDTO req, byte[] rtnMsg, ChannelConfig channelConfig)
            throws ChannelMsgException {
        Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
        RefundQueryRepDTO refundQueryRepDTO = new RefundQueryRepDTO();
        refundQueryRepDTO.setRefundBatchNo(req.getRefundBatchNo());
        refundQueryRepDTO.setPayDetailNo(req.getPayDetailNo());
        String repString = "";
        try {
            repString = StringUtils.toString(rtnMsg, config.getCharset());
            log.info("[支付宝]-[退款查询]-[报文解析]－返回报文[{}]", repString);
            Map resMap = AlipayUtil.queryString2Map(repString);
            if (resMap != null) {
                String isSuccess = (String) resMap.get("is_success");
                String error_code = (String) resMap.get("error_code");
                if (StringUtils.equalsIgnoreCase(isSuccess, "T")) {
                    String result_details = (String) resMap.get("result_details");
                    // 20160712001^2016071221001004430242532850^0.01^SUCCESS$zhifubao@dangdang.com^2088301773687981^0.00^SUCCESS
                    // 20160713009^2016071321001004430260225293^0.01^TRADE_HAS_CLOSED
                    String[] results;
                    if (result_details.contains("$")) {
                        String[] resultsPre = StringUtils.split(result_details, "$");
                        results = StringUtils.split(resultsPre[0], "^");
                    } else {
                        results = StringUtils.split(result_details, "^");
                    }
                    refundQueryRepDTO.setRefundBatchNo(results[0]);// 退款批次号
                    refundQueryRepDTO.setBankPayDetailNo(results[1]);// 支付宝交易流水号
                    String refundAmount = results[2];// 退款金额
                    if (StringUtils.isNotBlank(refundAmount)) {
                        refundQueryRepDTO.setRefundAmount(new BigDecimal(refundAmount));
                    }
                    String resultCode = results[3];// 退款状态码
                    if (resultCode.replace("\r\n", "").equalsIgnoreCase("SUCCESS")) {
                        refundQueryRepDTO.setTradeStatus(TradeStatus.SUCCESS);
                        refundQueryRepDTO.setChlRtnCode(resultCode);
                        refundQueryRepDTO.setChlRtnMsg("退款成功");
                        refundQueryRepDTO.setBankFinishTime(new Date());
                    } else if (resultCode.equalsIgnoreCase("INIT")) {
                        refundQueryRepDTO.setTradeStatus(TradeStatus.PROCESS);
                        refundQueryRepDTO.setChlRtnCode(resultCode);
                        refundQueryRepDTO.setChlRtnMsg("退款处理中");
                    } else if (resultCode.equalsIgnoreCase("BUYER_ERROR")) {
                        refundQueryRepDTO.setTradeStatus(TradeStatus.FAIL);
                        refundQueryRepDTO.setChlRtnCode(resultCode);
                        refundQueryRepDTO.setChlRtnMsg("用户账户异常,请采用其他方式退款");
                    } else if (resultCode.equalsIgnoreCase("SYSTEM_ERROR")) {
                        if (result_details.contains("CHECK_VALID_SUCCESS")) {
                            refundQueryRepDTO.setTradeStatus(TradeStatus.FAIL);
                            refundQueryRepDTO.setChlRtnCode(resultCode);
                            refundQueryRepDTO.setChlRtnMsg("系统异常，请联系支付宝同学进行确认最终退款结果");
                        }
                    } else {
                        refundQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
                        refundQueryRepDTO.setChlRtnCode(resultCode);
                        refundQueryRepDTO.setChlRtnMsg("退款状态未知");
                    }
                } else {
                    refundQueryRepDTO.setChlRtnCode(error_code);
                    refundQueryRepDTO.setChlRtnMsg("退款状态未知");
                    refundQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
                }
            } else {
                refundQueryRepDTO.setChlRtnCode(TradeStatus.UNKNOW.name());
                refundQueryRepDTO.setChlRtnMsg("退款状态未知");
                refundQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
            }
            log.info("[支付宝]-[退款查询]-[报文解析]－交易状态为[{}],状态码[{}],状态描述位[{}]", refundQueryRepDTO.getTradeStatus(),
                    refundQueryRepDTO.getChlRtnCode(), refundQueryRepDTO.getChlRtnMsg());
        } catch (Exception e) {
            log.error("[支付宝]-[退款查询]-[报文解析]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.UNKNOW);
        }
        return refundQueryRepDTO;
    }
}

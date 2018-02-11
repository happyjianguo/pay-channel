package com.dream.pay.channel.service.channel.wechat;

import com.dream.pay.channel.access.dto.RefundQueryRepDTO;
import com.dream.pay.channel.access.dto.RefundQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.XMLChannelMsgHandler;
import com.dream.pay.channel.service.enums.BankRtnCodeEnum;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.utils.BeanUtil;
import com.dream.pay.utils.DESUtil;
import com.dream.pay.utils.PropUtil;
import com.dream.pay.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 退款查询报文解析处理类
 */
@Slf4j
public class WechatPay_RefundQueryMsgHandler extends XMLChannelMsgHandler<RefundQueryReqDTO, RefundQueryRepDTO> {

    @Override
    public RefundQueryReqDTO beforBuildMsg(RefundQueryReqDTO req, ChannelConfig channelConfig)
            throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
        try {
            super.setParam("appid", config.getAppId());
            super.setParam("mch_id", config.getMerchantNo());// 商户号
            super.setParam("nonce_str", StringUtils.trim(RandomStringUtils.randomAlphabetic(24)));// 随机字符串
            // 校验参数，规则优先级：refund_id>out_refund_no>transaction_id>out_trade_no
            if (StringUtils.isNotBlank(req.getBankRefundDetailNo())) {
                super.setParam("refund_id", req.getBankRefundDetailNo());// 退款订单号－－第三方
            } else if (StringUtils.isNotBlank(req.getRefundDetailNo())) {
                super.setParam("out_refund_no", req.getRefundDetailNo());// 退款订单号－－业务线
            } else if (StringUtils.isNotBlank(req.getBankPayDetailNo())) {
                super.setParam("transaction_id", req.getBankPayDetailNo());// 支付流水号－－第三方
            } else {
                super.setParam("out_trade_no", req.getPayDetailNo());// 支付订单号－－业务线
            }
            String sign = WechatpayUtil.createSign(super.getParamMap(),
                    SignType.valueOf(StringUtils.trim(config.getSignType())),
                    DESUtil.decryptModeBase64(config.getSignKey()),
                    Charset.forName(StringUtils.trim(config.getCharset())));
            super.setParam("sign", sign);
            req.setTradeType(TradeType.REFUND_QUERY);
        } catch (Exception e) {
            log.error("[微信支付]-[退款查询]-[拼装报文前设置参数]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return req;
    }

    @SuppressWarnings("deprecation")
    @Override
    public RefundQueryRepDTO resolveMsg(RefundQueryReqDTO req, byte[] rtnMsg, ChannelConfig channelConfig)
            throws ChannelMsgException {
        Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
        RefundQueryRepDTO refundQueryRepDTO = new RefundQueryRepDTO();
        try {
            String repString = StringUtils.toString(rtnMsg, StringUtils.trim(config.getCharset()));
            Map<String, Object> resultMap = XmlUtil.fromXml(repString);
            String resReturnCode = (String) resultMap.get("return_code");
            if (!resReturnCode.equals("SUCCESS")) {
                String resReturnMsg = (String) resultMap.get("return_msg");
                refundQueryRepDTO.setChlRtnCode(resReturnCode);
                refundQueryRepDTO.setChlRtnMsg(resReturnMsg);
                refundQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
                log.error("[微信支付]-[退款查询]-[解析返回报文]-[通信标示码不为成功[{}],返回信息为[{}]]", resReturnCode, resReturnMsg);
                return refundQueryRepDTO;
            }
            boolean checkSignFlag = PropUtil.getBoolean("wechatPubPay.checkSign.flag", true);
            if (checkSignFlag) {
                Map<String, String> paramMap = BeanUtil.convertMap(resultMap, String.class);
                String localSign = WechatpayUtil.createSign(paramMap,
                        SignType.valueOf(StringUtils.trim(config.getSignType())),
                        DESUtil.decryptModeBase64(config.getSignKey()),
                        Charset.forName(StringUtils.trim(config.getCharset())));
                boolean signFlag = localSign.equals(resultMap.get("sign"));
                if (!signFlag) {
                    log.error("[微信支付]-[退款查询]-[解析返回报文]-[验签失败]-[{}]!=[{}]", resultMap.get("sign"), localSign);
                    throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
                }
            }
            String resResultCode = (String) resultMap.get("result_code");
            if (resResultCode.equals("SUCCESS")) {
                String resRefundCount = (String) resultMap.get("refund_count");
                int count = Integer.parseInt(resRefundCount);
                if (count == 1) {
                    String resRefundStatus = (String) resultMap.get("refund_status_0");// 退款结果
                    String resOutRefundNo = (String) resultMap.get("out_refund_no_0");// 业务线退款订单号
                    String resRefundId = (String) resultMap.get("refund_id_0");// 微信返回的退款流水号
                    String resRefundFee = (String) resultMap.get("refund_fee_0");// 退款金额
                    log.info("[微信支付]-[退款查询]-[解析返回报文]-[退款状态为[{}],业务线退款订单号为[{}],微信退款流水号为[{}],退款金额为[{}]]", resRefundStatus,
                            resOutRefundNo, resRefundId, resRefundFee);
                    if ("SUCCESS".equals(resRefundStatus)) {
                        refundQueryRepDTO.setChlRtnCode(BankRtnCodeEnum.S00000.name());
                        refundQueryRepDTO.setChlRtnMsg(BankRtnCodeEnum.S00000.getValue());
                        refundQueryRepDTO.setTradeStatus(TradeStatus.SUCCESS);
                    } else if ("FAIL".equals(resRefundStatus) || "NOTSURE".equals(resRefundStatus)
                            || "CHANGE".equals(resRefundStatus)) {
                        refundQueryRepDTO.setChlRtnCode(BankRtnCodeEnum.F00000.name());
                        refundQueryRepDTO.setChlRtnMsg(BankRtnCodeEnum.F00000.getValue());
                        refundQueryRepDTO.setTradeStatus(TradeStatus.FAIL);
                    } else if ("PROCESSING".equals(resRefundStatus)) {
                        refundQueryRepDTO.setChlRtnCode(BankRtnCodeEnum.P00000.name());
                        refundQueryRepDTO.setChlRtnMsg(BankRtnCodeEnum.P00000.getValue());
                        refundQueryRepDTO.setTradeStatus(TradeStatus.PROCESS);
                    } else {
                        refundQueryRepDTO.setChlRtnCode(BankRtnCodeEnum.U00000.name());
                        refundQueryRepDTO.setChlRtnMsg(BankRtnCodeEnum.U00000.getValue());
                        refundQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
                    }
                    if (StringUtils.isNotBlank(resRefundFee)) {
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        refundQueryRepDTO.setRefundAmount(new BigDecimal(
                                decimalFormat.format(new BigDecimal(resRefundFee).divide(new BigDecimal(100)))));
                    }
                    refundQueryRepDTO.setRefundDetailNo(resOutRefundNo);// 业务线退款订单号
                    refundQueryRepDTO.setBankRefundDetailNo(resRefundId);// 银行返回退款流水号
                    refundQueryRepDTO.setPayDetailNo(req.getPayDetailNo());
                    refundQueryRepDTO.setBankPayDetailNo(req.getBankPayDetailNo());
                    refundQueryRepDTO.setRefundBatchNo(req.getRefundBatchNo());
                    return refundQueryRepDTO;
                } else {
                    // 退款查询返回多条或者0条，异常
                    log.error("[微信支付]-[退款查询]-[解析返回报文]-[返回多条信息]");
                    throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.UNKNOW);
                }
            } else {// 网关返回业务resultcode 返回fail错误时，返回结果未签名，记录retcode、retmsg看失败详情。
                String resErrCode = (String) resultMap.get("err_code");
                String resErrCodeDes = (String) resultMap.get("err_code_des");
                refundQueryRepDTO.setRefundBatchNo(req.getRefundBatchNo());
                refundQueryRepDTO.setPayDetailNo(req.getPayDetailNo());
                refundQueryRepDTO.setBankPayDetailNo(req.getBankPayDetailNo());
                refundQueryRepDTO.setRefundDetailNo(req.getRefundDetailNo());
                refundQueryRepDTO.setBankRefundDetailNo(req.getBankRefundDetailNo());
                refundQueryRepDTO.setChlRtnCode(resErrCode);
                refundQueryRepDTO.setChlRtnMsg(resErrCodeDes);
                refundQueryRepDTO.setTradeStatus(TradeStatus.FAIL);
                return refundQueryRepDTO;
            }
        } catch (Exception e) {
            log.error("[微信支付]-[退款查询]-[解析返回报文]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.UNKNOW);
        }
    }

    public static void main(String[] args) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("appid", "wx8be4aaecf78a1934");
        resultMap.put("cash_fee", "9700");
        resultMap.put("mch_id", "1312747401");
        resultMap.put("nonce_str", "MrvRJGbP7Vpo9cO6");
        resultMap.put("out_refund_no_0", "201612201272557626");
        resultMap.put("out_trade_no", "9727344322");
        resultMap.put("refund_channel_0", "ORIGINAL");
        resultMap.put("refund_count", "1");
        resultMap.put("refund_fee", "9700");
        resultMap.put("refund_fee_0", "9700");
        resultMap.put("refund_id_0", "2007612001201612200661342277");
        resultMap.put("refund_recv_accout_0", "招商银行信用卡8078");
        resultMap.put("refund_status_0", "PROCESSING");
        resultMap.put("result_code", "SUCCESS");
        resultMap.put("return_code", "SUCCESS");
        resultMap.put("return_msg", "OK");
        resultMap.put("sign", "786D6509FF17BAD82DE66AEAC55C01D1");
        resultMap.put("total_fee", "9700");
        resultMap.put("transaction_id", "4007612001201612193332291624");
        String localSign = WechatpayUtil.createSign(resultMap, SignType.MD5,
                DESUtil.decryptModeBase64("B/n9pCssu57LFQsJwU/DKqqUePf1RVyAfNn4uJ1qnwxRLBFvMizF2Q==",""),
                Charset.forName(StringUtils.trim("UTf-8")));
        boolean signFlag = localSign.equals(resultMap.get("sign"));
        System.out.println(localSign);
        System.out.println(signFlag);
    }

}

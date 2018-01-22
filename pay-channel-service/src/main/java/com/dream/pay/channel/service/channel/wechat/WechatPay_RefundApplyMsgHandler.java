package com.dream.pay.channel.service.channel.wechat;

import com.dream.pay.channel.access.dto.RefundApplyRepDTO;
import com.dream.pay.channel.access.dto.RefundApplyReqDTO;
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
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 退款申请报文解析处理类
 */
@Slf4j
public class WechatPay_RefundApplyMsgHandler extends XMLChannelMsgHandler<RefundApplyReqDTO, RefundApplyRepDTO> {

    @Override
    public RefundApplyReqDTO beforBuildMsg(RefundApplyReqDTO req, ChannelConfig channelConfig)
            throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        try {
            Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
            super.setParam("appid", config.getAppId());
            super.setParam("mch_id", config.getMerchantNo());// 财付通的商户号
            super.setParam("nonce_str", StringUtils.trim(RandomStringUtils.randomAlphabetic(24)));// 随机字符串
            super.setParam("out_trade_no", req.getPayDetailNo());// 原支付订单号
            super.setParam("out_refund_no", req.getRefundDetailNo());// 商户退货订单号
            DecimalFormat format = new DecimalFormat("#0");
            super.setParam("total_fee", format.format(req.getPayAmount().multiply(new BigDecimal(100))));// 订单金额以分为单位
            super.setParam("refund_fee", format.format(req.getRefundAmount().multiply(new BigDecimal(100))));// 订单金额以分为单位
            super.setParam("op_user_id", req.getPartnerId().getCode());
            String sign = WechatpayUtil.createSign(super.getParamMap(),
                    SignType.valueOf(StringUtils.trim(config.getSignType())),
                    DESUtil.decryptModeBase64(config.getSignKey()),
                    Charset.forName(StringUtils.trim(config.getCharset())));
            super.setParam("sign", sign);
            req.setTradeType(TradeType.REFUND_APPLY);
        } catch (Exception e) {
            log.error("[微信支付]-[退款申请]-[拼装报文前设置参数]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return req;
    }

    @SuppressWarnings("deprecation")
    @Override
    public RefundApplyRepDTO resolveMsg(RefundApplyReqDTO req, byte[] rtnMsg, ChannelConfig channelConfig)
            throws ChannelMsgException {
        Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
        RefundApplyRepDTO refundApplyRepDTO = new RefundApplyRepDTO();
        String repString = "";
        try {
            repString = StringUtils.toString(rtnMsg, StringUtils.trim(config.getCharset()));
            Map<String, Object> resultMap = XmlUtil.fromXml(repString);
            String resReturnCode = (String) resultMap.get("return_code");
            if (!resReturnCode.equals("SUCCESS")) {
                String resReturnMsg = (String) resultMap.get("return_msg");
                refundApplyRepDTO.setChlRtnCode(resReturnCode);
                refundApplyRepDTO.setChlRtnMsg(resReturnMsg);
                refundApplyRepDTO.setTradeStatus(TradeStatus.RETRAY);
                log.error("[微信支付]-[退款申请]-[解析返回报文]-[通信标示码不为成功[{}],返回信息为[{}]]", resReturnCode, resReturnMsg);
                return refundApplyRepDTO;
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
                    log.error("[微信支付]-[退款申请]-[解析返回报文]-[验签失败]-[{}]!=[{}]", resultMap.get("sign"), localSign);
                    throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
                }
            }
            String resResultCode = (String) resultMap.get("result_code");
            if (resResultCode.equals("SUCCESS")) {
                String resOutRefundNo = (String) resultMap.get("out_refund_no");// 退款单号
                String resRefundId = (String) resultMap.get("refund_id");// 微信退款单号
                String resRefundFee = (String) resultMap.get("refund_fee");// 退款金额,以分为单位
                if (StringUtils.isNotBlank(resRefundFee)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    refundApplyRepDTO.setRefundAmount(new BigDecimal(
                            decimalFormat.format(new BigDecimal(resRefundFee).divide(new BigDecimal(100)))));
                }
                refundApplyRepDTO.setChlRtnCode(BankRtnCodeEnum.S00000.name());
                refundApplyRepDTO.setChlRtnMsg(BankRtnCodeEnum.S00000.getValue());
                refundApplyRepDTO.setTradeStatus(TradeStatus.SUCCESS);
                refundApplyRepDTO.setRefundDetailNo(resOutRefundNo);
                refundApplyRepDTO.setBankRefundDetailNo(resRefundId);
            } else {// 网关返回业务resultcode返回fail错误时，返回结果未签名，记录retcode、retmsg看失败详情。
                String resErrCode = (String) resultMap.get("err_code");
                String resErrCodeDes = (String) resultMap.get("err_code_des");
                refundApplyRepDTO.setChlRtnCode(resErrCode);
                refundApplyRepDTO.setChlRtnMsg(resErrCodeDes);
                if ("SYSTEMERROR".equals(resErrCode)) {
                    refundApplyRepDTO.setTradeStatus(TradeStatus.RETRAY);
                } else {
                    refundApplyRepDTO.setTradeStatus(TradeStatus.FAIL);
                }
                return refundApplyRepDTO;
            }
        } catch (Exception e) {
            log.error("[微信支付]-[退款申请]-[解析返回报文]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.UNKNOW);
        }
        return refundApplyRepDTO;
    }
}

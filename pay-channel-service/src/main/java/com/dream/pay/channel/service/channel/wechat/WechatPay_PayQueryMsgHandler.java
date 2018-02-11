package com.dream.pay.channel.service.channel.wechat;

import com.dream.pay.channel.access.dto.PayQueryRepDTO;
import com.dream.pay.channel.access.dto.PayQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.XMLChannelMsgHandler;
import com.dream.pay.channel.service.enums.BankRtnCodeEnum;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 支付查询报文解析处理类
 */
@Slf4j
public class WechatPay_PayQueryMsgHandler extends XMLChannelMsgHandler<PayQueryReqDTO, PayQueryRepDTO> {

    @Override
    public PayQueryReqDTO beforBuildMsg(PayQueryReqDTO req, ChannelConfig channelConfig) throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        try {
            Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
            super.setParam("appid", config.getAppId());
            super.setParam("mch_id", config.getMerchantNo());// 在财付通的商户号
            super.setParam("out_trade_no", StringUtils.trim(req.getPayDetailNo()));// 商户订单号
            super.setParam("nonce_str", StringUtils.trim(RandomStringUtils.randomAlphabetic(24)));
            String transaction_id = StringUtils.trim(req.getBankPayDetailNo());
            if (StringUtils.isNotBlank(transaction_id)) {
                super.setParam("transaction_id", transaction_id);// 财付通返回订单号
            }
            String sign = WechatpayUtil.createSign(super.getParamMap(),
                    SignType.valueOf(StringUtils.trim(config.getSignType())),
                    DESUtil.decryptModeBase64(config.getSignKey()),
                    Charset.forName(StringUtils.trim(config.getCharset())));
            super.setParam("sign", sign);
            req.setTradeType(TradeType.PAY_QUERY);
        } catch (Exception e) {
            log.error("[微信支付]-[支付查询]-[拼装报文前设置参数]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return req;
    }

    @SuppressWarnings({"deprecation"})
    @Override
    public PayQueryRepDTO resolveMsg(PayQueryReqDTO req, byte[] rtnMsg, ChannelConfig channelConfig)
            throws ChannelMsgException {
        Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
        PayQueryRepDTO payQueryRepDTO = new PayQueryRepDTO();
        String repString = "";
        try {
            repString = StringUtils.toString(rtnMsg, StringUtils.trim(config.getCharset()));
            Map<String, Object> resultMap = XmlUtil.fromXml(repString);
            String resReturnCode = (String) resultMap.get("return_code");// 通信标识
            if (!resReturnCode.equals("SUCCESS")) {
                String resReturnMsg = (String) resultMap.get("return_msg");
                payQueryRepDTO.setChlRtnCode(resReturnCode);
                payQueryRepDTO.setChlRtnMsg(resReturnMsg);
                payQueryRepDTO.setTradeStatus(TradeStatus.FAIL);
                log.error("[微信支付]-[支付查询]-[解析返回报文]-[通信标示码不为成功[{}],返回信息为[{}]]", resReturnCode, resReturnMsg);
                return payQueryRepDTO;
            }
            String resOutTradeNo = (String) resultMap.get("out_trade_no");// 商户订单号
            payQueryRepDTO.setPayDetailNo(resOutTradeNo);
            boolean checkSignFlag = PropUtil.getBoolean("wechatPubPay.checkSign.flag", true);
            if (checkSignFlag) {
                Map<String, String> paramMap = BeanUtil.convertMap(resultMap, String.class);
                String localSign = WechatpayUtil.createSign(paramMap,
                        SignType.valueOf(StringUtils.trim(config.getSignType())),
                        DESUtil.decryptModeBase64(config.getSignKey()),
                        Charset.forName(StringUtils.trim(config.getCharset())));
                boolean signFlag = localSign.equals(resultMap.get("sign"));
                if (!signFlag) {
                    log.error("[微信支付]-[支付查询]-[解析返回报文]-[验签失败]-[{}]!=[{}]", resultMap.get("sign"), localSign);
                    throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
                }
            }
            String resResultCode = (String) resultMap.get("result_code");
            if (resResultCode.equals("SUCCESS")) {
                String resTradeState = (String) resultMap.get("trade_state");// 多种状态
                String resTransactionId = (String) resultMap.get("transaction_id");// 财付通订单号
                String resTotalFee = (String) resultMap.get("total_fee");// 金额,以分为单位
                String resTimeEnd = (String) resultMap.get("time_end");// 支付完成时间
                if (resTradeState.equals("SUCCESS") || "REFUND".equals(resTradeState)) {// 验证状态是否是success
                    payQueryRepDTO.setChlRtnCode(BankRtnCodeEnum.S00000.name());
                    payQueryRepDTO.setChlRtnMsg(BankRtnCodeEnum.S00000.getValue());
                    payQueryRepDTO.setTradeStatus(TradeStatus.SUCCESS);
                    String openid = (String) resultMap.get("openid");
                    payQueryRepDTO.getMap().put("openid", openid);

                } else {
                    payQueryRepDTO.setChlRtnCode(BankRtnCodeEnum.F00000.name());
                    payQueryRepDTO.setChlRtnMsg(BankRtnCodeEnum.F00000.getValue());
                    payQueryRepDTO.setTradeStatus(TradeStatus.FAIL);
                }
                payQueryRepDTO.setBankPayDetailNo(resTransactionId);
                if (StringUtils.isNotBlank(resTimeEnd)) {
                    payQueryRepDTO.setBankFinishTime((DateUtil.StringToDefaultDate(resTimeEnd)));
                }
                if (StringUtils.isNotBlank(resTotalFee)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    payQueryRepDTO.setPayAmount(new BigDecimal(
                            decimalFormat.format(new BigDecimal(resTotalFee).divide(new BigDecimal(100)))));
                }
            } else {// 网关返回业务resultcode 返回fail错误时，返回结果未签名，记录retcode、retmsg看失败详情。
                String resErrCode = (String) resultMap.get("err_code");
                String resErrCodeDes = (String) resultMap.get("err_code_des");
                payQueryRepDTO.setChlRtnCode(resErrCode);
                payQueryRepDTO.setChlRtnMsg(resErrCodeDes);
                payQueryRepDTO.setTradeStatus(TradeStatus.FAIL);
                return payQueryRepDTO;
            }
        } catch (Exception e) {
            log.error("[微信支付]-[支付查询]-[解析返回报文]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.UNKNOW);
        }
        return payQueryRepDTO;
    }
}

package com.dream.pay.channel.service.channel.wechat;

import com.dream.pay.channel.access.dto.PayNotifyRepDTO;
import com.dream.pay.channel.access.dto.PayNotifyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.UrlChannelMsgHandler;
import com.dream.pay.channel.service.enums.BankRtnCodeEnum;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 支付通知报文解析处理类
 */
@Slf4j
public class WechatPay_PayNotifyMsgHandler extends UrlChannelMsgHandler<PayNotifyReqDTO, PayNotifyRepDTO> {

    @Override
    public PayNotifyRepDTO resolveCallBackMsg(PayNotifyReqDTO req, ChannelConfig channelConfig)
            throws ChannelMsgException {
        PayNotifyRepDTO payNotifyRepDTO = new PayNotifyRepDTO();
        try {
            String repString = req.getCallBackContent();
            Map<String, Object> resultMap = XmlUtil.fromXml(repString);
            String resReturnCode = (String) resultMap.get("return_code");// 通信标识
            if (!resReturnCode.equals("SUCCESS")) {
                String resReturnMsg = (String) resultMap.get("return_msg");
                payNotifyRepDTO.setChlRtnCode(resReturnCode);
                payNotifyRepDTO.setChlRtnMsg(resReturnMsg);
                payNotifyRepDTO.setTradeStatus(TradeStatus.FAIL);
                log.error("[微信支付]-[支付通知]-[解析返回报文]-[通信标示码不为成功[{}],返回信息为[{}]]", resReturnCode, resReturnMsg);
                return payNotifyRepDTO;
            }
            String resOutTradeNo = (String) resultMap.get("out_trade_no");// 先设置商户订单号，方便跟踪错误
            payNotifyRepDTO.setPayDetailNo(resOutTradeNo);
            // 验证签名
            boolean checkSignFlag = PropUtil.getBoolean("wechatPubPay.checkSign.flag", true);
            if (checkSignFlag) {
                Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
                Map<String, String> paramMap = BeanUtil.convertMap(resultMap, String.class);
                String localSign = WechatpayUtil.createSign(paramMap,
                        SignType.valueOf(StringUtils.trim(config.getSignType())),
                        DESUtil.decryptModeBase64(config.getSignKey()),
                        Charset.forName(StringUtils.trim(config.getCharset())));
                boolean signFlag = localSign.equals(resultMap.get("sign"));
                if (!signFlag) {
                    log.error("[微信支付]-[支付通知]-[解析返回报文]-[验签失败]-[{}]!=[{}]", resultMap.get("sign"), localSign);
                    throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
                }
            }
            String resResultCode = (String) resultMap.get("result_code");
            if (resResultCode.equals("SUCCESS")) {
                payNotifyRepDTO.setChlRtnCode(BankRtnCodeEnum.S00000.name());
                payNotifyRepDTO.setChlRtnMsg(BankRtnCodeEnum.S00000.getValue());
                payNotifyRepDTO.setTradeStatus(TradeStatus.SUCCESS);
                String resTransactionId = (String) resultMap.get("transaction_id");// 财付通订单号
                String resTotalFee = (String) resultMap.get("total_fee");// 金额,以分为单位
                String resTimeEnd = (String) resultMap.get("time_end");// 支付完成时间
                String openId = (String) resultMap.get("openid");
                payNotifyRepDTO.setBankPayDetailNo(resTransactionId);
                payNotifyRepDTO.setUserNo(openId);
                if (StringUtils.isNotBlank(resTimeEnd)) {
                    payNotifyRepDTO.setBankFinishTime((DateUtil.StringToDefaultDate(resTimeEnd)));
                }
                if (StringUtils.isNotBlank(resTotalFee)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    payNotifyRepDTO.setPayAmount(new BigDecimal(
                            decimalFormat.format(new BigDecimal(resTotalFee).divide(new BigDecimal(100)))));
                }
                payNotifyRepDTO.setChlFinishTime(new Date());
            } else {// 网关返回业务resultcode 返回fail错误时，返回结果未签名，记录retcode、retmsg看失败详情。
                String resErrCode = (String) resultMap.get("err_code");
                String resErrCodeDes = (String) resultMap.get("err_code_des");
                payNotifyRepDTO.setChlRtnCode(resErrCode);
                payNotifyRepDTO.setChlRtnMsg(resErrCodeDes);
                payNotifyRepDTO.setTradeStatus(TradeStatus.UNKNOW);
                payNotifyRepDTO.setChlFinishTime(new Date());
                return payNotifyRepDTO;
            }
        } catch (Exception e) {
            log.error("[微信支付]-[支付通知]-[解析返回报文]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.UNKNOW);
        }
        return payNotifyRepDTO;
    }

    public static void main(String[] args) {
        String notifyMsg = "<xml><appid><![CDATA[wx8be4aaecf78a1934]]></appid>" + "<attach><![CDATA[DEPOSIT]]></attach>"
                + "<bank_type><![CDATA[CFT]]></bank_type>" + "<cash_fee><![CDATA[1]]></cash_fee>"
                + "<fee_type><![CDATA[CNY]]></fee_type>" + "<is_subscribe><![CDATA[Y]]></is_subscribe>"
                + "<mch_id><![CDATA[1312747401]]></mch_id>"
                + "<nonce_str><![CDATA[wKeAvWGIhvzPaSHjgchuuGTn]]></nonce_str>"
                + "<openid><![CDATA[oSHr-jiWyC8BUV3GHMX_azl03c3g]]></openid>"
                + "<out_trade_no><![CDATA[1785874126]]></out_trade_no>"
                + "<result_code><![CDATA[SUCCESS]]></result_code>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                + "<sign><![CDATA[3A8B6622C7264CF1D1AD2CE984E860CF]]></sign>"
                + "<time_end><![CDATA[20160824114222]]></time_end>" + "<total_fee>1</total_fee>"
                + "<trade_type><![CDATA[NATIVE]]></trade_type>"
                + "<transaction_id><![CDATA[4001732001201608242104966123]]></transaction_id>" + "</xml>";
        Map<String, Object> resultMap = XmlUtil.fromXml(notifyMsg);
        Map<String, String> paramMap = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            paramMap.put(entry.getKey(), (String) entry.getValue());
        }
        String localSign = WechatpayUtil.createSign(paramMap, SignType.valueOf(StringUtils.trim("MD5")),
                "0e2RNDgJFn3tyfiQQvIlC0ys47YmiGlX", Charset.forName(StringUtils.trim("UTF-8")));
        System.out.println("localSign:" + localSign);
        System.out.println("sign:" + resultMap.get("sign"));
        System.out.println(localSign.equals(resultMap.get("sign")));
    }

}

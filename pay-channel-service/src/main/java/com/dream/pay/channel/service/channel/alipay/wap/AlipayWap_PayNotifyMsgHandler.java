package com.dream.pay.channel.service.channel.alipay.wap;

import com.dream.pay.channel.access.dto.PayNotifyRepDTO;
import com.dream.pay.channel.access.dto.PayNotifyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.channel.alipay.AlipayUtil;
import com.dream.pay.channel.service.channel.alipay.Alipay_ChannelConfig;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.UrlChannelMsgHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 支付通知报文解析处理类
 */
@Slf4j
public class AlipayWap_PayNotifyMsgHandler extends UrlChannelMsgHandler<PayNotifyReqDTO, PayNotifyRepDTO> {

    @Override
    public PayNotifyRepDTO resolveCallBackMsg(PayNotifyReqDTO req, ChannelConfig channelConfig)
            throws ChannelMsgException {
        PayNotifyRepDTO payNotifyRepDTO = new PayNotifyRepDTO();
        Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
        Map<String, String> returnMap = req.getExt();
        // 数据完整性验证－－非空验证
        if (returnMap == null || returnMap.size() == 0) {
            log.error("[支付宝无线]-[支付通知]-[解析通知报文]-[返回报文数据为空]-[{}]", returnMap);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
        }
        try {
            // 数据完整性验证－－签名验证
            boolean checkSignFlag = PropUtil.getBoolean("alipayWap.checkSign.flag", true);
            if (checkSignFlag) {
                log.info("[支付宝无线]-[支付通知]-[解析通知报文]-[验签开始]-[{}][{}]", config.getSignKey(),
                        StringUtils.trim(config.getCharset()));
                String localSign = AlipayUtil.createWapSign(returnMap, DESUtil.decryptModeBase64(config.getSignKey()),
                        Charset.forName(StringUtils.trim(config.getCharset())));
                boolean signFlag = localSign.equals(returnMap.get("sign"));
                if (!signFlag) {
                    log.error("[支付宝无线]-[支付通知]-[解析通知报文]-[验签失败]-[{}]!=[{}]", returnMap.get("sign"), localSign);
                    payNotifyRepDTO.setTradeStatus(TradeStatus.FAIL);
                    payNotifyRepDTO.setChlRtnMsg("验签失败[" + returnMap.get("sign") + "!=" + localSign + "]");
                    throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
                }
            }
            // 拍卖的支付申请接口较老，通知接口也较老，带notify_data的表示拍卖的逻辑，此处为了兼容，因为主站切到新接口，使用同一个支付方式同一个通知地址
            if (null != returnMap.get("notify_data")) {
                Map<String, Object> stringObjectMap = XmlUtil.fromXml(returnMap.get("notify_data"));
                Map<String, String> notifyDataMap = BeanUtil.convertMap(stringObjectMap, String.class);
                makPayNotifyRepDTO(payNotifyRepDTO, notifyDataMap);
            } else {
                // 主站的通知解析
                makPayNotifyRepDTO(payNotifyRepDTO, returnMap);
            }
        } catch (Exception e) {
            log.error("[支付宝无线]-[支付通知]-[解析通知报文]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
        }
        return payNotifyRepDTO;
    }

    private void makPayNotifyRepDTO(PayNotifyRepDTO payNotifyRepDTO, Map<String, String> returnMap) {
        String outTradeNo = returnMap.get("out_trade_no");// 商户订单号(上层系统)
        if (StringUtils.isNotBlank(outTradeNo)) {// 设置商户订单号
            payNotifyRepDTO.setPayDetailNo(outTradeNo);
        }
        String tradeNo = returnMap.get("trade_no");// 银行返回流水号(下层系统)
        if (StringUtils.isNotBlank(tradeNo)) {// 设置银行返回的支付单号
            payNotifyRepDTO.setBankPayDetailNo(tradeNo);
        }
        String totalFee = returnMap.get("total_fee");// 支付金额
        if (StringUtils.isNotBlank(totalFee)) {// 设置支付金额
            payNotifyRepDTO.setPayAmount(new BigDecimal(totalFee));
        }
        String gmtPayment = returnMap.get("gmt_payment");// 交易完成时间
        if (StringUtils.isNotBlank(gmtPayment)) {// 设置支付完成时间
            payNotifyRepDTO.setChlFinishTime(new Date());// 渠道支付完成时间
            payNotifyRepDTO.setBankFinishTime(DateUtil.StringToDateTime(gmtPayment));// 银行支付完成时间
        }
        String partner = returnMap.get("seller_id");// 收款帐号
        payNotifyRepDTO.setMerchantNo(partner);
        String buyerId = returnMap.get("buyer_id");// 付款帐号
        payNotifyRepDTO.setUserNo(buyerId);
        // 设置状态码和状态信息
        confirmRtnMsg(payNotifyRepDTO, returnMap);
    }

    private void confirmRtnMsg(PayNotifyRepDTO payNotifyRepDTO, Map<String, String> returnMap) {
        String tradeStatus = returnMap.get("trade_status");// 交易状态
        payNotifyRepDTO.setChlRtnCode(tradeStatus);
        payNotifyRepDTO.setTradeStatus(AlipayUtil.toTradeStatusEnum(tradeStatus));
        payNotifyRepDTO.setChlRtnMsg(AlipayUtil.toTradeDesc(tradeStatus));
        log.info("[支付宝无线]-[支付通知]-[解析通知报文]-交易状态为[{}],状态描述位[{}]", AlipayUtil.toTradeStatusEnum(tradeStatus),
                AlipayUtil.toTradeStatusEnum(tradeStatus));
    }

    public static void main(String[] args) {
        Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put("sign", "d45a713e9a997165bd14f7e74f0996ee");
        infoMap.put("v", "1.0");
        infoMap.put("sec_id", "MD5");
        infoMap.put("notify_data",
                "<notify><payment_type>1</payment_type><subject>当当网商品</subject><trade_no>2016091421001004340244399360</trade_no><buyer_email>13810299180</buyer_email><gmt_create>2016-09-14 16:11:15</gmt_create><notify_type>trade_status_sync</notify_type><quantity>1</quantity><out_trade_no>170000000005</out_trade_no><notify_time>2016-09-14 16:11:16</notify_time><seller_id>2088421631345065</seller_id><trade_status>TRADE_SUCCESS</trade_status><is_total_fee_adjust>N</is_total_fee_adjust><total_fee>0.01</total_fee><gmt_payment>2016-09-14 16:11:16</gmt_payment><seller_email>zhifubao-pm@dangdang.com</seller_email><price>0.01</price><buyer_id>2088712510212344</buyer_id><notify_id>c0c616fcd70f9dfe5b23ac1c9374d6fimi</notify_id><use_coupon>N</use_coupon></notify>");
        infoMap.put("service", "alipay.wap.trade.create.direct");
        Map<String, Object> stringObjectMap = XmlUtil.fromXml(infoMap.get("notify_data"));
        Map<String, String> notifyDataMap = BeanUtil.convertMap(stringObjectMap, String.class);
        String localSign = AlipayUtil.createWapSign(infoMap, "4zm0d2l74ur6wzubxsrx9nz4hxhhdutu",
                Charset.forName("UTF-8"));
        System.out.println("localSign=" + localSign);
        System.out.println("remoteSign=" + infoMap.get("sign"));
        System.out.println("商户订单号(上层系统):" + notifyDataMap.get("out_trade_no"));// 商户订单号(上层系统)
        System.out.println("银行返回流水号(下层系统):" + notifyDataMap.get("trade_no"));// 银行返回流水号(下层系统)
        System.out.println("支付金额:" + notifyDataMap.get("total_fee"));// 支付金额
        System.out.println("交易完成时间:" + notifyDataMap.get("gmt_payment"));// 交易完成时间
        System.out.println("收款帐号:" + notifyDataMap.get("seller_id"));// 收款帐号
        System.out.println("付款帐号:" + notifyDataMap.get("buyer_id"));// 付款帐号
        System.out.println("交易状态:" + notifyDataMap.get("trade_status"));//交易状态

        System.out.println("=============上为拍卖通知，下为主站通知=================================");
        Map<String, String> newInfoMap = new HashMap<String, String>();
        newInfoMap.put("buyer_id", "2088702601704434");
        newInfoMap.put("trade_no", "2016120621001004430275476459");
        newInfoMap.put("body", "MAIN");
        newInfoMap.put("use_coupon", "N");
        newInfoMap.put("notify_time", "2016-12-06 10:43:26");
        newInfoMap.put("subject", "当当订单");
        newInfoMap.put("sign_type", "MD5");
        newInfoMap.put("is_total_fee_adjust", "N");
        newInfoMap.put("notify_type", "trade_status_sync");
        newInfoMap.put("out_trade_no", "172016126104153");
        newInfoMap.put("gmt_payment", "2016-12-06 10:43:26");
        newInfoMap.put("trade_status", "TRADE_SUCCESS");
        newInfoMap.put("sign", "f585b90b9e123fbd82af323d5f9a43af");
        newInfoMap.put("buyer_email", "18611073586");
        newInfoMap.put("gmt_create", "2016-12-06 10:43:25");
        newInfoMap.put("price", "0.01");
        newInfoMap.put("total_fee", "0.01");
        newInfoMap.put("seller_id", "2088901524471441");
        newInfoMap.put("quantity", "1");
        newInfoMap.put("notify_id", "879a3f7dda49a32e302ef0119c8786djbi");
        newInfoMap.put("seller_email", "zhifubao-wx@dangdang.com");
        newInfoMap.put("payment_type", "1");
        System.out.println(newInfoMap.get("notify_data"));
        String localSignNew = AlipayUtil.createSign(newInfoMap, SignType.MD5, "qklte622ej1peh6t3x1zcv5s7irx23fd",
                Charset.forName("UTF-8"));
        System.out.println("localSign=" + localSignNew);
        System.out.println("remoteSign=" + newInfoMap.get("sign"));
        System.out.println("商户订单号(上层系统):" + newInfoMap.get("out_trade_no"));// 商户订单号(上层系统)
        System.out.println("银行返回流水号(下层系统):" + newInfoMap.get("trade_no"));// 银行返回流水号(下层系统)
        System.out.println("支付金额:" + newInfoMap.get("total_fee"));// 支付金额
        System.out.println("交易完成时间:" + newInfoMap.get("gmt_payment"));// 交易完成时间
        System.out.println("收款帐号:" + newInfoMap.get("seller_id"));// 收款帐号
        System.out.println("付款帐号:" + newInfoMap.get("buyer_id"));// 付款帐号
        System.out.println("交易状态:" + newInfoMap.get("trade_status"));//交易状态
    }
}

package com.dream.pay.channel.service.channel.alipay.std;

import com.dream.pay.channel.access.dto.PayNotifyRepDTO;
import com.dream.pay.channel.access.dto.PayNotifyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.channel.alipay.AlipayUtil;
import com.dream.pay.channel.service.channel.alipay.Alipay_ChannelConfig;
import com.dream.pay.channel.service.convert.BaseRepConvert;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.UrlChannelMsgHandler;
import com.dream.pay.enums.PayTool;
import com.dream.pay.utils.DESUtil;
import com.dream.pay.utils.DateUtil;
import com.dream.pay.utils.PropUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 支付通知报文解析处理类
 */
@Slf4j
public class AlipayNative_PayNotifyMsgHandler extends UrlChannelMsgHandler<PayNotifyReqDTO, PayNotifyRepDTO> {

    @Override
    public PayNotifyRepDTO resolveCallBackMsg(PayNotifyReqDTO req, ChannelConfig channelConfig)
            throws ChannelMsgException {
        PayNotifyRepDTO payNotifyRepDTO = new PayNotifyRepDTO();
        Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
        Map<String, String> returnMap = req.getExt();
        // 数据完整性验证－－非空验证
        if (returnMap == null || returnMap.size() == 0) {
            log.error("[支付宝]-[支付通知]-[解析通知报文]-[返回报文数据为空]-[{}]", returnMap);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        // 数据完整性验证－－签名验证
        boolean checkSignFlag = PropUtil.getBoolean("alipay.checkSign.flag", true);
        if (!checkSignFlag && req.isCheckSign()) {
            log.debug("[支付宝]-[支付通知]-[解析通知报文]-[验签开始]-[{}][{}]", config.getSignKey(),
                    StringUtils.trim(config.getCharset()));
            String localSign = AlipayUtil.createSign(returnMap, SignType.valueOf(config.getSignType()),
                    DESUtil.decryptModeBase64(config.getSignKey()),
                    Charset.forName(StringUtils.trim(config.getCharset())));
            boolean signFlag = localSign.equals(returnMap.get("sign"));
            if (!signFlag) {
                log.error("[支付宝]-[支付通知]-[解析通知报文]-[验签失败]-[{}]!=[{}]", returnMap.get("sign"), localSign);
                throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
            }
        }
        buildPayNotifyRepDTO(payNotifyRepDTO, returnMap);
        return payNotifyRepDTO;
    }

    private void buildPayNotifyRepDTO(PayNotifyRepDTO payNotifyRepDTO, Map<String, String> returnMap) {
        String outTradeNo = returnMap.get("out_trade_no");// 商户订单号(上层系统)
        payNotifyRepDTO.setPayDetailNo(outTradeNo);

        String tradeNo = returnMap.get("trade_no");// 银行返回流水号(下层系统)
        payNotifyRepDTO.setBankPayDetailNo(tradeNo);
        payNotifyRepDTO.setBankPayDetailNo(tradeNo);

        String totalFee = returnMap.get("total_fee");// 支付金额
        if (StringUtils.isNotBlank(totalFee)) {// 设置支付金额
            payNotifyRepDTO.setPayAmount(new BigDecimal(totalFee));
        }
        String gmtPayment = returnMap.get("gmt_payment");// 交易完成时间
        if (StringUtils.isNotBlank(gmtPayment)) {// 设置支付完成时间
            payNotifyRepDTO.setBankFinishTime(DateUtil.StringToDateTime(gmtPayment));// 银行支付完成时间
        }
        String partner = returnMap.get("seller_id");// 收款帐号
        payNotifyRepDTO.setMerchantNo(partner);
        String buyerId = returnMap.get("buyer_email");// 付款帐号
        payNotifyRepDTO.setUserNo(buyerId);
        // 设置状态码和状态信息
        String tradeStatus = returnMap.get("trade_status");// 交易状态
        payNotifyRepDTO.setTradeStatus(BaseRepConvert.convertTradeStatus(PayTool.ALIPAY_NATIVE, tradeStatus));
        payNotifyRepDTO.setChlRtnCode(tradeStatus);// 直接透传支付宝返回code
        payNotifyRepDTO.setChlRtnMsg(BaseRepConvert.convertChlRetMsg(PayTool.ALIPAY_NATIVE, tradeStatus));
        payNotifyRepDTO.setResponseBody("SUCCESS");
    }

    public static void main(String[] args) {
        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put("buyer_id", "2088302015168622");
        returnMap.put("trade_no", "2016091821001004620263569899");
        returnMap.put("body", "DEPOSIT");
        returnMap.put("use_coupon", "N");
        returnMap.put("notify_time", "2016-09-18 15:26:10");
        returnMap.put("subject", "当当订单");
        returnMap.put("sign_type", "MD5");
        returnMap.put("is_total_fee_adjust", "N");
        returnMap.put("notify_type", "trade_status_sync");
        returnMap.put("out_trade_no", "020151151102439000020253587");
        returnMap.put("gmt_payment", "2016-09-18 15:12:22");
        returnMap.put("trade_status", "TRADE_SUCCESS");
        returnMap.put("discount", "0.00");
        returnMap.put("sign", "0feaedff811cb488fae6dacde29720f4");
        returnMap.put("buyer_email", "zijinghuaff@126.com");
        returnMap.put("gmt_create", "2016-09-18 15:11:49");
        returnMap.put("price", "0.10");
        returnMap.put("total_fee", "0.10");
        returnMap.put("quantity", "1");
        returnMap.put("seller_id", "2088421631345065");
        returnMap.put("notify_id", "e3f6876b64202397055ef6f8c5c92d8ksa");
        returnMap.put("seller_email", "zhifubao-pm@dangdang.com");
        returnMap.put("payment_type", "1");

        String localSign = AlipayUtil.createSign(returnMap, SignType.valueOf("MD5"), "4zm0d2l74ur6wzubxsrx9nz4hxhhdutu",
                Charset.forName(StringUtils.trim("utf-8")));
        System.out.println(localSign);
    }
}

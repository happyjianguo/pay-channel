package com.dream.pay.channel.service.channel.wechat.pub;

import com.dream.pay.channel.access.dto.ExtendParamKey;
import com.dream.pay.channel.access.dto.PayApplyRepDTO;
import com.dream.pay.channel.access.dto.PayApplyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.channel.wechat.WechatpayUtil;
import com.dream.pay.channel.service.channel.wechat.Wechatpay_ChannelConfig;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.XMLChannelMsgHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 支付申请报文解析处理类
 */
@Slf4j
public class WechatPubPay_PayApplyMsgHandler extends XMLChannelMsgHandler<PayApplyReqDTO, PayApplyRepDTO> {

    @Override
    public PayApplyReqDTO beforBuildMsg(PayApplyReqDTO req, ChannelConfig channelConfig) throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        try {
            Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
            // 必输入参数：appid，mch_id，nonce_str，body，out_trade_no，total_fee，notify_url，trade_type
            super.setParam("appid", config.getAppId());// 公众账号ID
            super.setParam("mch_id", config.getMerchantNo());// 商户号
            super.setParam("nonce_str", StringUtils.trim(RandomStringUtils.randomAlphabetic(24)));// 随机字符串
            super.setParam("body", "当当订单");// 商品描述
            super.setParam("attach", req.getPartnerId().getCode());
            super.setParam("out_trade_no", req.getPayDetailNo());// 业务订单号
            DecimalFormat format = new DecimalFormat("#0");
            super.setParam("total_fee", format.format(req.getPayAmount().multiply(new BigDecimal(100))));// 订单金额以分为单位
            super.setParam("spbill_create_ip", StringUtils.trim(req.getUserIp()));// 请求IP
            super.setParam("notify_url", StringUtils.trim(String.valueOf(config.getPayNotifyUrl())));// 回调通知URL
            super.setParam("trade_type", "JSAPI");// 统一下单接口trade_type:交易类型JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付
            super.setParam("time_start", DateUtil.DateStampToStringMs(req.getReqDateTime()));// 交易起始时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(req.getReqDateTime());
            calendar.add(Calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
            super.setParam("time_expire", DateUtil.DateStampToStringMs(calendar.getTime()));// 交易结束时间
            if (null != req.getExt() && null != req.getExt().get(ExtendParamKey.OPEN_ID)) {// 用户标示
                String openId = req.getExt().get(ExtendParamKey.OPEN_ID);
                super.setParam("openid", openId);// 用户在商户appid下的唯一标识。
            } else {
                throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
            }
            String sign = WechatpayUtil.createSign(super.getParamMap(),
                    SignType.valueOf(StringUtils.trim(config.getSignType())),
                    DESUtil.decryptModeBase64(config.getSignKey()),
                    Charset.forName(StringUtils.trim(config.getCharset())));
            super.setParam("sign", sign);
            req.setTradeType(TradeType.PAY_APPLY);
        } catch (Exception e) {
            log.error("[微信公帐]-[支付申请]-[报文拼装前设置参数]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return req;
    }

    @SuppressWarnings("deprecation")
    @Override
    public PayApplyRepDTO resolveMsg(PayApplyReqDTO req, byte[] rtnMsg, ChannelConfig channelConfig)
            throws ChannelMsgException {
        Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
        PayApplyRepDTO payApplyRepDTO = new PayApplyRepDTO();
        try {
            String repString = StringUtils.toString(rtnMsg, StringUtils.trim(config.getCharset()));
            Map<String, Object> resultMap = XmlUtil.fromXml(repString);
            String resReturnCode = (String) resultMap.get("return_code");// 通信标识
            if (!resReturnCode.equals("SUCCESS")) {
                String resReturnMsg = (String) resultMap.get("return_msg");
                payApplyRepDTO.setChlRtnCode(resReturnCode);
                payApplyRepDTO.setChlRtnMsg(resReturnMsg);
                payApplyRepDTO.setTradeStatus(TradeStatus.FAIL);
                log.error("[微信公帐]-[支付申请]-[解析返回报文]-[通信标示码不为成功[{}],返回信息为[{}]]", resReturnCode, resReturnMsg);
                return payApplyRepDTO;
            }
            // 验证签名
            Map<String, String> paramMap = BeanUtil.convertMap(resultMap, String.class);
            String localSign = WechatpayUtil.createSign(paramMap,
                    SignType.valueOf(StringUtils.trim(config.getSignType())),
                    DESUtil.decryptModeBase64(config.getSignKey()),
                    Charset.forName(StringUtils.trim(config.getCharset())));
            boolean signFlag = localSign.equals(resultMap.get("sign"));
            if (!signFlag) {
                log.error("[微信公帐]-[支付申请]-[解析返回报文]-[验签失败]-[{}]!=[{}]", resultMap.get("sign"), localSign);
                throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
            }
            String resResultCode = (String) resultMap.get("result_code");
            if (resResultCode.equals("SUCCESS")) {
                payApplyRepDTO.setChlRtnCode(TradeStatus.SUCCESS.name());
                payApplyRepDTO.setChlRtnMsg(TradeStatus.SUCCESS.getValue());
                payApplyRepDTO.setTradeStatus(TradeStatus.SUCCESS);
                // 封装传递给客户端的参数
                String repContent = buildRepContent(config, resultMap);
                payApplyRepDTO.setRepContent(repContent);
            } else {
                String resErrCode = (String) resultMap.get("err_code");
                String resErrCodeDes = (String) resultMap.get("err_code_des");
                payApplyRepDTO.setChlRtnCode(resErrCode);
                payApplyRepDTO.setChlRtnMsg(resErrCodeDes);
                payApplyRepDTO.setTradeStatus(TradeStatus.FAIL);
                return payApplyRepDTO;
            }
        } catch (Exception e) {
            log.error("[微信公帐]-[支付申请]-[解析返回报文]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return payApplyRepDTO;
    }

    private String buildRepContent(Wechatpay_ChannelConfig config, Map<String, Object> resultMap) {
        String nonceStr = StringUtils.trim(RandomStringUtils.randomAlphabetic(24));// 随机字符串
        long timeStamp = Long.parseLong(String.valueOf(System.currentTimeMillis()).toString().substring(0, 10));// 当前时间戳
        Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put("appId", (String) resultMap.get("appid"));
        infoMap.put("timeStamp", String.valueOf(timeStamp));
        infoMap.put("nonceStr", nonceStr);
        infoMap.put("package", "prepay_id=" + resultMap.get("prepay_id"));
        infoMap.put("signType", config.getSignType());
        String sign = WechatpayUtil.createSign(infoMap, SignType.valueOf(StringUtils.trim(config.getSignType())),
                DESUtil.decryptModeBase64(config.getSignKey()), Charset.forName(StringUtils.trim(config.getCharset())));
        infoMap.put("paySign", sign);
        String repContent = JsonUtil.toJson(infoMap);
        return repContent;
    }

    public static void main(String[] args) {
        String nonceStr = StringUtils.trim(RandomStringUtils.randomAlphabetic(24));// 随机字符串
        long timeStamp = Long.parseLong(String.valueOf(System.currentTimeMillis()).toString().substring(0, 10));// 当前时间戳
        Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put("appid", "wx8888888888888888");
        infoMap.put("timeStamp", String.valueOf(timeStamp));
        infoMap.put("nonceStr", nonceStr);
        infoMap.put("package", "prepay_id=123456789");

        infoMap.put("signType", "MD5");
        infoMap.put("paySign", "C380BEC2BFD727A4B6845133519F3AD6");
        String repContent = JsonUtil.toJson(infoMap);
        System.out.println(repContent);
    }
}

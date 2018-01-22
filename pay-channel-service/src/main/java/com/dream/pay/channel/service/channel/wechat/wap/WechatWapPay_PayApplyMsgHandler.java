package com.dream.pay.channel.service.channel.wechat.wap;

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
import com.dream.pay.enums.BizChannelEnum;
import com.dream.pay.enums.PayTool;
import com.dream.pay.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * @Author huangbojs
 */
@Slf4j
public class WechatWapPay_PayApplyMsgHandler extends XMLChannelMsgHandler<PayApplyReqDTO, PayApplyRepDTO> {

    @Override
    public PayApplyReqDTO beforBuildMsg(PayApplyReqDTO req, ChannelConfig channelConfig) throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        try {
            Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
            // 必输入参数：appid，mch_id，nonce_str，body，out_trade_no，total_fee，notify_url，trade_type
            super.setParam("appid", config.getAppId());// 公众账号ID
            // log.info("=============================APP支付"+config.getMerchantNo()+"||"+config.getAppId());
            super.setParam("mch_id", config.getMerchantNo());// 商户号
            super.setParam("nonce_str", StringUtils.trim(RandomStringUtils.randomAlphabetic(24)));// 随机字符串
            super.setParam("body", PropUtil.get("wechatPay.productName"));// 商品描述
            super.setParam("attach", req.getPartnerId().getCode());
            super.setParam("out_trade_no", req.getPayDetailNo());// 业务订单号
            DecimalFormat format = new DecimalFormat("#0");
            super.setParam("total_fee", format.format(req.getPayAmount().multiply(new BigDecimal(100))));// 订单金额以分为单位
            super.setParam("spbill_create_ip", req.getExt().get(ExtendParamKey.SPBILL_CREATE_IP));// 请求IP
            super.setParam("notify_url", StringUtils.trim(String.valueOf(config.getPayNotifyUrl())));// 回调通知URL
            super.setParam("trade_type", "MWEB");// 统一下单接口trade_type:交易类型JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付, MWEB-wap
            super.setParam("time_start", DateUtil.DateStampToStringMs(req.getReqDateTime()));// 交易起始时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(req.getReqDateTime());
            calendar.add(Calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
            super.setParam("time_expire", DateUtil.DateStampToStringMs(calendar.getTime()));// 交易结束时间
            String sign = WechatpayUtil.createSign(super.getParamMap(),
                    SignType.valueOf(StringUtils.trim(config.getSignType())),
                    DESUtil.decryptModeBase64(config.getSignKey()),
                    Charset.forName(StringUtils.trim(config.getCharset())));
            super.setParam("sign", sign);
            req.setTradeType(TradeType.PAY_APPLY);
        } catch (Exception e) {
            log.error("[微信H5]-[支付申请]-[报文拼装前设置参数]-[出现异常]", e);
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
        payApplyRepDTO.setPayType(PayTool.WX_H5);
        payApplyRepDTO.setBizChannel(BizChannelEnum.WX);
        try {
            String repString = StringUtils.toString(rtnMsg, StringUtils.trim(config.getCharset()));
            Map<String, Object> resultMap = XmlUtil.fromXml(repString);
            String resReturnCode = (String) resultMap.get("return_code");// 通信标识
            if (!resReturnCode.equals("SUCCESS")) {
                String resReturnMsg = (String) resultMap.get("return_msg");
                payApplyRepDTO.setChlRtnCode(resReturnCode);
                payApplyRepDTO.setChlRtnMsg(resReturnMsg);
                payApplyRepDTO.setTradeStatus(TradeStatus.FAIL);
                log.error("[微信H5]-[支付申请]-[解析返回报文]-[通信标示码不为成功[{}],返回信息为[{}]]", resReturnCode, resReturnMsg);
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
                log.error("[微信H5]-[支付申请]-[解析返回报文]-[验签失败]-[{}]!=[{}]", resultMap.get("sign"), localSign);
                throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.PROCESS);
            }
            String resResultCode = (String) resultMap.get("result_code");
            if (resResultCode.equals("SUCCESS")) {
                payApplyRepDTO.setPayDetailNo(req.getPayDetailNo());
                payApplyRepDTO.setPayAmount(req.getPayAmount());
                payApplyRepDTO.setChlRtnCode(TradeStatus.SUCCESS.name());
                payApplyRepDTO.setChlRtnMsg(TradeStatus.SUCCESS.getValue());
                payApplyRepDTO.setTradeStatus(TradeStatus.SUCCESS);
                // 封装传递给客户端的参数
                String mweb_url = (String) resultMap.get("mweb_url");
                payApplyRepDTO.setRepContent(mweb_url);
            } else {
                String resErrCode = (String) resultMap.get("err_code");
                String resErrCodeDes = (String) resultMap.get("err_code_des");
                payApplyRepDTO.setChlRtnCode(resErrCode);
                payApplyRepDTO.setChlRtnMsg(resErrCodeDes);
                payApplyRepDTO.setTradeStatus(TradeStatus.FAIL);
                return payApplyRepDTO;
            }
        } catch (Exception e) {
            log.error("[微信H5]-[支付申请]-[解析返回报文]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return payApplyRepDTO;
    }
}

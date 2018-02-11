package com.dream.pay.channel.service.channel.alipay;

import com.dream.pay.channel.access.dto.PayQueryRepDTO;
import com.dream.pay.channel.access.dto.PayQueryReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.convert.BaseRepConvert;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.UrlChannelMsgHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.enums.PayTool;
import com.dream.pay.utils.DESUtil;
import com.dream.pay.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 支付查询报文解析处理类
 */
@Slf4j
public class Alipay_PayQueryMsgHandler extends UrlChannelMsgHandler<PayQueryReqDTO, PayQueryRepDTO> {

    @Override
    public PayQueryReqDTO beforBuildMsg(PayQueryReqDTO req, ChannelConfig channelConfig) throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        try {
            Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
            super.setParam("service", config.getPayQueryService());
            super.setParam("partner", config.getPartner());
            super.setParam("_input_charset", config.getCharset());
            super.setParam("out_trade_no", StringUtils.trim(req.getPayDetailNo()));
            String sign = AlipayUtil.createSign(super.getParamMap(), SignType.valueOf(config.getSignType()),
                    DESUtil.decryptModeBase64(config.getSignKey()), Charset.forName(config.getCharset()));
            super.setParam("sign_type", StringUtils.trim(config.getSignType()));
            super.setParam("sign", sign);
        } catch (Exception e) {
            log.error("[支付宝]-[支付查询]-[报文拼装前设置参数]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return req;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public PayQueryRepDTO resolveMsg(PayQueryReqDTO req, byte[] rtnMsg, ChannelConfig channelConfig)
            throws ChannelMsgException {
        Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
        PayQueryRepDTO payQueryRepDTO = new PayQueryRepDTO();
        try {
            String repString = StringUtils.toString(rtnMsg, config.getCharset());
            log.info("[支付宝]-[支付查询]-[报文解析]－返回报文[{}]", repString);
            // 报文转换为map集合
            Map<String, Object> map = AlipayUtil.getMapFromXmlStr(repString);
            String isSuccess = (String) map.get("is_success");
            if ("T".equals(isSuccess)) {
                Map<String, String> childMap = (Map<String, String>) map.get("listItem");
                buildPayQueryRepDto(payQueryRepDTO, childMap);
                return payQueryRepDTO;
            } else {
                log.error("[支付宝]-[支付查询]-[报文解析]－[is_success]=[{}]", isSuccess);
                payQueryRepDTO.setTradeStatus(TradeStatus.FAIL);
                payQueryRepDTO.setChlRtnCode(TradeStatus.FAIL.name());
                payQueryRepDTO.setChlRtnMsg(
                        null != map.get("error") ? String.valueOf(map.get("error")) : TradeStatus.UNKNOW.name());
                return payQueryRepDTO;
            }
        } catch (Exception e) {
            log.info("[支付宝]-[支付查询]-[报文解析]－[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.UNKNOW);
        }

    }

    private void buildPayQueryRepDto(PayQueryRepDTO payQueryRepDTO, Map<String, String> childMap) {
        if (null != childMap) {
            // 设置返回参数
            String trade_status = (String) childMap.get("trade_status");
            log.info("[支付宝]-[支付查询]-[报文解析]-[trade_status]=[{}]", trade_status);
            String out_trade_no = childMap.get("out_trade_no");// 业务订单号
            payQueryRepDTO.setPayDetailNo(out_trade_no);
            String trade_no = childMap.get("trade_no");// 支付宝交易流水号
            payQueryRepDTO.setBankPayDetailNo(trade_no);
            payQueryRepDTO.setBankPayDetailNo(trade_no);
            String total_fee = childMap.get("total_fee");// 支付金额
            if (StringUtils.isNotBlank(total_fee)) {
                payQueryRepDTO.setPayAmount(new BigDecimal(total_fee));
            }
            String gmt_payment = childMap.get("gmt_payment");// 交易完成时间
            if (StringUtils.isNotBlank(gmt_payment)) {
                payQueryRepDTO.setBankFinishTime(DateUtil.StringToDefaultDate(gmt_payment));// 银行支付完成时间
            }
            String partner = childMap.get("seller_id");// 收款帐号
            payQueryRepDTO.setMerchantNo(partner);
            String buyerId = childMap.get("buyer_email");// 付款帐号
            payQueryRepDTO.setUserNo(buyerId);
            // 设置状态码和状态信息
            payQueryRepDTO.setTradeStatus(BaseRepConvert.convertTradeStatus(PayTool.ALIPAY_NATIVE, trade_status));
            payQueryRepDTO.setChlRtnCode(trade_status);
            payQueryRepDTO.setChlRtnMsg(BaseRepConvert.convertChlRetMsg(PayTool.ALIPAY_NATIVE, trade_status));
        } else {
            payQueryRepDTO.setTradeStatus(TradeStatus.UNKNOW);
            payQueryRepDTO.setChlRtnCode(TradeStatus.UNKNOW.name());
            payQueryRepDTO.setChlRtnMsg("状态未知");
        }
    }
}

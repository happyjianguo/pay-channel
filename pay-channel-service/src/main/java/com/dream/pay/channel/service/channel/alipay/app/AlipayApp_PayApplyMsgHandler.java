package com.dream.pay.channel.service.channel.alipay.app;

import com.dream.pay.channel.access.dto.PayApplyRepDTO;
import com.dream.pay.channel.access.dto.PayApplyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.channel.alipay.AlipayConfigContants;
import com.dream.pay.channel.service.channel.alipay.AlipayUtil;
import com.dream.pay.channel.service.channel.alipay.Alipay_ChannelConfig;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.UrlChannelMsgHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.utils.DESUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

/**
 * @author Created by mengzhenbin on 2016/07/12
 * @remark 支付申请报文解析处理类
 */
@Slf4j
public class AlipayApp_PayApplyMsgHandler extends UrlChannelMsgHandler<PayApplyReqDTO, PayApplyRepDTO> {

    @Override
    public PayApplyReqDTO beforBuildMsg(PayApplyReqDTO req, ChannelConfig channelConfig) throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        try {
            Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
            // 添加接口参数
            super.setParam("partner", StringUtils.trim(config.getPartner()));// 签约的支付宝账号对应的的支付宝唯一用户号（2088开头16位纯数字）
            super.setParam("seller_id", StringUtils.trim(config.getPartner()));// 商户收款支付账号，seller_id，seller_email，seller_account_name必输其一
            // 添加业务线等订单信息
            super.setParam("out_trade_no", StringUtils.trim(req.getPayDetailNo())); // 商户订单号，商户网站订单系统中唯一订单号，必填
            super.setParam("subject", AlipayConfigContants.SUBJECT_VALUE);// 商品名称
            super.setParam("body", req.getPartnerId().getCode());// 传递各业务线的编码
            DecimalFormat format = new DecimalFormat(AlipayConfigContants.DECIMAL_FORMAT);
            super.setParam("total_fee", format.format(req.getPayAmount()));// 订单金额以元为单位
            super.setParam("notify_url", StringUtils.trim(config.getPayNotifyUrl()));// 服务器异步通知页面路径
            super.setParam("service", StringUtils.trim(config.getAppPayApplyService()));// 接口名称
            super.setParam("payment_type", StringUtils.trim(config.getPaymentType()));// 支付类型固定值1
            super.setParam("_input_charset", StringUtils.trim(config.getCharset()));// 参数编码字符集
            super.setParam("return_url", StringUtils.trim(config.getPayReturnUrl()));// 页面跳转同步通知页面路径
            String sign = AlipayUtil.createSign(super.getParamMap(), SignType.valueOf(config.getSignType()),
                    DESUtil.decryptModeBase64(config.getSignKey(),""), Charset.forName(config.getCharset()));
            super.setParam("sign", sign);
            super.setParam("sign_type", StringUtils.trim(config.getSignType()));
        } catch (Exception e) {
            log.error("[支付宝WAP]-[支付申请]-[报文拼装前设置参数]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return req;
    }

    @Override
    public PayApplyRepDTO resolveMsg(PayApplyReqDTO req, byte[] rtnMsg, ChannelConfig channelConfig)
            throws ChannelMsgException {
        PayApplyRepDTO payApplyRepDTO = new PayApplyRepDTO();
        try {
            String repString = new String(rtnMsg);
            if (StringUtils.isNotBlank(repString)) {
                payApplyRepDTO.setChlRtnCode(ChannelRtnCodeEnum.SUCCESS.name());
                payApplyRepDTO.setChlRtnMsg(ChannelRtnCodeEnum.SUCCESS.getMessage());
                payApplyRepDTO.setTradeStatus(TradeStatus.SUCCESS);
            } else {
                payApplyRepDTO.setChlRtnCode(ChannelRtnCodeEnum.M10000.name());
                payApplyRepDTO.setChlRtnMsg(ChannelRtnCodeEnum.M10000.getMessage());
                payApplyRepDTO.setTradeStatus(TradeStatus.FAIL);
            }
            payApplyRepDTO.setRepContent(repString); // 支付写出的ftl
            payApplyRepDTO.setPayDetailNo(req.getPayDetailNo());// 支付单号
            payApplyRepDTO.setPayAmount(
                    new BigDecimal(new DecimalFormat(AlipayConfigContants.DECIMAL_FORMAT).format(req.getPayAmount())));// 支付金额
        } catch (Exception e) {
            log.error("[支付宝WAP]-[支付申请]-[报文解析]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return payApplyRepDTO;
    }
}

package com.dream.pay.channel.service.channel.alipay;

import com.dream.pay.channel.access.dto.RefundApplyRepDTO;
import com.dream.pay.channel.access.dto.RefundApplyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.convert.BaseRepConvert;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.impl.UrlChannelMsgHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.enums.PayTool;
import com.dream.pay.utils.DESUtil;
import com.dream.pay.utils.DateUtil;
import com.dream.pay.utils.ParamUtil;
import com.youzan.platform.util.lang.StringUtil;
import lombok.extern.slf4j.Slf4j;
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
public class Alipay_RefundApplyMsgHandler extends UrlChannelMsgHandler<RefundApplyReqDTO, RefundApplyRepDTO> {

    @Override
    public RefundApplyReqDTO beforBuildMsg(RefundApplyReqDTO req, ChannelConfig channelConfig)
            throws ChannelMsgException {
        req = super.beforBuildMsg(req, channelConfig);
        try {
            Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
            super.setParam("service", StringUtils.trim(config.getRefundApplyService()));// 接口名称
            super.setParam("partner", StringUtils.trim(config.getPartner()));// 合作者身份ID
            super.setParam("_input_charset", AlipayConfigContants.REFUND_CHARSET);// 参数编码字符集
            super.setParam("sign_type", StringUtils.trim(config.getSignType()));
            super.setParam("seller_email", StringUtils.trim(config.getSellerEmail()));// 卖家支付宝账号
            super.setParam("notify_url", StringUtils.trim(config.getRefundNotifyUrl()));// 服务器异步通知页面路径
            super.setParam("batch_no", StringUtils.trim(req.getRefundBatchNo()));// 退款批次号
            super.setParam("refund_date", DateUtil.DateToDefaultString(req.getReqDateTime()));// 退款时间
            super.setParam("batch_num", "1");// 退款比数(detail_data中＃数量＋1)
            StringBuilder builder = new StringBuilder();
            DecimalFormat format = new DecimalFormat(AlipayConfigContants.DECIMAL_FORMAT);
            builder.append(req.getBankPayDetailNo()).append("^").append(format.format(req.getRefundAmount())).append("^")
                    .append("refund");
            super.setParam("detail_data", builder.toString());// 退款明细数据，2011011001034366^20.00^协商退款
            String sign = AlipayUtil.createSign(super.getParamMap(), SignType.valueOf(config.getSignType()),
                    DESUtil.decryptModeBase64(config.getSignKey()), Charset.forName(AlipayConfigContants.REFUND_CHARSET));
            super.setParam("sign", sign);
            req.setTradeType(TradeType.REFUND_APPLY);
        } catch (Exception e) {
            log.error("[支付宝]-[退款申请]-[报文拼装前设置参数]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return req;
    }

    @Override
    public RefundApplyRepDTO resolveMsg(RefundApplyReqDTO req, byte[] rtnMsg, ChannelConfig channelConfig)
            throws ChannelMsgException {
        Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
        RefundApplyRepDTO refundRepDTO = new RefundApplyRepDTO();
        try {
            String repString = StringUtils.toString(rtnMsg, StringUtils.trim(config.getCharset()));
            Map<String, Object> respMap = AlipayUtil.getMapFromXmlStr(repString);
            // 设置状态码和状态信息
            String is_success = (String) respMap.get("is_success");
            String errorInfo = (String) respMap.get("error");
            refundRepDTO.setChlRtnCode(is_success);
            refundRepDTO.setTradeStatus(BaseRepConvert.convertTradeStatus(PayTool.ALIPAY_NATIVE, is_success));
            if (StringUtil.isNotBlank(errorInfo)) {
                refundRepDTO.setChlRtnMsg(TradeStatus.FAIL.getValue() + ":" + errorInfo);
            } else {
                refundRepDTO.setChlRtnMsg(BaseRepConvert.convertChlRetMsg(PayTool.ALIPAY_NATIVE, is_success));
            }
            DecimalFormat format = new DecimalFormat(AlipayConfigContants.DECIMAL_FORMAT);
            refundRepDTO.setRefundAmount(new BigDecimal(format.format(req.getRefundAmount()))); // 金额
            refundRepDTO.setRefundDetailNo(req.getRefundDetailNo()); // 业务退款订单号
            log.info("[支付宝]-[退款申请]-[报文解析]－rtnCode[{}],rtnMsg[{}]", is_success, refundRepDTO.getChlRtnMsg());
        } catch (Exception e) {
            log.error("[支付宝]-[退款申请]-[报文解析]-[出现异常]", e);
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.UNKNOW);
        }
        return refundRepDTO;
    }

    @Override
    public byte[] builderMsg(RefundApplyReqDTO t, ChannelConfig channelConfig) throws ChannelMsgException {
        String reqString = "";
        reqString = ParamUtil.createSortParamString(this.getParamMap());
        byte[] result = null;
        try {
            result = reqString.getBytes(AlipayConfigContants.REFUND_CHARSET);
        } catch (Exception e) {
            throw new ChannelMsgException(ChannelRtnCodeEnum.M10000, TradeStatus.FAIL);
        }
        return result;
    }
}

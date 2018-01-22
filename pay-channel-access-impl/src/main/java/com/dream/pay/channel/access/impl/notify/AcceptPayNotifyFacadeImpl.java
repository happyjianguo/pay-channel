package com.dream.pay.channel.access.impl.notify;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dream.pay.channel.access.dto.PayNotifyRepDTO;
import com.dream.pay.channel.access.dto.PayNotifyReqDTO;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.notify.AcceptPayNotifyFacade;
import com.dream.pay.channel.access.notify.GatePayNotifyFacade;
import com.dream.pay.channel.service.nsq.PayNsqMessagePoser;
import com.dream.pay.enums.PartnerIdEnum;
import com.dream.pay.enums.PayTool;
import com.dream.pay.utils.ParamUtil;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AcceptPayNotifyFacadeImpl extends PayNsqMessagePoser implements AcceptPayNotifyFacade {

    @Resource
    private GatePayNotifyFacade gatePayNotifyFacade;

    @Override
    public void payNotify(String bizLine, String payType, HttpServletRequest request) {
        PayTool payTypeEnum = PayTool.valueOf(payType.toUpperCase());

        if (null == payTypeEnum) {
            log.error("接收支付通知接口，支付方式输入不合法[{}]", payType);
            return;
        }
        PartnerIdEnum partnerIdEnum = PartnerIdEnum.valueOf(bizLine.toUpperCase());
        if (null == partnerIdEnum) {
            log.error("接收支付通知接口，业务线输入不合法[{}]", bizLine);
            return;
        }
        PayNotifyReqDTO payNotifyReqDTO = new PayNotifyReqDTO();
        payNotifyReqDTO.setPayType(payTypeEnum);
        payNotifyReqDTO.setPartnerId(partnerIdEnum);
        payNotifyReqDTO.setReqDateTime(new Date());
        payNotifyReqDTO.setCheckSign(true);

        Map<String, String> stringMap;
        try {
            log.info("接收支付通知接口,请求url参数信息[{}]", request.getParameterMap());
            stringMap = ParamUtil.convertParamMap(request.getParameterMap(), true);// 参数转map
            StringBuffer stringBuffer = new StringBuffer();
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                stringBuffer.append("([" + entry.getKey() + "]=[" + entry.getValue() + "])");
            }
            log.info("接收支付通知接口,解析参数成MAP[{}]", stringBuffer.toString());
            payNotifyReqDTO.setExt(stringMap);// 适用于支付宝，支付宝无线，快钱
        } catch (Exception e) {
            log.error("接收支付通知接口，解析参数成MAP出错", e);
            return;
        }
        try {
            String postData = IOUtils.toString(request.getInputStream(), "UTF-8");// 流转字符串
            log.info("接收支付通知接口,解析的流参数信息为[{}]", postData);
            payNotifyReqDTO.setCallBackContent(postData);// 适用于微信扫码，微信公帐
        } catch (IOException e) {
            log.error("接收支付通知接口，解析参数成字符出错", e);
            return;
        }

        PayNotifyRepDTO payNotifyRepDTO = gatePayNotifyFacade.payNotify(payNotifyReqDTO);
        payNotifyRepDTO.setPayType(payTypeEnum);

        if (TradeStatus.SUCCESS.equals(payNotifyRepDTO.getTradeStatus())) {
            this.validateParam(payNotifyRepDTO);// 渠道返回后解析参数校验非空性
            //TODO
            //发送NSQ消息，收单支付成功监听
            super.sendNsqMessage(payNotifyRepDTO);
            //返回第三方接受通知成功
            respMsg(payNotifyRepDTO.getPayType(), payNotifyRepDTO);

        } else {
            log.error("通知[{}]处理渠道返状态不是成功,是[{}]", payNotifyRepDTO.getPayDetailNo(), payNotifyRepDTO.getTradeStatus());
        }

    }

    private void respMsg(PayTool payType, PayNotifyRepDTO payNotifyRepDTO) {
        HttpServletResponse httpServletResponse = ResteasyProviderFactory.getContextData(HttpServletResponse.class);
        String responseMsg = "";
        try {
            switch (payType) {
                case ALIPAY_BARCODE://支付宝条码
                case ALIPAY_WAP:// 支付宝无线
                case ALIPAY_APP:// 支付宝APP
                case ALIPAY_NATIVE://支付宝扫码
                    responseMsg = "success";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case WX_BARCODE:// 微信条码
                case WX_NATIVE:// 微信扫码
                case WX_JS:// 微信公帐
                case WX_H5: // 微信wap
                case WX_APP:// 微信app
                case WX_APPLET://w微信小程序
                    responseMsg = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                    httpServletResponse.setContentType("text/xml");
                    BufferedOutputStream out = new BufferedOutputStream(httpServletResponse.getOutputStream());
                    out.write(responseMsg.getBytes());
                    out.flush();
                    out.close();
                    break;
                default:
                    break;
            }
            log.info("接受[{}]通知后,返回信息[{}]成功", payType, responseMsg);
        } catch (Exception e) {
            log.error("接受[{}]通知后,返回信息[{}]出现异常", payType, responseMsg, e);
        }
    }

    /**
     * @param httpServletResponse
     * @param responseMsg
     * @return void
     * @throws IOException
     * @throws
     * @Description: TODO(写响应内容)
     */
    private void writeResponse(HttpServletResponse httpServletResponse, String responseMsg) throws IOException {
        PrintWriter writer = httpServletResponse.getWriter();
        writer.write(responseMsg);
        writer.flush();
        writer.close();
    }

    /**
     * 参数非空校验
     *
     * @param payNotifyRepDTO
     */
    private void validateParam(PayNotifyRepDTO payNotifyRepDTO) {
        Assert.notNull(payNotifyRepDTO.getTradeStatus(), "渠道返回状态不可为空");
        Assert.notNull(payNotifyRepDTO.getChlRtnCode(), "渠道返回码不可为空");
        Assert.notNull(payNotifyRepDTO.getChlRtnMsg(), "渠道返回信息不可为空");
        Assert.notNull(payNotifyRepDTO.getPayDetailNo(), "支付明细号不可为空");
        Assert.notNull(payNotifyRepDTO.getPayAmount(), "支付金额不可为空");
        Assert.notNull(payNotifyRepDTO.getBankFinishTime(), "银行完成时间不可为空");
    }
}

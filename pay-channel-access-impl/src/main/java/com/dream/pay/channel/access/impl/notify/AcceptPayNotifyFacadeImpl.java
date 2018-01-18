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
import com.dream.pay.channel.access.enums.PayType;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.notify.AcceptPayNotifyFacade;
import com.dream.pay.channel.access.notify.GatePayNotifyFacade;
import com.dream.pay.channel.service.enums.BizLine;
import com.dream.pay.utils.ParamUtil;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AcceptPayNotifyFacadeImpl implements AcceptPayNotifyFacade {

    @Resource
    private GatePayNotifyFacade gatePayNotifyFacade;
    @Autowired
    private NotifyServiceFactory payNotifyServiceFactory;

    @Override
    public void payNotify(String bizLine, String payType, HttpServletRequest request) {
        PayType payTypeEnum = PayType.valueOf(payType.toUpperCase());
        Map<String, String> stringMap = null;
        if (null == payTypeEnum) {
            log.error("接收支付通知接口，支付方式输入不合法[{}]", payType);
            return;
        }
        BizLine bizLineEnum = BizLine.valueOf(bizLine.toUpperCase());
        if (null == bizLineEnum) {
            log.error("接收支付通知接口，业务线输入不合法[{}]", bizLine);
            return;
        }
        PayNotifyReqDTO payNotifyReqDTO = new PayNotifyReqDTO();
        payNotifyReqDTO.setPayType(payTypeEnum);
        payNotifyReqDTO.setBizCode(bizLine);
        payNotifyReqDTO.setReqDateTime(new Date());
        try {
            log.info("接收通知付通接口的请求url参数信息[{}]", request.getParameterMap());
            stringMap = ParamUtil.convertParamMap(request.getParameterMap(), true);// 参数转map
            StringBuffer stringBuffer = new StringBuffer();
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                stringBuffer.append("([" + entry.getKey() + "]=[" + entry.getValue() + "])");
            }
            log.info("接收支付通知接口,解析参数成MAP[{}]", stringBuffer.toString());
            payNotifyReqDTO.setMap(stringMap);// 适用于支付宝，支付宝无线，快钱
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
        payNotifyReqDTO.setCheckSign(true);
        boolean isSend = false;
        PayNotifyRepDTO payNotifyRepDTO = gatePayNotifyFacade.payNotify(payNotifyReqDTO);
        payNotifyRepDTO.setPayType(payTypeEnum.getValue());
        if (TradeStatus.SUCCESS.equals(payNotifyRepDTO.getTradeStatus())) {
            if (payNotifyRepDTO.isNoNoticeBusinessSystem()) { // 不需要进行业务系统的到账通知，直接给第三方支付回应报文
                respMsg(payNotifyRepDTO.getPayType(), null);
            } else {
                this.validateParam(payNotifyRepDTO);// 渠道返回后解析参数校验非空性
                isSend = payNotifyServiceFactory.selectNotifyService(bizLine).handlePayNotify(payNotifyRepDTO);
            }
        } else {
            if (BizLine.EXCHANGE.toString().equals(bizLine)) {// 如果是兑换的交易，需要通知兑换业务系统
                isSend = payNotifyServiceFactory.selectNotifyService(bizLine).handlePayNotify(payNotifyRepDTO);
            }
            log.error("通知[{}]处理渠道返状态不是成功,是[{}]", payNotifyRepDTO.getBizOrderNo(), payNotifyRepDTO.getTradeStatus());
        }
        if (isSend) {// 通知消息队列成功，给第三方通道返回成功
            respMsg(payNotifyRepDTO.getPayType(), payNotifyRepDTO);
        }

    }

    private void respMsg(Integer payType, PayNotifyRepDTO payNotifyRepDTO) {
        HttpServletResponse httpServletResponse = ResteasyProviderFactory.getContextData(HttpServletResponse.class);
        String responseMsg = "";
        try {
            switch (payType.intValue()) {
                case 51:// 支付宝扫码
                case 79:// 支付宝无线
                case 11:// 支付宝APP
                case 44:// 财付通
                case 92:// 支付宝分账
                case 96:// 支付宝分账APP
                case 95:// 支付宝分账WAP
                case 196:// 支付宝分账APP
                case 195:// 支付宝分账WAP
                case 98:// 支付B2B
                    responseMsg = "success";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 48:// 快钱工行
                case 25:// 首信支付
                    responseMsg = "sent";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 4:// 招行支付
                case 197:// 招行一网通手机支付
                    responseMsg = "OK";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 97:// 19pay
                    responseMsg = "Y";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 55:// 银联在线
                    responseMsg = "OK";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 53:// 和包支付
                case 81:// 和包虚拟支付
                    responseMsg = "SUCCESS";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 84:// 微信扫码
                case 85:// 微信公帐
                case 86: // 微信wap
                case 10:// 微信app
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
            log.info("接受[{}]通知后,返回信息[{}]成功", payType.intValue(), responseMsg);
        } catch (Exception e) {
            log.error("接受[{}]通知后,返回信息[{}]出现异常", payType.intValue(), responseMsg, e);
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
        Assert.notNull(payNotifyRepDTO.getBizOrderNo(), "bizOrderNo不可为空");
        // Assert.notNull(payNotifyRepDTO.getPlatOrderNo(), "platOrderNo不可为空");
        // 银联在线没有返回交易单号
        Assert.notNull(payNotifyRepDTO.getBankFinshDateTime(), "bankFinshDateTime不可为空");
        Assert.notNull(payNotifyRepDTO.getChlRtnMsg(), "chlRtnMsg不可为空");
        Assert.notNull(payNotifyRepDTO.getPayAmount(), "payAmount不可为空");
    }
}

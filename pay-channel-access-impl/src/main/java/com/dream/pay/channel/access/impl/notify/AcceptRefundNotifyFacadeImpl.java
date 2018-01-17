package com.dream.pay.channel.access.impl.notify;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dream.pay.channel.access.dto.RefundNotifyRepDTO;
import com.dream.pay.channel.access.dto.RefundNotifyReqDTO;
import com.dream.pay.channel.access.enums.PayType;
import com.dream.pay.channel.access.notify.AcceptRefundNotifyFacade;
import com.dream.pay.channel.access.notify.GateRefundNotifyFacade;
import com.dream.pay.channel.service.enums.BizLine;
import com.dream.pay.utils.ParamUtil;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;


@Service("acceptRefundNotifyFacadeImpl")
@Slf4j
public class AcceptRefundNotifyFacadeImpl implements AcceptRefundNotifyFacade {

    @Resource
    private GateRefundNotifyFacade gateRefundNotifyFacade;
    @Autowired
    private NotifyServiceFactory notifyServiceFactory;

    @Override
    public void refundNotify(String bizLine, String payType, HttpServletRequest request) {
        PayType payTypeEnum = PayType.valueOf(payType.toUpperCase());
        if (null == payTypeEnum) {
            log.error("接收退款通知接口，支付方式输入不合法[{}]", payType);
            return;
        }
        BizLine bizLineEnum = BizLine.valueOf(bizLine.toUpperCase());
        if (null == bizLineEnum) {
            log.error("接收退款通知接口，业务线输入不合法[{}]", bizLine);
            return;
        }
        // MDC.put("payType", payType.toUpperCase());
        RefundNotifyReqDTO refundNotifyReqDTO = new RefundNotifyReqDTO();
        refundNotifyReqDTO.setPayType(payTypeEnum);
        refundNotifyReqDTO.setBizCode(bizLine);
        refundNotifyReqDTO.setReqDateTime(new Date());
        try {
            log.info("接收通知付通接口的请求url参数信息[{}]", request.getQueryString());
            Map<String, String> stringMap = ParamUtil.convertParamMap(request.getParameterMap(), false);// 参数转map
            StringBuffer stringBuffer = new StringBuffer();
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                stringBuffer.append("([" + entry.getKey() + "]=[" + entry.getValue() + "])");
            }
            log.info("接收支付通知接口,解析参数成MAP[{}]", stringBuffer.toString());
            refundNotifyReqDTO.setMap(stringMap);// 适用于支付宝
        } catch (Exception e) {
            log.error("接收支付通知接口，解析参数成MAP出错", e);
            return;
        }
        RefundNotifyRepDTO refundNotifyRepDTO = gateRefundNotifyFacade.refundNotify(refundNotifyReqDTO);
        refundNotifyRepDTO.setPayType(payTypeEnum.getValue());
        try {
            notifyServiceFactory.selectNotifyService(bizLine).handleRefundNotify(refundNotifyRepDTO);
        } catch (Exception e) {
            log.error("通知退款业务处理异常", e);
        }
        respMsg(refundNotifyRepDTO.getPayType());
        log.info("通知处理渠道返状态,是[{}]", refundNotifyRepDTO.getTradeStatus());
    }

    private void respMsg(Integer payType) {
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
                    responseMsg = "success";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 25:// 首信支付
                    responseMsg = "sent";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 4:// 招行支付
                    responseMsg = "OK";
                    writeResponse(httpServletResponse, responseMsg);
                    break;
                case 55:// 银联在线
                    responseMsg = "OK";
                    writeResponse(httpServletResponse, responseMsg);
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
}

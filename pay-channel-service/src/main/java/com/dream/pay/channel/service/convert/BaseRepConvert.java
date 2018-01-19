package com.dream.pay.channel.service.convert;

import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.enums.PayTool;

import java.util.HashMap;
import java.util.Map;

public class BaseRepConvert {

    public static final Map<String, TradeStatus> tradeStatusMap = new HashMap<String, TradeStatus>();
    public static final Map<String, String> chlRetMsgMap = new HashMap<String, String>();

    static {
        tradeStatusMap.put("ALIPAY_NATIVE#TRADE_SUCCESS", TradeStatus.SUCCESS);
        tradeStatusMap.put("ALIPAY_NATIVE#TRADE_FINISHED", TradeStatus.SUCCESS);
        tradeStatusMap.put("ALIPAY_NATIVE#TRADE_CLOSED", TradeStatus.FAIL);
        tradeStatusMap.put("ALIPAY_NATIVE#WAIT_BUYER_PAY", TradeStatus.PROCESS);
        tradeStatusMap.put("ALIPAY_NATIVE#TRADE_PENDING", TradeStatus.PROCESS);
        tradeStatusMap.put("ALIPAY_NATIVE#T", TradeStatus.PROCESS);
        tradeStatusMap.put("ALIPAY_NATIVE#P", TradeStatus.PROCESS);
        tradeStatusMap.put("ALIPAY_NATIVE#F", TradeStatus.FAIL);
        tradeStatusMap.put("ALIPAY_NATIVE#REFUND_SUCCESS", TradeStatus.SUCCESS);
        tradeStatusMap.put("ALIPAY_NATIVE#REFUND_FAIL", TradeStatus.FAIL);
    }

    static {
        chlRetMsgMap.put("ALIPAY_NATIVE#TRADE_SUCCESS", "交易成功");
        chlRetMsgMap.put("ALIPAY_NATIVE#TRADE_FINISHED", "交易成功且结束");
        chlRetMsgMap.put("ALIPAY_NATIVE#TRADE_CLOSED", "交易关闭，未支付或已退款");
        chlRetMsgMap.put("ALIPAY_NATIVE#WAIT_BUYER_PAY", "交易创建，等待买家付款");
        chlRetMsgMap.put("ALIPAY_NATIVE#TRADE_PENDING", "等待卖家收款");
        chlRetMsgMap.put("ALIPAY_NATIVE#T", "退款受理成功");
        chlRetMsgMap.put("ALIPAY_NATIVE#P", "退款受理处理中");
        chlRetMsgMap.put("ALIPAY_NATIVE#F", "退款受理失败");
        chlRetMsgMap.put("ALIPAY_NATIVE#REFUND_SUCCESS", "退款受理成功");
        chlRetMsgMap.put("ALIPAY_NATIVE#REFUND_FAIL", "退款受理失败");
    }

    public static TradeStatus convertTradeStatus(PayTool payType, String retCode) {
        TradeStatus tradeStatus = tradeStatusMap.get(payType + "#" + retCode);
        if (null == tradeStatus) {
            tradeStatus = TradeStatus.UNKNOW;
        }
        return tradeStatus;
    }

    public static String convertChlRetMsg(PayTool payType, String retCode) {
        String chlRetMsg = chlRetMsgMap.get(payType + "#" + retCode);
        if (null == chlRetMsg) {
            chlRetMsg = "返回错误信息未知";
        }
        return chlRetMsg;
    }

    public static void main(String[] args) {
        System.out.println(convertTradeStatus(PayTool.ALIPAY_NATIVE, "TRADE_SUCCESS"));
    }
}

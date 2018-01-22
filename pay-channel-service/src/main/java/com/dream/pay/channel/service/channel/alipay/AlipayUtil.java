package com.dream.pay.channel.service.channel.alipay;

import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.other.MapFilter;
import com.dream.pay.utils.Base64;
import com.dream.pay.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 支付宝对接工具类 Created by mengzhenbin on 16/06/16
 */
@Slf4j
public class AlipayUtil {

    /**
     * 生成签名
     *
     * @param paramMap
     * @param signType
     * @return
     */
    public static String createSign(Map<String, String> paramMap, SignType signType, String signKey, Charset charset) {
        // 除去数组中的空值和不参与生成摘要的参数
        MapFilter<String> keyFilter = new MapFilter<String>("sign", "sign_type", "pay_url", "wap_pay_url", "key");
        MapFilter<String> valueFilter = new MapFilter<String>("", null);
        Map<String, String> requestParam = ParamUtil.mapFilter(paramMap, keyFilter, valueFilter);
        // 把数组所有元素，按照"参数=参数值"的模式用"&"字符拼接成字符串
        String paramString = ParamUtil.createSortParamString(requestParam);
        if (signType == SignType.MD5) {
            return SignUtil.md5(paramString + signKey, charset);
        } else if (signType == SignType.RSA) {
            PrivateKey privateKey = SignUtil.getPrivateKey(signKey);
            return SignUtil.signRSA(paramString, privateKey, charset);
        }
        return "";
    }

    /**
     * RSA验签名检查
     *
     * @param sign              签名值
     * @param alipay_public_key 支付宝公钥
     * @param input_charset     编码格式
     * @return 布尔值
     */
    public static boolean verify(Map<String, String> paramMap, String sign, String alipay_public_key,
                                 String input_charset) {
        try {
            // 除去数组中的空值和不参与生成摘要的参数
            MapFilter<String> keyFilter = new MapFilter<String>("sign", "sign_type", "pay_url", "wap_pay_url", "key");
            MapFilter<String> valueFilter = new MapFilter<String>("", null);
            Map<String, String> requestParam = ParamUtil.mapFilter(paramMap, keyFilter, valueFilter);
            // 把数组所有元素，按照"参数=参数值"的模式用"&"字符拼接成字符串
            String paramString = ParamUtil.createSortParamString(requestParam);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(alipay_public_key);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(paramString.getBytes(input_charset));
            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * wap到款通知验签<br/>
     * wap签名比较特殊
     *
     * @param paramMap
     * @return
     */
    public static String createWapSign(Map<String, String> paramMap, String signKey, Charset charset) {
        StringBuilder signString = new StringBuilder();
        signString.append("service=" + paramMap.get("service"));
        signString.append("&v=" + paramMap.get("v"));
        signString.append("&sec_id=" + paramMap.get("sec_id"));
        signString.append("&notify_data=" + paramMap.get("notify_data"));
        if (SignType.MD5.name().equalsIgnoreCase(paramMap.get("sec_id"))) {
            log.warn("[AlipayWap]wap到款通知验签走的是老的渠道方式");
            return SignUtil.md5(signString.toString() + signKey, charset);
        } else {
            log.info("[AlipayWap]wap到款通知验签方式sec_id[{}]无效，应该是MD5验签", paramMap.get("sec_id"));
            return createSign(paramMap, SignType.MD5, signKey, charset);
        }
    }


    /**
     * 将返回的xml字符串转换为Map对象
     *
     * @param xmlData
     * @return
     */
    public static Map<String, Object> getMapFromXmlStr(String xmlData) {
        Map<String, Object> map = new TreeMap<String, Object>();
        try {
            xmlData = xmlData.replaceAll("\"", "'").replaceAll("\t", "");
            String resMsg = xmlData;
            if (resMsg.indexOf("&") > -1) {
                resMsg = resMsg.replaceAll("&", "&amp;");
            }
            Document document = DocumentHelper.parseText(resMsg);
            Element rootEle = document.getRootElement();
            // 获取根节点下的子节点data
            Iterator iter = rootEle.elementIterator();
            while (iter.hasNext()) {
                Element subEle = (Element) iter.next();
                map.put(subEle.getName(), subEle.getTextTrim());
                if ("response".equals(subEle.getName())) {
                    List iterChild = subEle.content();
                    if (iterChild != null) {
                        Map<String, String> childMap = new HashMap<String, String>();
                        for (int i = 0; i < iterChild.size(); i++) {
                            Element subChild = (Element) iterChild.get(i);
                            if ("trade".equals(subChild.getName())) {
                                List itemChild = subChild.content();
                                if (itemChild != null) {
                                    for (int j = 0; j < itemChild.size(); j++) {
                                        try {
                                            Element subItemChild = (Element) itemChild.get(j);
                                            childMap.put(subItemChild.getName(), subItemChild.getTextTrim());
                                        } catch (Exception ex) {
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                        map.put("listItem", childMap);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Alipay支付将xmlStr转换为Map时异常", e);
        }
        return map;
    }

    /**
     * 根据dom和xpath获取节点的text
     *
     * @param dom
     * @param xpath
     * @return
     */
    public static String getValueByDomXpath(Document dom, String xpath) {
        Element ele = (Element) dom.selectSingleNode(xpath);
        return ele.getText();
    }

    /**
     * 查询字符串转换成Map<br/>
     * name1=key1&name2=key2&...
     *
     * @param queryString
     * @return
     */

    public static Map queryString2Map(String queryString) {
        if (null == queryString || "".equals(queryString)) {
            return null;
        }
        Map m = new HashMap();
        String[] strArray = queryString.split("&");
        for (int index = 0; index < strArray.length; index++) {
            String pair = strArray[index];
            putMapByPair(pair, m);
        }
        return m;

    }

    /**
     * 把键值添加至Map<br/>
     * pair:name=value
     *
     * @param pair name=value
     * @param m
     */

    @SuppressWarnings("unchecked")
    public static void putMapByPair(String pair, Map m) {
        if (null == pair || "".equals(pair)) {
            return;
        }
        int indexOf = pair.indexOf("=");
        if (-1 != indexOf) {
            String k = pair.substring(0, indexOf);
            String v = pair.substring(indexOf + 1, pair.length());
            if (null != k && !"".equals(k)) {
                m.put(k, v);
            }
        } else {
            m.put(pair, "");
        }
    }

    public static String getToken(String request_token_str) {
        String[] strs = request_token_str.split("&");
        String request_token = "";
        if (strs != null && strs.length > 0) {
            for (int i = 0; i < strs.length; i++) {
                String keyValues = strs[i];
                if (keyValues.startsWith("res_data")) {
                    String domStr = keyValues.substring(9);
                    Map<String, Object> infoMap = XmlUtil.fromXml(domStr);
                    request_token = (String) infoMap.get("request_token");
                }
            }
        }
        return request_token;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJsonStr(String repString) {
        Map<String, Object> resolveMap = new HashMap<String, Object>();
        HashMap<String, Object> retMap = JsonUtil.fromJson(repString, HashMap.class);
        resolveMap.put("sign", retMap.get("sign"));
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) retMap
                .get("alipay_trade_refund_response");
        resolveMap.put("code", responseMap.get("code"));
        resolveMap.put("msg", responseMap.get("msg"));
        resolveMap.put("sub_code", responseMap.get("sub_code"));
        resolveMap.put("sub_msg", responseMap.get("sub_msg"));
        resolveMap.put("fund_change", responseMap.get("fund_change"));
        resolveMap.put("out_trade_no", responseMap.get("out_trade_no"));
        resolveMap.put("gmt_refund_pay", responseMap.get("gmt_refund_pay"));
        resolveMap.put("refund_fee", responseMap.get("refund_fee"));
        return resolveMap;
    }

    public static String toTradeDesc(String msg) {
        // 正向支付
        if ("TRADE_SUCCESS".equals(msg)) {
            return "交易成功";
        } else if ("TRADE_FINISHED".equals(msg)) {
            return "交易成功且结束";
        } else if ("TRADE_CLOSED".equals(msg)) {
            return "交易关闭，未支付或已退款";
        } else if ("WAIT_BUYER_PAY".equals(msg)) {
            return "交易创建，等待买家付款";
        } else if ("TRADE_PENDING".equals(msg)) {
            return "等待卖家收款";
        }
        if ("T".equals(msg)) {
            return "退款受理成功";
        } else if ("P".equals(msg)) {
            return "退款受理处理中";
        } else if ("F".equals(msg)) {
            return "退款受理失败";
        }
        // 未对应
        else {
            return "状态未知";
        }
    }

    public static TradeStatus toTradeStatusEnum(String msg) {
        // 正向支付
        if ("TRADE_SUCCESS".equals(msg)) {
            return TradeStatus.SUCCESS;
        } else if ("TRADE_FINISHED".equals(msg)) {
            return TradeStatus.SUCCESS;
        } else if ("TRADE_CLOSED".equals(msg)) {
            return TradeStatus.FAIL;
        } else if ("WAIT_BUYER_PAY".equals(msg)) {
            return TradeStatus.PROCESS;
        } else if ("TRADE_PENDING".equals(msg)) {
            return TradeStatus.PROCESS;
        }
        // 逆向退款申请
        if ("T".equals(msg)) {
            return TradeStatus.PROCESS;
        } else if ("P".equals(msg)) {
            return TradeStatus.PROCESS;
        } else if ("F".equals(msg)) {
            return TradeStatus.FAIL;
        }
        // 未对应
        else {
            return TradeStatus.UNKNOW;
        }
    }
}

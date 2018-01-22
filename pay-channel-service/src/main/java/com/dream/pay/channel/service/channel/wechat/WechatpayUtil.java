package com.dream.pay.channel.service.channel.wechat;

import com.dream.pay.channel.service.enums.SignType;
import com.dream.pay.other.MapFilter;
import com.dream.pay.utils.ParamUtil;
import com.dream.pay.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class WechatpayUtil {

    /**
     * 生成签名
     *
     * @param paramMap
     * @param signType
     * @return
     */
    public static String createSign(Map<String, String> paramMap, SignType signType, String signKey, Charset charset) {
        // 除去数组中的空值和不参与生成摘要的参数
        MapFilter<String> keyFilter = new MapFilter<>("sign", "key");
        MapFilter<String> valueFilter = new MapFilter<>("", null);
        Map<String, String> requestParam = ParamUtil.mapFilter(paramMap, keyFilter, valueFilter);
        // 把数组所有元素，按照"参数=参数值"的模式用"&"字符拼接成字符串
        String paramString = ParamUtil.createSortParamString(requestParam);
        if (signType == SignType.MD5) {
            String signString = paramString + "&key=" + signKey;
            log.info("[微信支付]-[添加签名]-[{}]", signString);
            return SignUtil.md5(signString).toUpperCase();
        } else if (signType == SignType.RSA) {
            PrivateKey privateKey = SignUtil.getPrivateKey(signKey);
            return SignUtil.signRSA(paramString, privateKey, charset);
        }
        return "";
    }

    /**
     * @Title: mapToXml @Description: 将map转换成xml格式的string @param @param
     * map @param @return 设定文件 @return String 返回类型 @throws
     */
    public static String mapToXml(Map<String, String> map) {
        StringBuffer returnXml = new StringBuffer();
        try {
            returnXml.append("<xml>");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String k = (String) entry.getKey();
                String v = (String) entry.getValue();
                if (null != v && !"".equals(v)) {
                    if (isNumeric(v)) {// 是否是数字
                        returnXml.append("<" + k + ">" + v + "</" + k + ">\n");
                    } else {
                        returnXml.append("<" + k + "><![CDATA[" + v + "]]></" + k + ">\n");
                    }
                }
            }
            returnXml.append("</xml>");
        } catch (Exception e) {
            log.error("mapToXml |error|", e);
        }
        return returnXml.toString();
    }

    /**
     * @Title: isNumeric @Description: 判断是否是数字 @param @param str @param @return
     * 设定文件 @return boolean 返回类型 @throws
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}

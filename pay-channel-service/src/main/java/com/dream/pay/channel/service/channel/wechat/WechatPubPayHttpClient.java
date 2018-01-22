package com.dream.pay.channel.service.channel.wechat;

import com.dream.pay.channel.service.core.exception.ChannelSocketException;
import com.dream.pay.utils.PropUtil;
import com.dream.pay.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLContexts;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

@Slf4j
public class WechatPubPayHttpClient {

    private String mchid;
    private String pkcs12Path;
    public static final String METHOD_POST = "POST";
    // 连接超时时间10秒 单位毫秒
    public static int CON_TIMEOUT = PropUtil.getInt("wechatpay.conn.timeout", 10000);
    // 读超时时间10秒 单位毫秒
    public static int READ_TIMEOUT = PropUtil.getInt("wechatpay.read.timeout", 10000);

    public WechatPubPayHttpClient() {
    }

    public WechatPubPayHttpClient(String mchid, String pkcs12Path) {
        this.mchid = mchid;
        this.pkcs12Path = pkcs12Path;
    }

    public String postWithCert(String postUrl, byte[] reqMsg) throws ChannelSocketException {
        String responseContent = null;
        try {
            KeyStore keyStore = SignUtil.loadPKCS12KeyStore(pkcs12Path, mchid);
            SSLContext context = SSLContexts.custom().loadKeyMaterial(keyStore, mchid.toCharArray()).build();
            HttpsURLConnection httpsURLConnection = null;
            URL url = new URL(postUrl);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setSSLSocketFactory(context.getSocketFactory());
            responseContent = post(httpsURLConnection, reqMsg);
        } catch (Exception e) {
            log.error("WechatPubPayHttpClient.postWithCert |error|", e);
        }
        return responseContent;
    }

    public String postWithNoCert(String postUrl, byte[] reqMsg) throws ChannelSocketException {
        String responseContent = null;
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, null, new SecureRandom());
            URL url = new URL(postUrl);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setSSLSocketFactory(context.getSocketFactory());
            responseContent = post(httpsURLConnection, reqMsg);
        } catch (Exception e) {
            log.error("WechatPubPayHttpClient.postWithNoCert |error|", e);
        }
        return responseContent;
    }

    /**
     * @param httpsURLConnection  请求的地址   
     * @param xmlStrByte          请求的数据     
     */
    public static String post(HttpsURLConnection httpsURLConnection, byte[] xmlStrByte) throws Exception {
        String responseContent = "";
        httpsURLConnection.setDoInput(true);
        httpsURLConnection.setDoOutput(true);
        httpsURLConnection.setRequestMethod(METHOD_POST);
        httpsURLConnection.setConnectTimeout(CON_TIMEOUT);
        httpsURLConnection.setReadTimeout(READ_TIMEOUT);
        httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(xmlStrByte.length));
        httpsURLConnection.setUseCaches(false);
        httpsURLConnection.getOutputStream().write(xmlStrByte, 0, xmlStrByte.length);
        httpsURLConnection.getOutputStream().flush();
        httpsURLConnection.getOutputStream().close();
        try (InputStream is = httpsURLConnection.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
            String tempLine = null;
            StringBuffer tempStr = new StringBuffer();
            String crlf = System.getProperty("line.separator");
            while ((tempLine = br.readLine()) != null) {
                tempStr.append(tempLine).append(crlf);
            }
            responseContent = tempStr.toString();
        } catch (Exception e) {
            log.error("WechatPubPayHttpClient.post |error|", e);
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return responseContent;
    }
}
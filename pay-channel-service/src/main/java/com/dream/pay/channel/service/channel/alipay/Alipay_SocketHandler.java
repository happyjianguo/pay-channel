package com.dream.pay.channel.service.channel.alipay;

import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.access.enums.TradeStatus;
import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.core.exception.ChannelSocketException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.socket.ChannelSocketHandler;
import com.dream.pay.channel.service.enums.ChannelRtnCodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class Alipay_SocketHandler implements ChannelSocketHandler {

    @Override
    public byte[] send(BaseReq req, byte[] reqMsg, ChannelConfig channelConfig) throws ChannelSocketException {
        Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
        byte[] repContent = null;
        try {
            String sendreqMsg = "";
            if (TradeType.REFUND_APPLY.equals(req.getTradeType())) {// 退款时需要指定退款的编码
                sendreqMsg = new String(reqMsg, AlipayConfigContants.REFUND_CHARSET);
                log.info("[支付宝通信组件]发送出报文[{}]", sendreqMsg);
                String url = config.getPostUrl();
                byte[] postData = sendreqMsg.getBytes(AlipayConfigContants.REFUND_CHARSET);
                repContent = this.httpPostMethod(url, postData, AlipayConfigContants.REFUND_CHARSET);
            } else {
                sendreqMsg = new String(reqMsg, channelConfig.getCharset());
                log.info("[支付宝通信组件]发送出报文[{}]", sendreqMsg);
                repContent = this.callHttp("UTF-8", config, sendreqMsg);
            }
        } catch (Exception e) {
            log.error("[支付宝通信组件]发送出报文[出现异常]", e);
            throw new ChannelSocketException(ChannelRtnCodeEnum.S10000, TradeStatus.FAIL);
        }
        return repContent;
    }

    protected byte[] callHttp(String charset, Alipay_ChannelConfig config, String postDate) throws IOException {
        String url = config.getPostUrl();
        byte[] postData = postDate.getBytes(config.getCharset());
        return this.httpPostMethod(url, postData, charset);
    }

    /**
     * 以http post方式通信
     *
     * @param strUrl
     * @param postData
     * @return
     * @throws IOException
     */
    protected byte[] httpPostMethod(String strUrl, byte[] postData, String charset) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        byte[] bys = this.doPost(httpURLConnection, postData, charset);
        httpURLConnection.disconnect();
        return bys;
    }

    /**
     * post方式处理
     *
     * @param conn
     * @param postData
     * @throws IOException
     */
    protected byte[] doPost(HttpURLConnection conn, byte[] postData, String charset) throws IOException {
        String respContent = "";
        // 以post方式通信
        conn.setRequestMethod("POST");
        // 设置请求默认属性
        this.setHttpRequest(conn);
        // Content-Type
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
        final int len = 1024; // 1KB
        doOutput(out, postData, len);
        // 关闭流
        out.close();
        // 获取响应返回状态码
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            // 获取应答输入流
            InputStream inputStream = conn.getInputStream();
            respContent = doResponse(charset, inputStream);
            log.info("[支付宝通信组件]接收到报文[{}]", respContent);
        } else {
            log.info("[支付宝通信组件]报文返回状态码!=200[状态码＝{}]", responseCode);
        }
        return respContent.getBytes(charset);
    }

    /**
     * 处理输出<br/>
     * 注意:流关闭需要自行处理
     *
     * @param out
     * @param data
     * @param len
     * @throws IOException
     */
    public static void doOutput(OutputStream out, byte[] data, int len) throws IOException {
        int dataLen = data.length;
        int off = 0;
        while (off < dataLen) {
            if (len >= dataLen) {
                out.write(data, off, dataLen);
            } else {
                out.write(data, off, len);
            }
            // 刷新缓冲区
            out.flush();
            off += len;
            dataLen -= len;
        }

    }

    /**
     * 处理应答
     *
     * @throws IOException
     */
    protected String doResponse(String enc, InputStream inputStream) throws IOException {

        if (null == inputStream) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, enc));
        // 获取应答内容
        String resContent = bufferedReader2String(reader);
        // 关闭流
        reader.close();
        // 关闭输入流
        inputStream.close();
        return resContent;
    }

    /**
     * BufferedReader转换成String<br/>
     * 注意:流关闭需要自行处理
     *
     * @param reader
     * @return String
     * @throws IOException
     */
    public static String bufferedReader2String(BufferedReader reader) throws IOException {
        StringBuffer buf = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buf.append(line);
            buf.append("\r\n");
        }

        return buf.toString();
    }

    /**
     * 设置http请求默认属性
     *
     * @param httpConnection
     */
    protected void setHttpRequest(HttpURLConnection httpConnection) {
        // 设置连接超时时间
        httpConnection.setConnectTimeout(30 * 1000);
        String userAgentValue = "Mozilla/4.0 (compatible; MSIE 6.0; Windows XP)";
        // User-Agent
        httpConnection.setRequestProperty("User-Agent", userAgentValue);
        // 不使用缓存
        httpConnection.setUseCaches(false);
        // 允许输入输出
        httpConnection.setDoInput(true);
        httpConnection.setDoOutput(true);
    }
}
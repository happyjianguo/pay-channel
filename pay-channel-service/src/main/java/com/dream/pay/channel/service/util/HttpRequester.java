package com.dream.pay.channel.service.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequester {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequester.class);
    /**
     * 连接池
     */
    private PoolingHttpClientConnectionManager connectionManager;

    /**
     * 请求配置信息
     */
    private RequestConfig requestConfig;

    public HttpRequester() {
    }

    /**
     * 构造函数
     */
    public HttpRequester(HttpCfg httpCfg) {
        try {
            connectionManager = getConnManager(httpCfg);
            requestConfig = RequestConfig.custom().setSocketTimeout(httpCfg.getReadTimeOut())
                    .setConnectionRequestTimeout(httpCfg.getReadTimeOut()).setConnectTimeout(httpCfg.getConnTimeOut())
                    .build();
        } catch (Exception e) {
            logger.error("##########初始化通信类错误" + e);
        }
    }

    private HttpClient getConnection() throws Exception {
        PoolingHttpClientConnectionManager connManager = this.connectionManager;
        CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }

    /**
     * 获得连接管理
     *
     * @return
     * @throws Exception
     */
    private static PoolingHttpClientConnectionManager getConnManager(HttpCfg httpCfg) throws Exception {
        System.setProperty("jsse.enableSNIExtension", "false");
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();

        if (HttpCfg.HTTPS.equals(httpCfg.getProtocol())) {
            ConnectionSocketFactory connectionSocketFactory = getSSLFactory(httpCfg);
            registryBuilder.register("https", connectionSocketFactory);
        } else {
            ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
            registryBuilder.register("http", plainSF);
        }

        try {
            Registry<ConnectionSocketFactory> registry = registryBuilder.build();
            return new PoolingHttpClientConnectionManager(registry);
        } catch (Exception e) {
            logger.error("创建的连接失败", e);
            throw e;
        }
    }

    /**
     * 创建ssl工厂
     *
     * @param httpCfg
     * @return
     * @throws Exception
     */
    private static ConnectionSocketFactory getSSLFactory(HttpCfg httpCfg) throws Exception {

        // 构建信任库
        TrustManager[] tm = {new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};

        // 构建SSL上下文
        SSLContext sslContext = SSLContext.getInstance("SSL");

        // 区别单向和双向认证
        if (httpCfg.getHttpsFlag() == 1) {
            logger.info("#####HTTPS单向认证");
            sslContext.init(null, tm, null);// 单向认证
        } else {
            logger.info("#####HTTPS双向认证");
            // 加载私钥
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream(httpCfg.getPfxPath()), httpCfg.getPfxPass().toCharArray());

            // 构建私钥算法
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, httpCfg.getPfxPass().toCharArray());

            sslContext.init(kmf.getKeyManagers(), tm, null);// 双向认证
        }

        ConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return sslSF;
    }

    /**
     * 标准post表单请求
     *
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpRep sendPostForm(HttpReq httpRequest) throws Exception {
        HttpPost httpPost = new HttpPost(httpRequest.getUrl());
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : httpRequest.getParamMap().entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        HttpEntity formEntity = new UrlEncodedFormEntity(params);
        httpPost.setEntity(formEntity); // 添加请求post数据
        return this.send(httpRequest, httpPost);
    }

    /**
     * 标准get表单请求
     *
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpRep sendGetForm(HttpReq httpRequest) throws Exception {
        HttpGet httpGet = new HttpGet(httpRequest.getUrl());
        return this.send(httpRequest, httpGet);
    }

    /**
     * post提交文本请求
     *
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpRep sendPostString(HttpReq httpRequest) throws Exception {
        HttpPost httpPost = new HttpPost(httpRequest.getUrl());
        StringEntity formEntity = new StringEntity(httpRequest.getRequestBody(), httpRequest.getCharset());
        httpPost.setEntity(formEntity); // 添加请求post数据
        return this.send(httpRequest, httpPost);
    }

    /**
     * http核心方法
     *
     * @param httpRequest
     * @param httpMethod
     * @return
     * @throws IOException
     */
    private HttpRep send(HttpReq httpRequest, HttpRequestBase httpMethod) throws UnknownHostException,
            HttpHostConnectException, ConnectTimeoutException, SocketTimeoutException, Exception {
        HttpClient httpclient = this.getConnection();

        for (Map.Entry<String, String> entry : httpRequest.getHeadMap().entrySet()) { // 添加报文头
            httpMethod.addHeader(entry.getKey(), entry.getValue());
        }

        HttpResponse response = null; // 执行请求
        HttpRep respons = new HttpRep();
        try {
            response = httpclient.execute(httpMethod);
            if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) // 如果web服务器
            {
                logger.error("服务器应答失败,Http返回码为:" + response.getStatusLine().getStatusCode() + "]");
                throw new IOException("服务器应答失败,Http返回码为" + response.getStatusLine().getStatusCode());
            }
            String rspMsg = EntityUtils.toString(response.getEntity(), httpRequest.getCharset());
            respons.setContent(rspMsg);
        } catch (UnknownHostException e) {
            logger.error("[" + httpRequest.getUrl() + "]连接主机失败,未知主机名", e);
            throw e;
        } catch (HttpHostConnectException e) {
            logger.error("[" + httpRequest.getUrl() + "]无法连接到主机", e);
            throw e;
        } catch (ConnectTimeoutException e) {
            logger.error("[" + httpRequest.getUrl() + "]连接主机超时", e);
            throw e;
        } catch (SocketTimeoutException e) {
            logger.error("[" + httpRequest.getUrl() + "]读取主机超时", e);
            throw e;
        } catch (Exception e) {
            logger.error("[" + httpRequest.getUrl() + "]时异常", e);
            throw e;
        }
        return respons;
    }
}

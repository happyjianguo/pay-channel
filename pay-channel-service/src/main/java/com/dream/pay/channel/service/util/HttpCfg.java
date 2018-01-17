package com.dream.pay.channel.service.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpCfg {
    /**
     * 私钥路径
     */
    private String pfxPath;

    /**
     * 私钥密码
     */
    private String pfxPass;

    /**
     * 连接超时
     */
    private int connTimeOut;

    /**
     * 读超时
     */
    private int readTimeOut;

    /**
     * 协议
     */
    private String protocol = HTTPS;

    /**
     * 标识单向认证还是双向认证 默认单向 = 1 如果为双向，请设置为2或者其它值
     */
    private int httpsFlag = 1;

    /**
     * 端口
     */
    private int port;

    public static final String HTTPS = "HTTPS";
    public static final String HTTP = "HTTP";

}

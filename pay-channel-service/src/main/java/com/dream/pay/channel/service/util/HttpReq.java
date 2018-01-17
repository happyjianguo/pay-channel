package com.dream.pay.channel.service.util;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class HttpReq {
    /**
     * 提交参数集合
     */
    private Map<String, String> paramMap = new HashMap<String, String>();

    /**
     * 提交报头集合
     */
    private Map<String, String> headMap = new HashMap<String, String>();

    /**
     * 提交地址
     */
    private String url = "";

    /**
     * 请求报文体
     */
    private String requestBody = "";

    /**
     * 请求报文编码
     */
    private String charset = "UTF-8";

}

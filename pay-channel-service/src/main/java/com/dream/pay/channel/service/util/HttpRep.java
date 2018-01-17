package com.dream.pay.channel.service.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Vector;

@Setter
@Getter
public class HttpRep {
    /**
     * 响应地址
     */
    private String urlString;

    /**
     * 默认端口号
     */
    private int defaultPort;

    /**
     * 文件
     */
    private String file;

    /**
     * 主机
     */
    private String host;

    /**
     * 路径
     */
    private String path;

    /**
     * 端口
     */
    private int port;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 请求参数
     */
    private String query;

    /**
     * 请求
     */
    private String ref;

    /**
     * 浏览器信息
     */
    private String userInfo;

    /**
     * 编码
     */
    private String contentEncoding;

    /**
     * 信息
     */
    private String content;

    /**
     * 信息类型
     */
    private String contentType;

    /**
     * 响应码
     */
    private int code;

    /**
     * 消息
     */
    private String message;
    /**
     * 方法
     */
    private String method;

    /**
     * 连接超时
     */
    private int connectTimeout;

    /**
     * 读超时
     */
    private int readTimeout;

    /**
     * 其他数据
     */
    private Vector<String> contentCollection;
}

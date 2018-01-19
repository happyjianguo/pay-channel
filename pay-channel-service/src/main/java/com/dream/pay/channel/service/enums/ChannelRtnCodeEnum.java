package com.dream.pay.channel.service.enums;

import lombok.Getter;

/**
 * 渠道返回错误码
 *
 * @author mengzhenbin
 */
public enum ChannelRtnCodeEnum {

    /**
     * 请求成功
     */
    SUCCESS("请求成功"),
    /**
     * 系统异常
     */
    E10000("系统异常"),

    /**
     * 验证参数异常
     */
    V10000("验证组件异常"), V10001("业务编码不合法"), V10002("渠道编码不合法"), V10003("请求时间不合法"),

    /**
     * 拼装报文异常
     */
    M10000("报文组件异常"),

    /**
     * 解析返回报文异常
     */
    M10001("同步返回报文异常"),

    /**
     * 解析异步返回报文异常
     */
    M10002("异步返回报文异常"),

    /**
     * 通信异常
     */
    S10000("通信组件异常"),;


    @Getter
    private String message;

    ChannelRtnCodeEnum(String message) {
        this.message = message;
    }
}

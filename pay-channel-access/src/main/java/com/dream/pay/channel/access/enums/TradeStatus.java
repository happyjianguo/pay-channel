package com.dream.pay.channel.access.enums;

import lombok.Getter;

/**
 * 交易状态
 *
 * @author mengzhenbin
 */
@Getter
public enum TradeStatus {
    SUCCESS("成功"), FAIL("失败"), PROCESS("处理中"), UNKNOW("未知"), RETRAY("重试");

    private String value;

    TradeStatus(String value) {
        this.value = value;
    }
}

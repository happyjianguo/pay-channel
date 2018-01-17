package com.dream.pay.channel.access.enums;

/**
 * @author 孟振滨: mengzhenbin@dangdang.com
 * @version 创建时间：2016年6月23日 下午2:22:21
 */
public enum TradeType {
	PAY_APPLY, // 支付申请
	PAY_QUERY, // 支付查询
	PAY_NOTIFY, // 支付通知
	REFUND_APPLY, // 退款申请
	REFUND_QUERY, // 退款查询
	REFUND_NOTIFY, // 退款通知
	AGREE_QUERY, // 签约查询
	AGREE_NOTIFY, // 签约通知
	PUBKEY_QUERY; // 公钥查询
}

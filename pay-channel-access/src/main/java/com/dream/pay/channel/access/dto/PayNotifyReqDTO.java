package com.dream.pay.channel.access.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PayNotifyReqDTO extends BaseReq {
	private static final long serialVersionUID = 1L;
	private String callBackContent;
	private boolean isCheckSign;
}

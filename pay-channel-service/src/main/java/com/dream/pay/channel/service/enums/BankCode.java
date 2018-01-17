package com.dream.pay.channel.service.enums;

public enum BankCode {
	BOC("中国银行"), 
	ABC("中国农业银行"), 
	CCB("中国建设银行"), 
	ICBC("中国工商银行"), 
	
	CMB("招商银行"), 
	GDB("广东发展银行"), 
	BCOM("中国交通银行"), 
	CMBC("中国民生银行"), 
	PAB("平安银行"), 
	CITIC("中信银行"), 
	SPDB("浦发银行"), 
	CEB("中国光大银行"), 
	CIB("兴业银行"), 
	HXB("华夏银行"), 
	PSBC("中国邮政储蓄银行"),
	
	BOB("北京银行"), 
	SHB("上海银行"), 
	GZCB("广州银行"), 
	SDB("深圳发展银行"), 
	NJCB("南京银行"), 
	NBCB("宁波银行"), 
	DLB("大连银行"), 
	JSB("江苏银行"),
	HZB("杭州银行"), 
	CZB("浙商银行"),
	CBHB("渤海银行"), 
	BEA("东亚银行"), 
	
	BJRCB("北京农商银行"), 
	SRCB("上海农村商业银行"), 
	HSB("徽商银行");
	
	private String desc;

	private BankCode(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}

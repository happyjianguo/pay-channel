package com.dream.pay.channel.access.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 提现申请请求参数
 *
 * @author mengzhenbin
 * @version 1.0 on 2017/3/16
 */
@Data
@Setter
@Getter
@ToString(callSuper = true)
public class WithdrawApplyReqDTO extends BaseReq {

    private static final long serialVersionUID = -6843196946798701960L;
    /**
     * 提现流水
     */
    @NotNull(message = "提现流水号不可为空")
    private String withdrawNo;

    /**
     * 提现金额(单位:分)
     */
    @NotNull(message = "提现金额不可为空")
    private Long withdrawAmount;

    /**
     * 收款银行分类码 联行号
     */
    @NotNull(message = "收款人所属银行联行号不可为空")
    private String recvBankCode;

    /**
     * 收款账户开户行名称
     */
    @NotNull(message = "收款账户开户行名称不可为空")
    private String recvBankName;

    /**
     * 对公 or 对私
     */
    private String recvAccType;

    /**
     * 收款方的银行开户卡号
     */
    @NotNull(message = "收款方的银行开户卡号不可为空")
    private String recvAccNo;

    /**
     * 收款方银行卡开户姓名
     */
    @NotNull(message = "收款方银行卡开户姓名不可为空")
    private String recvAccName;


}

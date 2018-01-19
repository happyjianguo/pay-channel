package com.dream.pay.channel.access.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

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
public class WithdrawApplyRepDTO extends BaseRep {

    /**
     * 提现流水-渠道／第三方
     */
    private String withdrawChannelNo;

    /**
     * 三方完成时间
     */
    private Date withdrawFinishTime;

}

package com.dream.pay.channel.access.impl.notify;


import com.dream.pay.channel.access.dto.PayNotifyRepDTO;
import com.dream.pay.channel.access.dto.RefundNotifyRepDTO;

public interface PaymentNotifyService {
    public boolean handlePayNotify(PayNotifyRepDTO payNotifyRepDTO);

    public boolean handleRefundNotify(RefundNotifyRepDTO refundNotifyRepDTO);
}

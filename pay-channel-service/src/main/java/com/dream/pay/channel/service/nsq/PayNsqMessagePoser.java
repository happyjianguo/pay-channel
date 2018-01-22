package com.dream.pay.channel.service.nsq;

import com.dream.pay.channel.access.dto.PayNotifyRepDTO;
import com.dream.pay.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author mengzhenbin
 * @Since 2018/1/10
 */
@Slf4j
public class PayNsqMessagePoser extends NsqMessagePoser {

    @Value("${pay_messgae_topic}")
    private String topic;

    /**
     * 发送消息
     */
    public void sendNsqMessage(PayNotifyRepDTO payNotifyRepDTO) {
        send(topic, payNotifyRepDTO.getPayDetailNo() + "-" + payNotifyRepDTO.getTradeStatus(), JsonUtil.toJson(payNotifyRepDTO));

    }
}

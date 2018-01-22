package com.dream.pay.channel.service.nsq;

import com.google.common.collect.Maps;
import com.youzan.dts.client.api.TransactionActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Resource;

/**
 * Nsq事务消息发送
 *
 * @Author mengzhenbin
 * @Since 2018/1/10
 */
@Slf4j
public class NsqMessagePoser {

    @Resource
    private TransactionActivityService transactionActivityService;

    /**
     * 发送消息
     */
    public void send(String topic, String txId, String message) {
        log.info("发送支付消息开始:[{}]-[{}]", topic, message);
        try {
            //开启分布式事务
            transactionActivityService.start("pay_channel", txId, Maps.<String, Object>newHashMap());

            transactionActivityService.enrollAction(topic, message);
        } catch (DuplicateKeyException e) {
            log.error("开启消息事务失败,业务号重复:业务类型=[{}],业务号=[{}]", "pay-channel", txId);
        } catch (Exception e) {
            log.error("开启消息事务失败,未知异常:业务类型=[{}],业务号=[{}]", "pay-channel", txId, e);
        }
        log.info("发送支付消息结束:[{}]-[{}]", topic, message);

    }
}

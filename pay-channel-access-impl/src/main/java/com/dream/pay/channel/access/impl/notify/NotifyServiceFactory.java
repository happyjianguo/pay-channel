package com.dream.pay.channel.access.impl.notify;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class NotifyServiceFactory implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取网关类渠道通知写入Service
     *
     * @return
     */
    public PaymentNotifyService selectNotifyService(String bizLine) {
        PaymentNotifyService paymentNotifyService = applicationContext
                .getBean(bizLine.toUpperCase() + "_PaymentNotifyService", PaymentNotifyService.class);
        return paymentNotifyService;
    }
}

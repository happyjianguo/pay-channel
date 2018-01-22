package com.dream.pay.channel.service.core.context;

import com.dream.pay.channel.access.enums.TradeType;
import com.dream.pay.channel.service.access.GateWayService;
import com.dream.pay.channel.service.core.exception.BaseException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.enums.PayTool;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 渠道上下文
 *
 * @author chenjianchunjs
 */
@Component
public class ChannelContext implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final static ThreadLocal<PayTool> PAYTYPE_HOLDER = new ThreadLocal<PayTool>();
    private final static ThreadLocal<TradeType> TRADETYPE_HOLDER = new ThreadLocal<TradeType>();
    private final static Map<PayTool, ChannelConfig> PAYTYPE_CHANNEL_CONFIG_MAP = Maps.newHashMap();

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取当前渠道上下文--支付方式
     *
     * @return
     * @throws BaseException
     */
    public static PayTool getPayType() throws BaseException {
        PayTool payType = PAYTYPE_HOLDER.get();
        if (payType == null) {
            throw new BaseException();
        }
        return payType;
    }

    /**
     * 获取当前请求类型上下文--交易类型
     *
     * @return
     * @throws BaseException
     */
    public static TradeType getTradeType() throws BaseException {
        TradeType tradeType = TRADETYPE_HOLDER.get();
        if (tradeType == null) {
            throw new BaseException();
        }
        return tradeType;
    }

    /**
     * 设置当前渠道上下文--支付方式
     *
     * @param payType
     */
    public static void setPayType(PayTool payType) {
        PAYTYPE_HOLDER.set(payType);
    }

    /**
     * 设置当前请求类型下文--交易类型
     *
     * @param tradeType
     */
    public static void setTradeType(TradeType tradeType) {
        TRADETYPE_HOLDER.set(tradeType);
    }

    /**
     * 取得渠道上下文配置实例
     *
     * @return
     * @throws BaseException
     */
    public static ChannelConfig getConfig() throws BaseException {
        PayTool payType = getPayType();
        return PAYTYPE_CHANNEL_CONFIG_MAP.get(payType);
    }

    /**
     * 获取网关类渠道支付Service
     *
     * @return
     * @throws BaseException
     */
    GateWayService getGateWayService() throws BaseException {
        PayTool payType = getPayType();
        GateWayService channel = applicationContext.getBean(payType.name().toUpperCase(), GateWayService.class);
        return channel;
    }

}

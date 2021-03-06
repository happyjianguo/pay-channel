package com.dream.pay.channel.service.core.handler.validate;

import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.service.core.exception.ChannelValidateException;

/**
 * 请求实体验证接口
 *
 * @author mengzhenbin
 * @since 16/06/16
 */
public interface ChannelValidateHandler<T extends BaseReq> {

    /**
     * 渠道接口验证参数
     *
     * @param t
     * @throws ChannelValidateException
     */
    public void validate(T t) throws ChannelValidateException;
}

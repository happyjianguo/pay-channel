package com.dream.pay.channel.service.core.process;

import com.dream.pay.channel.access.dto.BaseRep;
import com.dream.pay.channel.access.dto.BaseReq;
import com.dream.pay.channel.service.channel.alipay.Alipay_ChannelConfig;
import com.dream.pay.channel.service.channel.wechat.Wechatpay_ChannelConfig;
import com.dream.pay.channel.service.core.exception.ChannelMsgException;
import com.dream.pay.channel.service.core.exception.ChannelSocketException;
import com.dream.pay.channel.service.core.exception.ChannelValidateException;
import com.dream.pay.channel.service.core.handler.config.ChannelConfig;
import com.dream.pay.channel.service.core.handler.msg.ChannelMsgHandler;
import com.dream.pay.channel.service.core.handler.socket.ChannelSocketHandler;
import com.dream.pay.channel.service.core.handler.validate.ChannelValidateHandler;
import com.dream.pay.enums.PayTool;
import com.dream.pay.utils.PropUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 前置系统主要行为的实现封装类，抽象实现了整个前置的全部行为，集成了系统各个组件
 *
 * @author mengzhenbin
 * @since 16/06/16
 */
@Slf4j
@Setter
@Getter
public class ChannelActionProcess<REQ extends BaseReq, REP extends BaseRep> {

    /**
     * 验证组件
     */
    private ChannelValidateHandler<REQ> channelValidateHandler;

    /**
     * 报文组件
     */
    private ChannelMsgHandler<REQ, REP> channelMsgHandler;

    /**
     * 通信组件
     */
    private ChannelSocketHandler<REQ> channelSocketHandler;

    /**
     * 配置组件
     */
    private ChannelConfig channelConfig;

    /**
     * 核心方法
     *
     * @param req 上层平台请求实体
     * @return
     * @throws ChannelValidateException
     * @throws ChannelMsgException
     * @throws ChannelSocketException
     */

    public REP doProcess(REQ req) throws ChannelValidateException, ChannelMsgException, ChannelSocketException {
        // 预处理(可后续扩展实现渠道流水的纪录)
        beforPayProcess(req, channelConfig);

        if (channelValidateHandler != null) { // 如果参数校验组建为空则不验证参数
            channelValidateHandler.validate(req);
        }

        byte[] reqAfter = null;
        REQ reqBefor = null;
        if (channelMsgHandler != null) { // 如果报文组件为空就不组建报文
            long begin = System.currentTimeMillis();
            reqBefor = channelMsgHandler.beforBuildMsg(req, channelConfig);
            byte[] reqMsg = channelMsgHandler.builderMsg(reqBefor, channelConfig); // 组建报文
            reqAfter = channelMsgHandler.afterBuildMsg(reqBefor, reqMsg, channelConfig);
            long end = System.currentTimeMillis();
            log.info("####################组装报文耗时[" + (end - begin) + "]ms####################");
        }

        byte[] rtnMsg = reqAfter;
        if (channelSocketHandler != null || reqAfter == null) { // 如果通信组件不为空或者返回消息为null，就不发送报文。
            long begin = System.currentTimeMillis();
            rtnMsg = channelSocketHandler.send(reqBefor, reqAfter, channelConfig);
            long end = System.currentTimeMillis();
            log.info("####################通信耗时[" + (end - begin) + "]ms####################");
        }

        REP resolveMsgAfter = null;
        if (channelMsgHandler != null || rtnMsg == null) { // 如果报文组件不为空或者返回消息为null，就解析报文。
            long begin = System.currentTimeMillis();
            byte[] resolveMsgBefor = channelMsgHandler.beforResolveMsg(reqBefor, rtnMsg, channelConfig);
            REP resolveMsg = channelMsgHandler.resolveMsg(reqBefor, resolveMsgBefor, channelConfig); // 解析报文
            resolveMsgAfter = channelMsgHandler.afterResolveMsg(reqBefor, resolveMsg, channelConfig);
            long end = System.currentTimeMillis();
            log.info("####################解析报文耗时[" + (end - begin) + "]ms####################");
        }
        // 后处理
        afterProcess(reqBefor, resolveMsgAfter, channelConfig);
        return resolveMsgAfter;
    }

    /**
     * 回调核心方法
     *
     * @param req 上层平台请求实体
     * @return REP 返回类型
     * @throws ChannelValidateException
     * @throws ChannelMsgException
     * @throws ChannelSocketException
     */
    public REP doCallbackProcess(REQ req) throws ChannelValidateException, ChannelMsgException, ChannelSocketException {
        long begin = System.currentTimeMillis();
        beforNotifyProcess(req, channelConfig);
        REP resolveMsg = channelMsgHandler.resolveCallBackMsg(req, channelConfig);
        long end = System.currentTimeMillis();
        log.info("####################解析报文耗时[" + (end - begin) + "]ms####################");
        return resolveMsg;
    }

    /**
     * 核心方法执行前－－支付
     *
     * @param req
     * @param channelConfig
     * @throws ChannelValidateException
     * @throws ChannelMsgException
     */
    protected void beforPayProcess(REQ req, ChannelConfig channelConfig)
            throws ChannelValidateException, ChannelMsgException {
        String payTool = req.getPayType().name();

        if (PayTool.WX_NATIVE.equals(req.getPayType())
                || PayTool.WX_JS.equals(req.getPayType())
                || PayTool.WX_APP.equals(req.getPayType())
                || PayTool.WX_H5.equals(req.getPayType())) {
            Wechatpay_ChannelConfig config = (Wechatpay_ChannelConfig) channelConfig;
            config.setAppId(PropUtil.get(payTool + ".appId"));
            config.setSignKey(PropUtil.get(payTool + ".signKey"));
            config.setMerchantNo(PropUtil.get(payTool + ".merchantNo"));
            config.setPxfPath(PropUtil.get(payTool + ".pxfPath"));
            config.setPayNotifyUrl(PropUtil.get(payTool + ".payNotifyUrl") + "/" + payTool);
        } else if (PayTool.ALIPAY_NATIVE.equals(req.getPayType())
                || PayTool.ALIPAY_WAP.equals(req.getPayType())
                || PayTool.ALIPAY_APP.equals(req.getPayType())) {
            Alipay_ChannelConfig config = (Alipay_ChannelConfig) channelConfig;
            config.setPartner(PropUtil.get(payTool + ".partner"));
            config.setSignKey(PropUtil.get(payTool + ".sign_key"));
            config.setSignType(PropUtil.get(payTool + ".sign_type"));
            config.setCharset(PropUtil.get(payTool + ".charset"));
            config.setSellerEmail(PropUtil.get(payTool + ".seller_email"));

            config.setShowUrl(PropUtil.get("alipay.show_url") + "/" + payTool);
            config.setPayReturnUrl(PropUtil.get("alipay.pay_return_url") + "/" + payTool);
            config.setPayNotifyUrl(PropUtil.get("alipay.pay_notify_url") + "/" + payTool);
            config.setRefundNotifyUrl(PropUtil.get("alipay.refund_notify_url") + "/" + payTool);
        }

    }

    /**
     * 核心方法执行前－－通知
     *
     * @param req
     * @param channelConfig
     * @throws ChannelValidateException
     */
    protected void beforNotifyProcess(REQ req, ChannelConfig channelConfig) throws ChannelValidateException {
        // TODO 空实现
    }

    /**
     * 放心方法执行后
     *
     * @param req
     * @param rep
     * @param channelConfig
     */
    protected void afterProcess(REQ req, REP rep, ChannelConfig channelConfig) {
        // TODO 空实现
    }
}

package com.dream.pay.channel.access.notify;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * 支付渠道服务
 * 
 * 网关支付回调
 * 
 * @author mengzhenbin
 *
 */
@Path("channel")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,
		MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_SVG_XML, MediaType.APPLICATION_XHTML_XML,
		MediaType.TEXT_XML, MediaType.TEXT_HTML })
public interface AcceptRefundNotifyFacade {
	/**
	 * 网关支付－－接受第三方通知
	 * 
	 */
	@POST
	@GET
	@Path("refundNotify/{bizLine}/{payType}")
	public void refundNotify(@PathParam("bizLine") String bizLine, @PathParam("payType") String payType,
                             @Context HttpServletRequest request);
}

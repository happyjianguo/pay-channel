<html>
 <head></head>
 <body>
 <form id="alipaysubmit" name="alipaysubmit" action="${wap_pay_url!''}?_input_charset=${_input_charset!''}" method="get">
   <input type="hidden" name="_input_charset" value="${_input_charset!''}" />
   <input type="hidden" name="subject" value="${subject!''}" />
   <input type="hidden" name="sign" value="${sign!''}" />
   <input type="hidden" name="notify_url" value="${notify_url!''}" />
   <input type="hidden" name="body" value="${body!''}" />
   <input type="hidden" name="payment_type" value="${payment_type!''}" />
   <input type="hidden" name="out_trade_no" value="${out_trade_no!''}"/>
   <input type="hidden" name="partner" value="${partner!''}"/>
   <input type="hidden" name="service" value="${service!''}"/>
   <input type="hidden" name="total_fee" value="${total_fee!''}"/>
   <input type="hidden" name="return_url" value="${return_url!''}"/>
   <input type="hidden" name="sign_type" value="${sign_type!''}"/>
   <input type="hidden" name="seller_id" value="${seller_id!''}"/>
   <input type="hidden" name="show_url" value="${show_url!''}"/>
   <input type="submit" value="确认" style="display:none;" />
 </form>
 <script>document.forms['alipaysubmit'].submit();</script> 
 </body>
</html>

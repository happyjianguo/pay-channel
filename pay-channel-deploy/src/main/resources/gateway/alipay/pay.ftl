<html><head><meta http-equiv="Content-Type" content="text/html; charset="gbk"/></head><body>
<form id="alipaysubmit" name="qform" action="${pay_url!''}" method="post">
    <input type="hidden" name="service" value="${service!''}"/>
  	<input type="hidden" name="partner" value="${partner!''}"/>
  	<input type="hidden" name="seller_email" value="${seller_email!''}"/>
    <input type="hidden" name="_input_charset" value="${_input_charset!''}"/>
    <input type="hidden" name="payment_type" value="${payment_type!''}"/>
    <input type="hidden" name="notify_url" value="${notify_url!''}"/>
    <input type="hidden" name="show_url" value="${show_url!''}"/>
    <input type="hidden" name="return_url" value="${return_url!''}"/>
    <input type="hidden" name="out_trade_no" value="${out_trade_no!''}"/>
    <input type="hidden" name="subject" value="${subject!''}"/>
    <input type="hidden" name="total_fee" value="${total_fee!''}"/>
    <input type="hidden" name="body" value="${body!''}"/>
    <input type="hidden" name="sign_type" value="${sign_type!''}"/>
    <input type="hidden" name="sign" value="${sign!''}"/>
    <input type="hidden" name="exter_invoke_ip" value="${exter_invoke_ip!''}"/>
    <input type="hidden" name="paymethod" value="${paymethod!''}"/>
    <input type="hidden" name="defaultbank" value="${defaultbank!''}"/>
    <input type="hidden" name="qr_pay_mode" value="${qr_pay_mode!''}"/>
    <input type="hidden" name="qrcode_width" value="${qrcode_width!''}"/>
    <input type="hidden" name="hb_fq_param" value="${hb_fq_param!''}"/>
    <input type="submit" value="提交" style="display:none;">
</form>
<script>document.forms['qform'].submit();</script>
</body></html>
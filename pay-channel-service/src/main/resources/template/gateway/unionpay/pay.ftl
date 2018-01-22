<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body>

<form name="qform" method="post" action="${postUrl!''}">
    <input type="hidden" name="version" value="${version!''}"/>
    <input type="hidden" name="encoding" value="${encoding!''}"/>
    <input type="hidden" name="certId" value="${certId!''}"/>
    <input type="hidden" name="signMethod" value="${signMethod!''}"/>
    <input type="hidden" name="signature" value="${signature!''}"/>
    <input type="hidden" name="txnType" value="${txnType!''}"/>
    <input type="hidden" name="txnSubType" value="${txnSubType!''}"/>
    <input type="hidden" name="bizType" value="${bizType!''}"/>
    <input type="hidden" name="channelType" value="${channelType!''}"/>
    <input type="hidden" name="accessType" value="${accessType!''}"/>
    <input type="hidden" name="merId" value="${merId!''}"/>
    <input type="hidden" name="frontUrl" value="${frontUrl!''}"/>
    <input type="hidden" name="backUrl" value="${backUrl!''}"/>
    <input type="hidden" name="orderId" value="${orderId!''}"/>
    <input type="hidden" name="currencyCode" value="${currencyCode!''}"/>
    <input type="hidden" name="txnAmt" value="${txnAmt!''}"/>
    <input type="hidden" name="txnTime" value="${txnTime!''}"/>
</form>

<script>document.qform.submit();</script>
</body></html>
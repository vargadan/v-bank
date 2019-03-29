<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title" value="Now can make a transfer from ${param.fromAccount}"/>
<%@ include file="header.jsp" %>
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
    <c:if test="${not empty _csrf}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </c:if>
    <input type="submit" value="send"/>
</form>
<%@ include file="footer.jsp" %>
</body>
</html>
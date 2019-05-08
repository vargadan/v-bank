<!doctype html>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:page>
<%--<jsp:include page="header.jsp">--%>
<%--    <jsp:param name="title" value="Please make a transfer from ${param.fromAccount}"/>--%>
<%--</jsp:include>--%>
    <div class="container">
        <form action="/doTransfer" method="post">
            <input id="fromAccount" name="fromAccount" type="hidden" value="${param.fromAccount}">
            <fieldset>
                <p>
                    <label for="toAccount">To Account:</label>
                    <input id="toAccount" name="toAccount" type="text" value="${param.toAccount}">
                </p>
                <p>
                    <label for="amount">Amount:</label>
                    <input id="amount" name="amount" type="text" value="${param.toAccount}">
                </p>
                <p>
                    <label for="currency">Currency:</label>
                    <input id="currency" name="currency" type="text" value="${param.toAccount != null ? param.toAccount : 'CHF'}">
                </p>
                <p>
                    <label for="note">Note:</label>
                    <input id="note" name="note" type="text" value="${param.toAccount}">
                </p>
                <c:if test="${not empty csrfProtectionToken}">
                    <input type="hidden" name="_csrf" value="${csrfProtectionToken}"/>
                </c:if>
                <p>
                    <input type="submit" value="send" class="button"/>
                </p>
            </fieldset>
        </form>
    </div>
    <div class="container">
        <a href="/">back</a>
    </div>
</t:page>
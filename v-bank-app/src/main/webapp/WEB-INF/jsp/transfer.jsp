<!doctype html>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<<<<<<< HEAD
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<t:page>
<%--<jsp:include page="header.jsp">--%>
<%--    <jsp:param name="title" value="Please make a transfer from ${param.fromAccount}"/>--%>
<%--</jsp:include>--%>
=======
<t:page title="Make a transfer from ${param.fromAccountNo}">
>>>>>>> 5678176... titles
    <div class="container">
        <form action="/doTransfer" method="post">
            <input id="fromAccount" name="fromAccountNo" type="hidden" value="${param.fromAccountNo}">
            <fieldset>
                <p>
                    <label for="toAccount">To Account:</label>
                    <input id="toAccount" name="toAccountNo" type="text" value="">
                </p>
                <p>
                    <label for="amount">Amount:</label>
                    <input id="amount" name="amount" type="text" value="">
                </p>
                <p>
                    <label for="currency">Currency:</label>
                    <input id="currency" name="currency" type="text" value="CHF">
                </p>
                <p>
                    <label for="note">Note:</label>
                    <input id="note" name="note" type="text" value="">
                </p>
                <p>
                    <input type="submit" value="send" class="button"/>
                </p>
                <sec:csrfInput />
            </fieldset>
        </form>
    </div>
    <div class="container">
        <a href="/">back</a>
    </div>
</t:page>